package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.interview.dto.InterviewDTO;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.offer.entity.Offer;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class InterviewMapperTest {

    private final InterviewMapper interviewMapper = Mappers.getMapper(InterviewMapper.class);

    private CandidateRepository candidateRepository;
    private OfferRepository offerRepository;
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
        assertEquals(interview.getScheduledAt() , dto.scheduledAt());
        assertEquals(interview.getComment() , dto.comment());
        assertEquals(candidateId, dto.candidateId());
        assertEquals(offerId, dto.offerId());
    }



}
