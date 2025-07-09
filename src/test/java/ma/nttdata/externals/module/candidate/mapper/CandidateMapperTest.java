package ma.nttdata.externals.module.candidate.mapper;

import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.constants.LanguageLevel;
import ma.nttdata.externals.module.candidate.constants.ProficiencyLevel;
import ma.nttdata.externals.module.candidate.dto.*;
import ma.nttdata.externals.module.candidate.entity.*;
import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import ma.nttdata.externals.module.cv.entity.CvFile;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CandidateMapperTest {

    private final CandidateMapper candidateMapper = Mappers.getMapper(CandidateMapper.class);

    @Test
    void testCandidateToCandidateDTO() {
        // Create a candidate entity with all necessary fields
        Candidate candidate = new Candidate();
        candidate.setId(UUID.randomUUID());
        candidate.setFullName("John Doe");
        candidate.setBirthDate(LocalDate.of(1990, 1, 1));
        candidate.setYearsOfExperience(5);
        candidate.setGender(GenderEnum.M);
        candidate.setMainTech("Java");
        candidate.setSummary("Experienced Java developer");

        // Map to DTO
        CandidateDTO candidateDTO = candidateMapper.candidateToCandidateDTO(candidate);

        // Verify mapping
        assertEquals(candidate.getId(), candidateDTO.id());
        assertEquals(candidate.getFullName(), candidateDTO.fullName());
        assertEquals(candidate.getBirthDate(), candidateDTO.birthDate());
        assertEquals(candidate.getYearsOfExperience(), candidateDTO.yearsOfExperience());
        assertEquals(candidate.getGender(), candidateDTO.gender());
        assertEquals(candidate.getMainTech(), candidateDTO.mainTech());
        assertEquals(candidate.getSummary(), candidateDTO.summary());
    }

    @Test
    void testCandidateDTOToCandidate() {
        // Create a candidate DTO with all necessary fields
        CandidateDTO candidateDTO = new CandidateDTO(
                UUID.randomUUID(),
                "Jane Smith",
                LocalDate.of(1985, 5, 15),
                8,
                GenderEnum.F,
                "Python",
                "Skilled Python developer",
                null, null, null, null, null, null, null
        );

        // Map to entity
        Candidate candidate = candidateMapper.candidateDTOToCandidate(candidateDTO);

        // Verify mapping
        assertEquals(candidateDTO.id(), candidate.getId());
        assertEquals(candidateDTO.fullName(), candidate.getFullName());
        assertEquals(candidateDTO.birthDate(), candidate.getBirthDate());
        assertEquals(candidateDTO.yearsOfExperience(), candidate.getYearsOfExperience());
        assertEquals(candidateDTO.gender(), candidate.getGender());
        assertEquals(candidateDTO.mainTech(), candidate.getMainTech());
        assertEquals(candidateDTO.summary(), candidate.getSummary());
    }

    @Test
    void testCandidateWithRelationshipsToCandidateDTO() {
        // Create a candidate entity with relationships
        Candidate candidate = new Candidate();
        candidate.setId(UUID.randomUUID());
        candidate.setFullName("John Doe");
        candidate.setBirthDate(LocalDate.of(1990, 1, 1));
        candidate.setYearsOfExperience(5);
        candidate.setGender(GenderEnum.M);
        candidate.setMainTech("Java");
        candidate.setSummary("Experienced Java developer");

        // Add contacts
        List<Contact> contacts = new ArrayList<>();
        Contact contact = new Contact();
        contact.setId(UUID.randomUUID());
        contact.setContactType("Email");
        contact.setContactValue("john.doe@example.com");
        contact.setCandidate(candidate);
        contacts.add(contact);
        candidate.setContacts(contacts);

        // Add experiences
        List<Experience> experiences = new ArrayList<>();
        Experience experience = new Experience();
        experience.setId(UUID.randomUUID());
        experience.setCompanyName("Tech Corp");
        experience.setPosition("Senior Developer");
        experience.setStartDate("2018-01-01");
        experience.setEndDate("2023-01-01");
        experience.setDescription("Worked on Java projects");
        experience.setCandidate(candidate);
        experiences.add(experience);
        candidate.setExperiences(experiences);

        // Add skills
        List<Skill> skills = new ArrayList<>();
        Skill skill = new Skill();
        skill.setId(UUID.randomUUID());
        skill.setSkillName("Spring Boot");
        skill.setProficiencyLevel(ProficiencyLevel.EXPERT);
        skill.setCandidate(candidate);
        skills.add(skill);
        candidate.setSkills(skills);

        // Map to DTO
        CandidateDTO candidateDTO = candidateMapper.candidateToCandidateDTO(candidate);

        // Verify mapping
        assertEquals(candidate.getId(), candidateDTO.id());
        assertEquals(candidate.getFullName(), candidateDTO.fullName());

        // Verify contacts
        assertNotNull(candidateDTO.contacts());
        assertEquals(1, candidateDTO.contacts().size());
        assertEquals(contact.getContactType(), candidateDTO.contacts().getFirst().contactType());
        assertEquals(contact.getContactValue(), candidateDTO.contacts().getFirst().contactValue());

        // Verify experiences
        assertNotNull(candidateDTO.experiences());
        assertEquals(1, candidateDTO.experiences().size());
        assertEquals(experience.getCompanyName(), candidateDTO.experiences().getFirst().companyName());
        assertEquals(experience.getPosition(), candidateDTO.experiences().getFirst().position());

        // Verify skills
        assertNotNull(candidateDTO.skills());
        assertEquals(1, candidateDTO.skills().size());
        assertEquals(skill.getSkillName(), candidateDTO.skills().getFirst().skillName());
    }

    @Test
    void testCandidateDTOWithRelationshipsToCandidate() {
        // Create DTOs for relationships
        ContactDTO contactDTO = new ContactDTO(UUID.randomUUID(), "Email", "jane.smith@example.com");
        ExperienceDTO experienceDTO = new ExperienceDTO(UUID.randomUUID(), "Data Corp", "Data Scientist", 
                "2015-01-01", "2020-01-01", "Data analysis");
        SkillDTO skillDTO = new SkillDTO(UUID.randomUUID(), "Machine Learning", ProficiencyLevel.EXPERT);

        List<ContactDTO> contacts = List.of(contactDTO);
        List<ExperienceDTO> experiences = List.of(experienceDTO);
        List<SkillDTO> skills = List.of(skillDTO);

        // Create a candidate DTO with relationships
        CandidateDTO candidateDTO = new CandidateDTO(
                UUID.randomUUID(),
                "Jane Smith",
                LocalDate.of(1985, 5, 15),
                8,
                GenderEnum.F,
                "Python",
                "Skilled Python developer",
                contacts, experiences, skills, null, null, null, null
        );

        // Map to entity
        Candidate candidate = candidateMapper.candidateDTOToCandidate(candidateDTO);

        // Verify mapping
        assertEquals(candidateDTO.id(), candidate.getId());
        assertEquals(candidateDTO.fullName(), candidate.getFullName());

        // Verify contacts
        assertNotNull(candidate.getContacts());
        assertEquals(1, candidate.getContacts().size());
        assertEquals(contactDTO.contactType(), candidate.getContacts().getFirst().getContactType());
        assertEquals(contactDTO.contactValue(), candidate.getContacts().getFirst().getContactValue());

        // Verify experiences
        assertNotNull(candidate.getExperiences());
        assertEquals(1, candidate.getExperiences().size());
        assertEquals(experienceDTO.companyName(), candidate.getExperiences().getFirst().getCompanyName());
        assertEquals(experienceDTO.position(), candidate.getExperiences().getFirst().getPosition());

        // Verify skills
        assertNotNull(candidate.getSkills());
        assertEquals(1, candidate.getSkills().size());
        assertEquals(skillDTO.skillName(), candidate.getSkills().getFirst().getSkillName());

        // Verify bidirectional relationships
        assertEquals(candidate, candidate.getContacts().getFirst().getCandidate());
        assertEquals(candidate, candidate.getExperiences().getFirst().getCandidate());
        assertEquals(candidate, candidate.getSkills().getFirst().getCandidate());
    }

    @Test
    void testLanguageMapping() {
        // Create a language entity
        Language language = new Language();
        language.setId(UUID.randomUUID());
        language.setLanguage("English");
        language.setLanguageInEnglish("English");
        language.setDescription("Fluent in English");
        language.setEnglishDescription("Fluent in English");
        language.setLevel(LanguageLevel.ADVANCED);
        language.setNative(true);

        // Map to DTO
        LanguageDTO languageDTO = candidateMapper.languageToLanguageDTO(language);

        // Verify mapping
        assertEquals(language.getId(), languageDTO.id());
        assertEquals(language.getLanguage(), languageDTO.language());
        assertEquals(language.getLevel(), languageDTO.level());
        // isNative is ignored in the mapping

        // Map back to entity
        LanguageDTO newLanguageDTO = new LanguageDTO(
                UUID.randomUUID(),
                null,
                "Fluent in English",
                "Fluent in English",
                null,
                "English",
                "English",
                LanguageLevel.ADVANCED,
                true
        );

        Language mappedLanguage = candidateMapper.languageDTOToLanguage(newLanguageDTO);

        // Verify mapping
        assertEquals(newLanguageDTO.id(), mappedLanguage.getId());
        assertEquals(newLanguageDTO.language(), mappedLanguage.getLanguage());
        assertEquals(newLanguageDTO.level(), mappedLanguage.getLevel());
        assertTrue(mappedLanguage.isNative());
    }

    @Test
    void testCvFileMapping() {
        // Create a CV file entity
        CvFile cvFile = new CvFile();
        cvFile.setId(UUID.randomUUID());
        cvFile.setFilePath("resume.pdf");
        cvFile.setFileType("application/pdf");
        cvFile.setUploadedAt(LocalDateTime.now());

        // Map to DTO
        CvFileDTO cvFileDTO = candidateMapper.cvFileToCvFileDTO(cvFile);

        // Verify mapping
        assertNotNull(cvFileDTO);
        assertEquals(cvFile.getFileType(), cvFileDTO.mimeType());
        assertEquals(cvFile.getFilePath(), cvFileDTO.filePath());
        // Ignored fields should be null
        assertNull(cvFileDTO.promptCode());
        assertNull(cvFileDTO.b64EFile());
        assertNull(cvFileDTO.extractedData());

        // Map back to entity
        CvFileDTO newCvFileDTO = new CvFileDTO(
                null,
                null,
                "application/pdf",
                null,
                "new-resume.pdf"
        );

        CvFile mappedCvFile = candidateMapper.cvFileDTOToCvFile(newCvFileDTO);

        // Verify mapping
        assertEquals(newCvFileDTO.filePath(), mappedCvFile.getFilePath());
        assertEquals(newCvFileDTO.mimeType(), mappedCvFile.getFileType());
        // id should be ignored
        assertNull(mappedCvFile.getId());
    }
}
