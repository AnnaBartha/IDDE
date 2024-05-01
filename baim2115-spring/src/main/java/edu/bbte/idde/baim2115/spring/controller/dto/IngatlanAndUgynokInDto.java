package edu.bbte.idde.baim2115.spring.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IngatlanAndUgynokInDto {
    @NotNull
    private Long ingatlanId;
}
