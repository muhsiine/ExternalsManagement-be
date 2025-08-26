package ma.nttdata.externals.module.offer.dto;

import ma.nttdata.externals.module.candidate.dto.*;
import ma.nttdata.externals.module.candidate.entity.*;


import java.util.List;
import java.util.UUID;

public record OfferCandidatesDTO(
        UUID id,
        String fullName,
        String mainTech,
        Integer yearsOfExperience,
        List<SkillDTO> skills,
        List<LanguageDTO> languages,
        List<EducationDTO> educations,
        List<ContactDTO> contacts,
        List<ExperienceDTO> experiences,
        AddressDTO address
) {}