package edu.bbte.idde.baim2115.spring.model;

import jakarta.persistence.*;
import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "jpaIngatlan")
public class Ingatlan extends BaseEntity {
    private String orszag;
    private String varos;
    private Integer negyzetmeter;
    @Column(name = "termekAra")
    private Integer termekAra;
    @Column(name = "tulajNeve")
    private String tulajNeve;
    private String elerhetoseg;

    @ManyToOne
    @JoinColumn(name = "ingatlan_ugynok_id")
    @ToString.Exclude
    private IngatlanUgynok ingatlanUgynok;
}
