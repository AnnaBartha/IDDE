package edu.bbte.idde.baim2115.spring.controller.dto;

import edu.bbte.idde.baim2115.spring.model.IngatlanUgynok;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class IngatlanInDto {
    @NotNull
    @Length(max = 50)
    private String orszag;
    @NotNull
    @Length(max = 50)
    private String varos;
    @NotNull
    @Positive
    private Integer negyzetmeter;
    @NotNull
    @Positive
    private Integer termekAra;
    @NotNull
    @Length(max = 100)
    private String tulajNeve;
    @NotNull
    @Length(max = 150)
    private String elerhetoseg;
    @NotNull
    private IngatlanUgynok ingatlanUgynok;
}
