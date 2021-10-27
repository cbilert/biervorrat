package com.biervorrat.mapper;

import com.biervorrat.dto.BierDTO;
import com.biervorrat.entity.Bier;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BierMapper {

    BierMapper INSTANCE = Mappers.getMapper(BierMapper.class);

    BierDTO toDTO(Bier bier);

    Bier toModel(BierDTO bierDTO);
}
