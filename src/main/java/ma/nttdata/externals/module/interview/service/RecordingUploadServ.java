package ma.nttdata.externals.module.interview.service;

import java.util.List;

public interface RecordingUploadServ {

    String uploadChunk(String interviewId, int chunkSequence, byte[] audioData);

    void createFolder(String folderName);

    String extractSiteName(String siteUrl);

    String uploadSmallFile(String filePath, byte[] data) throws Exception;

    String uploadLargeFile(String filePath, byte[] data) throws Exception;

    String mergeChunks(String interviewId);

    byte[] mergeBytes(List<byte[]> chunks);
}
