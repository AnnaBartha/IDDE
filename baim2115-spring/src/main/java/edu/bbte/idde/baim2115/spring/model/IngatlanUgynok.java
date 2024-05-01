package edu.bbte.idde.baim2115.spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "jpaIngatlanUgynok")
public class IngatlanUgynok extends BaseEntity {
    private String email;
    private String telefonszam;

    @OneToMany(mappedBy = "ingatlanUgynok", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST})
    private List<Ingatlan> ingatlanok;
}
