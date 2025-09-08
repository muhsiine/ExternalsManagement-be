package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.RecordingFileNamePlaceholdersDTO;

import java.util.List;

public interface RecordingUploadServ {

    void uploadChunk(String interviewId, int chunkSequence, byte[] audioData, RecordingFileNamePlaceholdersDTO placeholders) ;

    void createFolder(String folderName);

    String extractSiteName(String siteUrl);

    String uploadSmallFile(String filePath, byte[] data) throws Exception;

    String uploadLargeFile(String filePath, byte[] data) throws Exception;

    String mergeChunks(String interviewId, byte[] lastChunk, RecordingFileNamePlaceholdersDTO placeholders) ;

    byte[] mergeBytes(List<byte[]> chunks);
}
