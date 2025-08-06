package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.InterviewDTO;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.offer.entity.Offer;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class InterviewMapper {

    @Autowired
    protected CandidateRepository candidateRepository;

    @Autowired
    protected OfferRepository offerRepository;

    // Entity to dto mapping
    @Mapping(target = "candidateId", source = "candidate.id")
    @Mapping(target = "offerId", source = "offer.id")
    public abstract InterviewDTO toDto(Interview interview);

    // DTO to Entity mapping
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "offer", ignore = true)
    public abstract Interview toEntity(InterviewDTO interviewDTO);

    // List mappings
    public abstract List<InterviewDTO> toDtoList(List<Interview> interviews);

    public abstract List<Interview> toEntityList(List<InterviewDTO> interviewDTOs);

    // upadte existed interview
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "offer", ignore = true)
    @Mapping(target = "evaluations" , ignore = true)
    @Mapping(target = "questions" , ignore = true)

    public abstract void updateInterviewFromDto(InterviewDTO dto, @MappingTarget Interview entity);

    // AfterMapping to set candidate and offer manually using repository
    @AfterMapping
    protected void afterToEntity(InterviewDTO dto, @MappingTarget Interview entity) {
        if (dto.candidateId() != null) {
            Candidate candidate = candidateRepository.findById(dto.candidateId())
                    .orElseThrow(() -> new RuntimeException("Candidate not found with ID: " + dto.candidateId()));
            entity.setCandidate(candidate);
        }

        if (dto.offerId() != null) {
            Offer offer = offerRepository.findById(dto.offerId())
                    .orElseThrow(() -> new RuntimeException("Offer not found with ID: " + dto.offerId()));
            entity.setOffer(offer);
        }
    }

}
