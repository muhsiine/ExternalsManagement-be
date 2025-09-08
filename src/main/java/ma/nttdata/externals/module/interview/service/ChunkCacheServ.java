package ma.nttdata.externals.module.interview.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;


public interface ChunkCacheServ {

    void cacheChunk(String interviewId, int chunkSequence, byte[] audioData);

    List<byte[]> getAllCachedChunks(String interviewId);

    void clearCachedChunks(String interviewId);

    boolean hasChunks(String interviewId);

    int getChunkCount(String interviewId);

    long getTotalSize(String interviewId);


}
