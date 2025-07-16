package ma.nttdata.externals.module.prompt.mapper;

import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import ma.nttdata.externals.module.prompt.entity.Prompt;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PromptMapper {

    PromptMapper INSTANCE = Mappers.getMapper(PromptMapper.class);

    PromptDTO toDTO(Prompt prompt);

    Prompt toEntity(PromptDTO promptDTO);

    Prompt updatePromptFromDto(PromptDTO promptDTO, @MappingTarget Prompt prompt);
}