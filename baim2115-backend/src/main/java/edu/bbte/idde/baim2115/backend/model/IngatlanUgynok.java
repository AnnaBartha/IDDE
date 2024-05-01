package edu.bbte.idde.baim2115.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

// baseEntytiben levo id talal e
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngatlanUgynok extends BaseEntity {
    private String email;
    private String telefonszam;

    private List<Ingatlan> ingatlanok;
}
