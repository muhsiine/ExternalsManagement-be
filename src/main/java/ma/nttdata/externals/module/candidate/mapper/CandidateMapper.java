package ma.nttdata.externals.module.candidate.mapper;

import ma.nttdata.externals.module.candidate.dto.*;
import ma.nttdata.externals.module.candidate.entity.*;
import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import ma.nttdata.externals.module.cv.entity.CvFile;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // Use Spring component model
public interface CandidateMapper {

    CandidateMapper INSTANCE = Mappers.getMapper(CandidateMapper.class);

    // Entity to DTO
    @Mapping(source = "languages", target = "naturalLanguages")
    CandidateDTO candidateToCandidateDTO(Candidate candidate);
    ContactDTO contactToContactDTO(Contact contact);
    ExperienceDTO experienceToExperienceDTO(Experience experience);
    SkillDTO skillToSkillDTO(Skill skill);
    EducationDTO educationToEducationDTO(Education education);
    @Mapping(target = "promptCode", ignore = true)
    @Mapping(target = "b64EFile", ignore = true)
    @Mapping(target = "mimeType", source = "fileType")
    @Mapping(target = "extractedData", ignore = true)
    CvFileDTO cvFileToCvFileDTO(CvFile cvFile);
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "country.cities", ignore = true)
    @Mapping(target = "city.country.cities", ignore = true)
    AddressDTO addressToAddressDTO(Address address);
    @Mapping(target = "candidateDTO", ignore = true)
    @Mapping(target = "isNative", ignore = true)
    LanguageDTO languageToLanguageDTO(Language language);

    // DTO to Entity
    @Mapping(source = "naturalLanguages", target = "languages")
    Candidate candidateDTOToCandidate(CandidateDTO candidateDTO);
    @Mapping(target = "candidate", ignore = true)
    Contact contactDTOToContact(ContactDTO contactDTO);
    @Mapping(target = "candidate", ignore = true)
    Experience experienceDTOToExperience(ExperienceDTO experienceDTO);
    @Mapping(target = "candidate", ignore = true)
    Skill skillDTOToSkill(SkillDTO skillDTO);
    @Mapping(target = "candidate", ignore = true)
    Education educationDTOToEducation(EducationDTO educationDTO);
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fileType", source = "mimeType")
    @Mapping(target = "uploadedAt", ignore = true)
    CvFile cvFileDTOToCvFile(CvFileDTO cvFileDTO);
    @Mapping(target = "candidate", ignore = true)
    Address addressDTOToAddress(AddressDTO addressDTO);
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "native", source = "isNative")
    Language languageDTOToLanguage(LanguageDTO languageDTO);

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
        if (candidate.getLanguages() != null) {
            candidate.getLanguages().forEach(language -> language.setCandidate(candidate));
        }

        Address address = candidate.getAddress();
        if (address != null) {
            address.setCandidate(candidate);
            if(address.getCity() != null){
                address.getCity().setCountry(address.getCountry());
            }
        }
    }

}
