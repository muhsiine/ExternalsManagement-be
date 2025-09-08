package ma.nttdata.externals.module.interview.service.impl;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.tasks.LargeFileUploadResult;
import com.microsoft.graph.tasks.LargeFileUploadTask;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.config.SharePointConfig;
import ma.nttdata.externals.module.interview.dto.RecordingFileNamePlaceholdersDTO;
import ma.nttdata.externals.module.interview.service.ChunkCacheServ;
import ma.nttdata.externals.module.interview.service.RecordingUploadServ;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordingUploadServImpl implements RecordingUploadServ {

    private final SharePointConfig sharePointConfig;
    private GraphServiceClient graphClient;
    private final ChunkCacheServ chunkCacheServ;


    private GraphServiceClient getGraphClient() {
        if(graphClient == null) {
            ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                    .clientId(sharePointConfig.getClientId())
                    .clientSecret(sharePointConfig.getClientSecret())
                    .tenantId(sharePointConfig.getTenantId())
                    .build();

            TokenCredentialAuthProvider authProvider =
                    new TokenCredentialAuthProvider(Collections.
                            singletonList(sharePointConfig.getScope()),
                            credential);
            graphClient = GraphServiceClient.builder()
                    .authenticationProvider(authProvider)
                    .buildClient();
        }
        return graphClient;
    }

    @Override
    public void uploadChunk(String interviewId, int chunkSequence, byte[] audioData, RecordingFileNamePlaceholdersDTO placeholders) {
        try{
            String monthFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String folderPath = sharePointConfig.getRecordingsFolderName()+monthFolder;
            createFolder(folderPath);
            chunkCacheServ.cacheChunk(interviewId, chunkSequence, audioData);

        }catch (Exception e){
            throw new RuntimeException("Failed to upload chunk: " + e.getMessage(), e);
        }
    }

    @Override
    public void createFolder(String folderName){
        try{

            String siteName = extractSiteName(sharePointConfig.getSiteUrl());
            String parentPath = folderName.substring(0, folderName.lastIndexOf("/" ));
            DriveItem newFolder = new DriveItem();
            newFolder.name = folderName.substring(folderName.lastIndexOf("/") + 1);
            newFolder.folder = new Folder();

            if(parentPath.isEmpty()){
                getGraphClient()
                        .sites(siteName)
                        .drives()
                        .byId(sharePointConfig.getDocumentLibrary())
                        .root()
                        .children()
                        .buildRequest()
                        .post(newFolder);
            }else{
                getGraphClient()
                        .sites(siteName)
                        .drives()
                        .byId(sharePointConfig.getDocumentLibrary())
                        .root()
                        .itemWithPath(parentPath)
                        .children()
                        .buildRequest()
                        .post(newFolder);
            }
        }catch (Exception e){
            throw new RuntimeException("Failed to create folder: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractSiteName(String siteUrl){
        return siteUrl.replace("https://","")
                .replace("/sites/",":/sites/");
    }

    @Override
    public String uploadSmallFile(String filePath, byte[] data)   {
        String siteName = extractSiteName(sharePointConfig.getSiteUrl());

        DriveItem uploadedItem = getGraphClient()
                .sites(siteName)
                .drives()
                .byId(sharePointConfig.getDocumentLibrary())
                .root()
                .itemWithPath(filePath)
                .content()
                .buildRequest()
                .put(data);

        return uploadedItem.webUrl;
    }

    @Override
    public String uploadLargeFile(String filePath, byte[] data) throws Exception {
        String siteName = extractSiteName(sharePointConfig.getSiteUrl());
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

        try {
            DriveItemUploadableProperties uploadProps = new DriveItemUploadableProperties();
            uploadProps.name = fileName;


            DriveItemCreateUploadSessionParameterSet params =
                    DriveItemCreateUploadSessionParameterSet
                            .newBuilder()
                            .withItem(uploadProps)
                            .build();

            UploadSession uploadSession = getGraphClient()
                    .sites(siteName)
                    .drives()
                    .byId(sharePointConfig.getDocumentLibrary())
                    .root()
                    .itemWithPath(filePath)
                    .createUploadSession(params)
                    .buildRequest()
                    .post();
            LargeFileUploadTask<DriveItem> uploadTask = new LargeFileUploadTask<>(
                    uploadSession,
                    getGraphClient(),
                    new ByteArrayInputStream(data),
                    data.length,
                    DriveItem.class
            );

            LargeFileUploadResult<DriveItem> result = uploadTask.upload();

            if (result.responseBody != null && result.responseBody.id!=null && result.responseBody.webUrl!=null) {
                return result.responseBody.webUrl;
            } else {
                throw new RuntimeException("Upload failed - no response body returned");
            }

        } catch (Exception e) {
            throw new RuntimeException("Large file upload failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String mergeChunks(String interviewId, byte[] lastChunk, RecordingFileNamePlaceholdersDTO placeholders) {
        try {
            String monthFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String folderPath = sharePointConfig.getRecordingsFolderName() + monthFolder;
            String mergedFileName = sanitize(placeholders.CandidateName()) + "_" +
                    sanitize(placeholders.offerTitle()) + "_" +
                    placeholders.dayOfTheMonth() + "_" +
                    interviewId + ".webm";

            List<byte[]> chunkData = new ArrayList<>(chunkCacheServ.getAllCachedChunks(interviewId));

            if (lastChunk != null && (chunkData.isEmpty() || !Arrays.equals(chunkData.getLast(), lastChunk))) {
                chunkData.add(lastChunk);
            }

            byte[] mergedBytes = mergeBytes(chunkData);
            String uploadedUrl = uploadLargeFile(folderPath + "/" + mergedFileName, mergedBytes);
            chunkCacheServ.clearCachedChunks(interviewId);
            return uploadedUrl;
        } catch (Exception e) {
            throw new RuntimeException("Failed to merge chunks: " + e.getMessage(), e);
        }
    }


    @Override
    public byte[] mergeBytes(List<byte[]> chunks){
        int totalLength = chunks.stream().mapToInt(b -> b.length).sum();
        byte[] merged = new byte[totalLength];
        int pos = 0;
        for(byte[] chunk: chunks){
            System.arraycopy(chunk,0,merged,pos,chunk.length);
            pos+=chunk.length;
        }
        return merged;
    }

    private String sanitize(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("\\s+", "_");
    }
}
