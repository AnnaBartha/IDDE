package edu.bbte.idde.baim2115.spring.mapper;

import edu.bbte.idde.baim2115.spring.controller.dto.IngatlanUgynokInDto;
import edu.bbte.idde.baim2115.spring.controller.dto.IngatlanUgynokOutDto;
import edu.bbte.idde.baim2115.spring.model.IngatlanUgynok;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

@Mapper(componentModel = "spring")
public abstract class IngatlanUgynokMapper {

    @Mapping(target = "id", ignore = true)
    public abstract IngatlanUgynok ingatlanUgynokFromDto(IngatlanUgynokInDto ingatlanUgynokInDto);

    public abstract IngatlanUgynokOutDto dtoFromIngatlanUgynok(IngatlanUgynok ingatlanUgynok);

    @IterableMapping(elementTargetType = IngatlanUgynokOutDto.class)
    public abstract Collection<IngatlanUgynokOutDto> dtosFromIngatlanUgynoks(
            Collection<IngatlanUgynok> ingatlanUgynoks);
}
