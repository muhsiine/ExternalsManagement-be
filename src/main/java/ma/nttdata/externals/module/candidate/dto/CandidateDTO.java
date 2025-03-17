package ma.nttdata.externals.module.candidate.dto;


import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.cv.dto.CvFileDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CandidateDTO(
        UUID id,
        String fullName,
        LocalDate birthDate,
        int yearsOfExperience,
        GenderEnum gender,
        String mainTech,
        String summary,
        List<ContactDTO> contacts,
        List<ExperienceDTO> experiences,
        List<SkillDTO> skills,
        List<EducationDTO> educations,
        List<CvFileDTO> cvFiles,
        AddressDTO address,
        List<LanguageDTO> naturalLanguages

) {}
