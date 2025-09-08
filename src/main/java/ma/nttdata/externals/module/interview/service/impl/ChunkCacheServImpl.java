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
    private final Pattern chunkPattern = Pattern.compile("(.+)_chunk_(\\d+)\\.bin");

    @Override
    public void cacheChunk(String interviewId, int chunkSequence, byte[] audioData) {
        validateInputs(interviewId, chunkSequence, audioData);
        createCacheDirectory();

        String fileName = String.format("%s_chunk_%05d.bin", interviewId, chunkSequence);
        Path filePath = Paths.get(cacheDirectory, fileName);

        try {
            Files.write(filePath, audioData, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

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

            for (Path file : filesToDelete) {
                try {
                    long size = Files.size(file);
                    Files.delete(file);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete expired chunk : " + interviewId, e);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete expired chunks: " + interviewId, e);
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
        return fileName.startsWith(interviewId + "_chunk_") && fileName.endsWith(".bin");
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