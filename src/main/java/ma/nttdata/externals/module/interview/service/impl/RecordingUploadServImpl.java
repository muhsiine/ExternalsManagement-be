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
import ma.nttdata.externals.module.interview.service.RecordingUploadServ;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordingUploadServImpl implements RecordingUploadServ {

    private final SharePointConfig sharePointConfig;
    private GraphServiceClient graphClient;


    private GraphServiceClient getGraphClient() {
        if(graphClient == null) {
            ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                    .clientId(sharePointConfig.getClientId())
                    .clientSecret(sharePointConfig.getClientSecret())
                    .tenantId(sharePointConfig.getTenantId())
                    .build();

            TokenCredentialAuthProvider authProvider =
                    new TokenCredentialAuthProvider(Collections.
                            singletonList("https://graph.microsoft.com/.default"),
                            credential);
            graphClient = GraphServiceClient.builder()
                    .authenticationProvider(authProvider)
                    .buildClient();
        }
        return graphClient;
    }

    @Override
    public String uploadChunk(String interviewId, int chunkSequence, byte[] audioData) {
        try{
            String fileName = String.format("%s_chunk_%d.webm", interviewId, chunkSequence);
            String folderPath = sharePointConfig.getRecordingsFolderName()+"/interview_"+interviewId;
            createFolder(folderPath);

            if(audioData.length <4*1024*1024) {
                uploadSmallFile(folderPath+"/"+fileName,audioData);
            }else{
                uploadLargeFile(folderPath+"/"+fileName,audioData);
            }
        }catch (Exception e){
            throw new RuntimeException("Failed to upload chunk: " + e.getMessage(), e);
        }
        return "";
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
    public String uploadSmallFile(String filePath, byte[] data) throws Exception {
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

        return uploadedItem.id;
    }

    @Override
    public String uploadLargeFile(String filePath, byte[] data) throws Exception {
        String siteName = extractSiteName(sharePointConfig.getSiteUrl());
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

        try {
            // Create upload session
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

            if (result.responseBody != null && result.responseBody.id!=null) {
                return result.responseBody.id;
            } else {
                throw new RuntimeException("Upload failed - no response body returned");
            }

        } catch (Exception e) {
            throw new RuntimeException("Large file upload failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String mergeChunks(String interviewId) {
        try {
            String folderPath = sharePointConfig.getRecordingsFolderName() + "/interview_" + interviewId;
            String mergedFileName = interviewId + "_merged.webm";

            String siteName = extractSiteName(sharePointConfig.getSiteUrl());
            var children = getGraphClient()
                    .sites(siteName)
                    .drives()
                    .byId(sharePointConfig.getDocumentLibrary())
                    .root()
                    .itemWithPath(folderPath)
                    .children()
                    .buildRequest()
                    .get()
                    .getCurrentPage();

            children.sort((a, b) -> {
                int seqA = Integer.parseInt(a.name.replaceAll(".*_chunk_(\\d+)\\.webm", "$1"));
                int seqB = Integer.parseInt(b.name.replaceAll(".*_chunk_(\\d+)\\.webm", "$1"));
                return Integer.compare(seqA, seqB);
            });

            return null;

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



}
