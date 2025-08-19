package ma.nttdata.externals.module.offer.mapper;

import ma.nttdata.externals.module.candidate.entity.*;
import ma.nttdata.externals.module.offer.dto.OfferCandidatesDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OfferCandidateMapper {

    @Mapping(target = "skills", source = "skills")
    @Mapping(target = "languages", source = "languages")
    @Mapping(target = "educations", source = "educations")
    @Mapping(target = "contacts", source = "contacts")
    @Mapping(target = "experiences", source = "experiences")
    @Mapping(target = "address", source = "address")
    OfferCandidatesDTO toOfferCandidatesDTO(Candidate candidate);

    // If you ever want to simplify into string lists (like skill names):
    // default List<String> mapSkills(List<Skill> skills) {
    //     return skills == null ? List.of() :
    //         skills.stream().map(Skill::getName).toList();
    // }
}
