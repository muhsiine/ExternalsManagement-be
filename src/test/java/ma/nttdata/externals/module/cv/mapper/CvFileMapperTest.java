package ma.nttdata.externals.module.cv.mapper;

import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import ma.nttdata.externals.module.cv.entity.CvFile;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CvFileMapperTest {

    private final CvFileMapper cvFileMapper = Mappers.getMapper(CvFileMapper.class);

    @Test
    void testToEntity() {
        // Create a DTO
        CvFileDTO dto = new CvFileDTO(
                "prompt123",
                "base64data",
                "application/pdf",
                "extracted data",
                "path/to/file.pdf"
        );

        // Map to entity
        CvFile entity = cvFileMapper.toEntity(dto);

        // Verify mapping
        assertNotNull(entity);
        assertEquals(dto.filePath(), entity.getFilePath());
        assertEquals(dto.mimeType(), entity.getFileType());

        // Verify ignored fields
        assertNull(entity.getId());
        assertNull(entity.getCandidate());
        // uploadedAt is initialized with a default value in the entity
        assertNotNull(entity.getUploadedAt());
    }

    @Test
    void testToDto() {
        // Create an entity
        CvFile entity = new CvFile();
        entity.setId(UUID.randomUUID());
        entity.setFilePath("path/to/file.pdf");
        entity.setFileType("application/pdf");
        entity.setUploadedAt(LocalDateTime.now());

        // Map to DTO
        CvFileDTO dto = cvFileMapper.toDto(entity);

        // Verify mapping
        assertNotNull(dto);
        assertEquals(entity.getFilePath(), dto.filePath());
        assertEquals(entity.getFileType(), dto.mimeType());

        // Verify ignored fields
        assertNull(dto.promptCode());
        assertNull(dto.b64EFile());
        assertNull(dto.extractedData());
    }

    @Test
    void testRoundTrip() {
        // Create a DTO
        CvFileDTO originalDto = new CvFileDTO(
                "prompt123",
                "base64data",
                "application/pdf",
                "extracted data",
                "path/to/file.pdf"
        );

        // Map to entity
        CvFile entity = cvFileMapper.toEntity(originalDto);

        // Set fields that would normally be set by the persistence layer
        entity.setId(UUID.randomUUID());
        entity.setUploadedAt(LocalDateTime.now());

        // Map back to DTO
        CvFileDTO roundTripDto = cvFileMapper.toDto(entity);

        // Verify mapping
        assertEquals(originalDto.filePath(), roundTripDto.filePath());
        assertEquals(originalDto.mimeType(), roundTripDto.mimeType());

        // Ignored fields should not be preserved
        assertNull(roundTripDto.promptCode());
        assertNull(roundTripDto.b64EFile());
        assertNull(roundTripDto.extractedData());
    }
}
