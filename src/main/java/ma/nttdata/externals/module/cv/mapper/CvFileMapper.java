package ma.nttdata.externals.module.cv.mapper;

import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import ma.nttdata.externals.module.cv.entity.CvFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper
public interface CvFileMapper {

    CvFileMapper INSTANCE = Mappers.getMapper(CvFileMapper.class);

    @Mappings({
            @Mapping(target = "candidate", ignore = true), // Candidate is likely assigned separately
            @Mapping(target = "fileType", source = "mimeType")
    })
    CvFile toEntity(CvFileDTO dto);

    @Mappings({
            @Mapping(target = "promptCode", ignore = true),
            @Mapping(target = "b64EFile", ignore = true),
            @Mapping(target = "mimeType", source = "fileType")
    })
    CvFileDTO toDto(CvFile entity);
}
