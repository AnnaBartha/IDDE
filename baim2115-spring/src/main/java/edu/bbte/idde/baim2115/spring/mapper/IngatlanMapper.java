package edu.bbte.idde.baim2115.spring.mapper;

import edu.bbte.idde.baim2115.spring.controller.dto.IngatlanInDto;
import edu.bbte.idde.baim2115.spring.controller.dto.IngatlanOutDto;
import edu.bbte.idde.baim2115.spring.model.Ingatlan;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

@Mapper(componentModel = "spring")
public abstract class IngatlanMapper {

    @Mapping(target = "id", ignore = true)
    public abstract Ingatlan ingatlanFromDto(IngatlanInDto ingatlanInDto);

    public abstract IngatlanOutDto dtoFromIngatlan(Ingatlan ingatlan);

    @IterableMapping(elementTargetType = IngatlanOutDto.class)
    public abstract Collection<IngatlanOutDto> dtosFromIngatlans(Collection<Ingatlan> ingatlans);
}
