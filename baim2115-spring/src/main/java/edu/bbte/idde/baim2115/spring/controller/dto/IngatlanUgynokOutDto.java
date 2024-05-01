package edu.bbte.idde.baim2115.spring.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class IngatlanUgynokOutDto {
    private Long id;
    private String email;
    private String telefonszam;
    // private List<Ingatlan> ingatlanok;
    private List<IngatlanOutDto> ingatlanok;
}
