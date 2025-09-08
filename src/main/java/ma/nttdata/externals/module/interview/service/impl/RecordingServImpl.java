package ma.nttdata.externals.module.interview.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.*;
import ma.nttdata.externals.module.interview.entity.Recording;
import ma.nttdata.externals.module.interview.mapper.RecordingMapper;
import ma.nttdata.externals.module.interview.repository.RecordingRepository;
import ma.nttdata.externals.module.interview.service.InterviewServ;
import ma.nttdata.externals.module.interview.service.RecordingServ;
import ma.nttdata.externals.module.interview.service.RecordingUploadServ;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordingServImpl implements RecordingServ {

    private final RecordingRepository recordingRepository;
    private final RecordingMapper recordingMapper;
    private final RecordingUploadServ recordingUploadServ;
    private final InterviewServ interviewServ;

    @Override
    public Recording createRecording(CreateRecordingRequestDTO request) {
        Recording recording = recordingMapper.fromCreateRecordRequestDtoToEntity(request);
        return recordingRepository.save(recording);
    }

    @Override
    public RecordingDTO createRecordingAndReturnDTO(CreateRecordingRequestDTO request) {
        Recording recording = recordingMapper.fromCreateRecordRequestDtoToEntity(request);
        return recordingMapper.toDto(recordingRepository.save(recording));
    }

    @Override
    public RecordingDTO findRecordingById(UUID id) {
        Recording recording = recordingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recording",id));
        return recordingMapper.toDto(recording);
    }

    @Override
    public RecordingDTO findRecordingByInterviewId(UUID interviewId) {
        Recording recording = recordingRepository.findRecordByInterviewId(interviewId);
        return recordingMapper.toDto(recording);
    }

    @Override
    public List<RecordingDTO> findAllRecordings() {
        List<Recording> recordings = recordingRepository.findAll();
        return recordingMapper.toListDto(recordings);
    }

    @Override
    public List<RecordingDTO> findAllRecordingsByOfferId(UUID offerId) {
        List<Recording> recordings = recordingRepository.findAllRecordsByOfferId(offerId);
        return recordingMapper.toListDto(recordings);
    }

    @Override
    public RecordingDTO updateRecording(RecordingDTO recordingDTO) {
        Recording recording = recordingRepository.findById(recordingDTO.id())
                .orElseThrow(() -> new ResourceNotFoundException("Recording", recordingDTO.id()));

        if (recordingDTO.transcript() != null) recording.setTranscript(recordingDTO.transcript());
        if (recordingDTO.fileUrl() != null) recording.setFileUrl(recordingDTO.fileUrl());
        if (recordingDTO.recordedAt() != null) recording.setRecordedAt(recordingDTO.recordedAt());

        return recordingMapper.toDto(recordingRepository.save(recording));
    }

    @Override
    public void deleteRecordingById(UUID id) {
        if (!recordingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Record", id);
        }
        recordingRepository.deleteById(id);
    }

    @Override
    public void deleteRecording(RecordingDTO record){
        UUID id = record.id();
        if (!recordingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Record", id);
        }
        recordingRepository.delete(recordingMapper.toEntity(record));
    }

    @Override
    public void uploadChunk(RecordingUploadRequestDTO req){

        RecordingFileNamePlaceholdersDTO  placeholders= interviewServ.getRecordingFileNamePlaceholdersByInterviewId(req.interviewId());
        try {
            recordingUploadServ.uploadChunk(req.interviewId().toString(), req.sequence(), req.chunk().getBytes(),placeholders);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String mergeChunksAndCreateRecording(MergeRecordingsRequestDTO req) {
        String fullRecordingUrl = null;
        RecordingFileNamePlaceholdersDTO  placeholders= interviewServ.getRecordingFileNamePlaceholdersByInterviewId(req.interviewId());
        try {
            fullRecordingUrl = this.recordingUploadServ.mergeChunks(req.interviewId().toString(),req.chunk().getBytes(),placeholders);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Recording recording = new Recording();
        recording.setTranscript(req.transcript());
        recording.setFileUrl(fullRecordingUrl);
        recording.setRecordedAt(LocalDateTime.now());

        interviewServ.setRecordingForInterview(req.interviewId(), recording);

        return fullRecordingUrl;
    }
}
