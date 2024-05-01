package edu.bbte.idde.baim2115.spring.controller.dto;

import lombok.Data;

@Data
public class IngatlanOutDto {
    private Long id;
    private String orszag;
    private String varos;
    private Integer negyzetmeter;
    private Integer termekAra;
    private String tulajNeve;
    private String elerhetoseg;
}
