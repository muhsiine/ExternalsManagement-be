package ma.nttdata.externals.module.interview.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.CreateRecordingRequestDTO;
import ma.nttdata.externals.module.interview.dto.RecordingDTO;
import ma.nttdata.externals.module.interview.dto.TranscriptFormattingRequestDTO;
import ma.nttdata.externals.module.interview.entity.Recording;
import ma.nttdata.externals.module.interview.mapper.RecordingMapper;
import ma.nttdata.externals.module.interview.repository.RecordingRepository;
import ma.nttdata.externals.module.interview.service.InterviewServ;
import ma.nttdata.externals.module.interview.service.RecordingServ;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordingServImpl implements RecordingServ {

    private final RecordingRepository recordingRepository;
    private final RecordingMapper recordingMapper;
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
    public String formatInterviewTranscript(List<TranscriptFormattingRequestDTO> request) {
        ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.writeValueAsString(request);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public RecordingDTO saveTranscriptAndCreateRecording(UUID interviewId, List<TranscriptFormattingRequestDTO> request) {

        Recording recording = new Recording();
        recording.setRecordedAt(LocalDateTime.now());
        recording.setTranscript(formatInterviewTranscript(request));

        interviewServ.setRecordingForInterview(interviewId, recording);
        return recordingMapper.toDto(recordingRepository.save(recording));
    }
}
