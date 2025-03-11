package ma.nttdata.externals.module.candidate.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CandidateDTO(
        UUID id,
        String fullName,
        LocalDate birthdate,
        String mainTech,
        String summary,
        LocalDateTime createdAt,
        List<ContactDTO> contacts,
        List<ExperienceDTO> experiences,
        List<SkillDTO> skills,
        List<EducationDTO> educations,
        List<CvFileDTO> cvFiles
) {}
