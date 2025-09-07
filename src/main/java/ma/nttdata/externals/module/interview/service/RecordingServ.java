package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.CreateRecordingRequestDTO;
import ma.nttdata.externals.module.interview.dto.MergeRecordingsRequestDTO;
import ma.nttdata.externals.module.interview.dto.RecordingDTO;
import ma.nttdata.externals.module.interview.dto.RecordingUploadRequestDTO;
import ma.nttdata.externals.module.interview.entity.Recording;

import java.util.List;
import java.util.UUID;

public interface RecordingServ {
    Recording createRecording(CreateRecordingRequestDTO request);

    RecordingDTO createRecordingAndReturnDTO(CreateRecordingRequestDTO request);

    RecordingDTO findRecordingById(UUID id);

    RecordingDTO findRecordingByInterviewId(UUID interviewId);

    List<RecordingDTO> findAllRecordings();

    List<RecordingDTO> findAllRecordingsByOfferId(UUID offerId);

    RecordingDTO updateRecording(RecordingDTO recordingDTO);

    void deleteRecordingById(UUID id);

    void deleteRecording(RecordingDTO recordDTO);

    String uploadChunk(RecordingUploadRequestDTO req);

    String mergeChunksAndCreateRecording(MergeRecordingsRequestDTO req);
}
