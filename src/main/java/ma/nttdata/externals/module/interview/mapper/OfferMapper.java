package ma.nttdata.externals.module.interview.mapper;

import ma.nttdata.externals.module.interview.dto.OfferDTO;
import ma.nttdata.externals.module.interview.entity.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    OfferMapper INSTANCE = Mappers.getMapper(OfferMapper.class);

    // Entity to DTO
    @Mapping(target = "titre", source = "titre")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "department", source = "department")
    OfferDTO toDto(Offer offer);

    // DTO to Entity
    @Mapping(target = "titre", source = "titre")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "department", source = "department")
    Offer toEntity(OfferDTO offerDTO);

    List<OfferDTO> toDtoList(List<Offer> offers);

    List<Offer> toEntityList(List<OfferDTO> offerDTOs);
}