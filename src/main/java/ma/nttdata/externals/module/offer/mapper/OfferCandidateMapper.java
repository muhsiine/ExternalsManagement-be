package ma.nttdata.externals.module.offer.mapper;

import ma.nttdata.externals.module.candidate.entity.*;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.offer.dto.OfferCandidatesDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses= CandidateMapper.class)
public interface OfferCandidateMapper {

    @Mapping(target = "skills", source = "skills")
    @Mapping(target = "languages", source = "languages")
    @Mapping(target = "educations", source = "educations")
    @Mapping(target = "contacts", source = "contacts")
    @Mapping(target = "experiences", source = "experiences")

    OfferCandidatesDTO toOfferCandidatesDTO(Candidate candidate);


}
