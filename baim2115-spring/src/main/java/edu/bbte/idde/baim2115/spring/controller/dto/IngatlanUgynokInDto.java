package edu.bbte.idde.baim2115.spring.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class IngatlanUgynokInDto {
    @NotNull
    @Length(max = 100)
    private String email;
    @NotNull
    @Length(max = 15)
    private String telefonszam;
    //private List<Ingatlan> ingatlanok;
}
