package ma.nttdata.externals.module.interview.service.impl;


import ma.nttdata.externals.module.interview.service.ChunkCacheServ;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ChunkCacheServImpl implements ChunkCacheServ {

    @Value("${app.cache.directory:${java.io.tmpdir}/interview-chunks}")
    private String cacheDirectory;
    private final Pattern chunkPattern = Pattern.compile("(.+)_chunk_(\\d+)\\.webm");

    @Override
    public void cacheChunk(String interviewId, int chunkSequence, byte[] audioData) {
        validateInputs(interviewId, chunkSequence, audioData);
        createCacheDirectory();

        String fileName = String.format("%s_chunk_%05d.webm", interviewId, chunkSequence);
        Path filePath = Paths.get(cacheDirectory, fileName);

        try {
            Path tempFile = Files.createTempFile(Paths.get(cacheDirectory), "temp_", ".webm");
            Files.write(tempFile, audioData, StandardOpenOption.WRITE);
            Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException("Failed to cache chunk: " + fileName, e);
        }
    }

    @Override
    public List<byte[]> getAllCachedChunks(String interviewId) {
        validateInterviewId(interviewId);

        try {
            List<Path> chunkFiles = getChunkFiles(interviewId);

            if (chunkFiles.isEmpty()) {
                return Collections.emptyList();
            }

            List<byte[]> chunks = new ArrayList<>();

            for (Path chunkFile : chunkFiles) {
                byte[] data = Files.readAllBytes(chunkFile);
                chunks.add(data);
            }

            return chunks;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read cached chunks for interview: " + interviewId, e);
        }
    }

    @Override
    public void clearCachedChunks(String interviewId) {
        validateInterviewId(interviewId);

        try {
            List<Path> filesToDelete = Files.list(Paths.get(cacheDirectory))
                    .filter(path -> isChunkFile(path, interviewId))
                    .collect(Collectors.toList());

            int deletedCount = 0;
            long deletedSize = 0;

            for (Path file : filesToDelete) {
                try {
                    long size = Files.size(file);
                    Files.delete(file);
                    deletedCount++;
                    deletedSize += size;
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete expired chunk : " + interviewId, e);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete expired chunks: " + interviewId, e);
        }
    }



    @Override
    public boolean hasChunks(String interviewId) {
        validateInterviewId(interviewId);

        try {
            return Files.list(Paths.get(cacheDirectory))
                    .anyMatch(path -> isChunkFile(path, interviewId));
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public int getChunkCount(String interviewId) {
        validateInterviewId(interviewId);

        try {
            return (int) Files.list(Paths.get(cacheDirectory))
                    .filter(path -> isChunkFile(path, interviewId))
                    .count();
        } catch (IOException e) {
            return 0;
        }
    }

    @Override
    public long getTotalSize(String interviewId) {
        validateInterviewId(interviewId);

        try {
            return Files.list(Paths.get(cacheDirectory))
                    .filter(path -> isChunkFile(path, interviewId))
                    .mapToLong(path -> {
                        try {
                            return Files.size(path);
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .sum();
        } catch (IOException e) {
            return 0;
        }
    }


    private void createCacheDirectory() {
        try {
            Path cachePath = Paths.get(cacheDirectory);
            if (!Files.exists(cachePath)) {
                Files.createDirectories(cachePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create cache directory: " + cacheDirectory, e);
        }
    }


    private List<Path> getChunkFiles(String interviewId) throws IOException {
        return Files.list(Paths.get(cacheDirectory))
                .filter(path -> isChunkFile(path, interviewId))
                .sorted((a, b) -> {
                    int seqA = extractSequenceNumber(a.getFileName().toString());
                    int seqB = extractSequenceNumber(b.getFileName().toString());
                    return Integer.compare(seqA, seqB);
                })
                .collect(Collectors.toList());
    }

    private boolean isChunkFile(Path path, String interviewId) {
        String fileName = path.getFileName().toString();
        return fileName.startsWith(interviewId + "_chunk_") && fileName.endsWith(".webm");
    }


    private int extractSequenceNumber(String fileName) {
        Matcher matcher = chunkPattern.matcher(fileName);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(2));
        }
        throw new IllegalArgumentException("Invalid chunk file name: " + fileName);
    }

    private void validateInputs(String interviewId, int chunkSequence, byte[] audioData) {
        validateInterviewId(interviewId);

        if (chunkSequence < 0) {
            throw new IllegalArgumentException("Chunk sequence must be non-negative");
        }

        if (audioData == null || audioData.length == 0) {
            throw new IllegalArgumentException("Audio data cannot be null or empty");
        }
    }

    private void validateInterviewId(String interviewId) {
        if (interviewId == null || interviewId.trim().isEmpty()) {
            throw new IllegalArgumentException("Interview ID cannot be null or empty");
        }

        if (interviewId.contains("/") || interviewId.contains("\\") || interviewId.contains("..")) {
            throw new IllegalArgumentException("Interview ID contains invalid characters");
        }
    }
}