package edu.bbte.idde.baim2115.backend.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ingatlan extends BaseEntity {
    private String orszag;
    private String varos;
    private Integer negyzetmeter;
    private Integer termekAra;

    private String tulajNeve;

    private String elerhetoseg;

}
