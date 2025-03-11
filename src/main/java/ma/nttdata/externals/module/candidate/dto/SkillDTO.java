package ma.nttdata.externals.module.candidate.dto;

import ma.nttdata.externals.module.candidate.constants.ProficiencyLevel;

import java.util.UUID;

public record SkillDTO(
        UUID id,
        String skillName,
        ProficiencyLevel proficiencyLevel
) {}
