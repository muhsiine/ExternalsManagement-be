package ma.nttdata.externals.module.offer.mapper;

import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.entity.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    OfferMapper INSTANCE = Mappers.getMapper(OfferMapper.class);

    // to dto
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "id", source = "id")
    OfferDTO toDto(Offer offer);


    // to entity
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "id", source = "id")
    Offer toEntity(OfferDTO offerDTO);

    List<OfferDTO> toDtoList(List<Offer> offers);

    List<Offer> toEntityList(List<OfferDTO> offerDTOs);
}