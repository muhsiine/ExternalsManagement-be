package ma.nttdata.externals.module.candidate.mapper;

import ma.nttdata.externals.module.candidate.dto.*;
import ma.nttdata.externals.module.candidate.entity.*;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // Use Spring component model
public interface CandidateMapper {

    CandidateMapper INSTANCE = Mappers.getMapper(CandidateMapper.class);

    // Entity to DTO
    CandidateDTO candidateToCandidateDTO(Candidate candidate);
    ContactDTO contactToContactDTO(Contact contact);
    ExperienceDTO experienceToExperienceDTO(Experience experience);
    SkillDTO skillToSkillDTO(Skill skill);
    EducationDTO educationToEducationDTO(Education education);
    @Mapping(target = "fileContent", ignore = true) // Ignore fileContent during DTO to Entity mapping
    CvFileDTO cvFileToCvFileDTO(CvFile cvFile);

    // DTO to Entity
    Candidate candidateDTOToCandidate(CandidateDTO candidateDTO);
    Contact contactDTOToContact(ContactDTO contactDTO);
    Experience experienceDTOToExperience(ExperienceDTO experienceDTO);
    Skill skillDTOToSkill(SkillDTO skillDTO);
    Education educationDTOToEducation(EducationDTO educationDTO);
    CvFile cvFileDTOToCvFile(CvFileDTO cvFileDTO);

    @AfterMapping
    default void setCandidateInRelatedEntities(CandidateDTO candidateDTO, @MappingTarget Candidate candidate) {
        if (candidate.getContacts() != null) {
            candidate.getContacts().forEach(contact -> contact.setCandidate(candidate));
        }
        if (candidate.getExperiences() != null) {
            candidate.getExperiences().forEach(experience -> experience.setCandidate(candidate));
        }
        if (candidate.getEducations() != null) {
            candidate.getEducations().forEach(education -> education.setCandidate(candidate));
        }
        if (candidate.getCvFiles() != null) {
            candidate.getCvFiles().forEach(cvFile -> cvFile.setCandidate(candidate));
        }
        if (candidate.getSkills() != null) {
            candidate.getSkills().forEach(skill -> skill.setCandidate(candidate));
        }
        // ... similarly for skills, educations, and cvFiles ...
    }

}