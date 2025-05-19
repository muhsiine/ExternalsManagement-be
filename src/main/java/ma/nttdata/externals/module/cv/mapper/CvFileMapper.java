package ma.nttdata.externals.module.cv.mapper;

import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import ma.nttdata.externals.module.cv.entity.CvFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CvFileMapper {
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "fileType", source = "mimeType")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uploadedAt", ignore = true)
    CvFile toEntity(CvFileDTO dto);

    @Mapping(target = "promptCode", ignore = true)
    @Mapping(target = "b64EFile", ignore = true)
    @Mapping(target = "mimeType", source = "fileType")
    @Mapping(target = "extractedData", ignore = true)
    CvFileDTO toDto(CvFile entity);
}

