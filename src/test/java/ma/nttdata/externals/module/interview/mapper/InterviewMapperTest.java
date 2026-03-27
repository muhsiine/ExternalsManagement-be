package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.interview.dto.InterviewDTO;
import ma.nttdata.externals.module.interview.dto.InterviewListDTO;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.offer.entity.Offer;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
        interview.setScheduledAt(LocalDateTime.of(2025, 8, 3, 6, 0));
        interview.setComment("Excellent communication skills");
        interview.setCandidate(candidate);
        interview.setOffer(offer);

        // Optionally, set empty lists for evaluations and questions if not null by default
        interview.setEvaluations(List.of()); // Or mock if you have Evaluation entity
        interview.setQuestions(List.of());   // Or mock if you have Question entity

        InterviewDTO dto = interviewMapper.toDto(interview);

        assertNotNull(dto);
        assertEquals(interview.getId(), dto.id());
        assertEquals(interview.getStartTime(), dto.startTime());
        assertEquals(interview.getEndTime(), dto.endTime());
        assertEquals(interview.getDescription(), dto.description());
        assertEquals(interview.getLink(), dto.link());
        assertEquals(interview.getScheduledAt(), dto.scheduledAt());
        assertEquals(interview.getComment(), dto.comment());
        assertEquals(candidateId, dto.candidateId());
        assertEquals(offerId, dto.offerId());

        assertNotNull(dto.evaluations());
        assertTrue(dto.evaluations().isEmpty());

        assertNotNull(dto.questions());
        assertTrue(dto.questions().isEmpty());
    }


    @Test
    void fromInterviewToInterviewListDTO_should_return_valid_interviewListDTO(){

        Candidate candidate = new Candidate();
        candidate.setId(UUID.randomUUID());
        candidate.setFullName("hamid");
        candidate.setMainTech("React");
        Offer offer = new Offer();
        offer.setId(UUID.randomUUID());
        offer.setTitle("test");

        Interview interview = new Interview();
        interview.setId(UUID.randomUUID());
        interview.setStartTime(LocalDateTime.now());
        interview.setEndTime(LocalDateTime.now().plusHours(1));
        interview.setDescription("Interview for testing");
        interview.setLink("http://example.com/interview");
        interview.setScheduledAt(LocalDateTime.now().plusDays(1));
        interview.setComment("Initial comment");
        interview.setNumberOfQuestions(5);
        interview.setEstimatedDuration(60);
        interview.setCandidate(candidate);
        interview.setOffer(offer);

        InterviewListDTO res = interviewMapper.fromInterviewToInterviewListDTO(interview);

        assertThat(res).isNotNull();
        assertThat(res.id()).isEqualTo(interview.getId());
        assertThat(res.startTime()).isEqualTo(interview.getStartTime());
        assertThat(res.endTime()).isEqualTo(interview.getEndTime());
        assertThat(res.description()).isEqualTo(interview.getDescription());
        assertThat(res.link()).isEqualTo(interview.getLink());
        assertThat(res.scheduledAt()).isEqualTo(interview.getScheduledAt());
        assertThat(res.comment()).isEqualTo(interview.getComment());
        assertThat(res.candidateFullName()).isEqualTo(candidate.getFullName());
        assertThat(res.candidateMainTech()).isEqualTo(candidate.getMainTech());
        assertThat(res.offerTitle()).isEqualTo(offer.getTitle());

    }


}
