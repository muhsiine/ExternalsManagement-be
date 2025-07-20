package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.InterviewDTO;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.offer.entity.Offer;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InterviewMapperTest {

    private final InterviewMapper interviewMapper = Mappers.getMapper(InterviewMapper.class);

    @Test
    void testToDto() {
        UUID candidateId = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();

        Candidate candidate = new Candidate();
        candidate.setId(candidateId);

        Offer offer = new Offer();
        offer.setId(offerId);

        Interview interview = new Interview();
        interview.setId(UUID.randomUUID());
        interview.setStartTime(LocalDateTime.of(2025, 7, 20, 10, 0));
        interview.setEndTime(LocalDateTime.of(2025, 7, 20, 11, 0));
        interview.setDescription("Tech interview");
        interview.setLink("https://zoom.com/interview123");
        interview.setFeedback_general("Very good candidate");
        interview.setCandidate(candidate);
        interview.setOffer(offer);

        InterviewDTO dto = interviewMapper.toDto(interview);

        assertNotNull(dto);
        assertEquals(interview.getId(), dto.id());
        assertEquals(interview.getStartTime(), dto.startTime());
        assertEquals(interview.getEndTime(), dto.endTime());
        assertEquals(interview.getDescription(), dto.description());
        assertEquals(interview.getLink(), dto.link());
        assertEquals(interview.getFeedback_general(), dto.feedback_general());
        assertEquals(candidateId, dto.candidateId());
        assertEquals(offerId, dto.offerId());
    }

    @Test
    void testToEntity() {
        UUID interviewId = UUID.randomUUID();
        UUID candidateId = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();

        InterviewDTO dto = new InterviewDTO(
                interviewId,
                LocalDateTime.of(2025, 7, 20, 10, 0),
                LocalDateTime.of(2025, 7, 20, 11, 0),
                "Tech interview",
                "https://zoom.com/interview123",
                "Good candidate",
                candidateId,
                offerId
        );

        Interview interview = interviewMapper.toEntity(dto);

        assertNotNull(interview);
        assertEquals(dto.id(), interview.getId());
        assertEquals(dto.startTime(), interview.getStartTime());
        assertEquals(dto.endTime(), interview.getEndTime());
        assertEquals(dto.description(), interview.getDescription());
        assertEquals(dto.link(), interview.getLink());
        assertEquals(dto.feedback_general(), interview.getFeedback_general());

        // only id to set
        assertNotNull(interview.getCandidate());
        assertEquals(candidateId, interview.getCandidate().getId());

        assertNotNull(interview.getOffer());
        assertEquals(offerId, interview.getOffer().getId());
    }
}
