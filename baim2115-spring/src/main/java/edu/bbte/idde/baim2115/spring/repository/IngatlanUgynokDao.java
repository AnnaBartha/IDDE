package edu.bbte.idde.baim2115.spring.repository;

import edu.bbte.idde.baim2115.spring.model.IngatlanUgynok;

import java.util.Collection;

public interface IngatlanUgynokDao extends DAO<IngatlanUgynok> {
    Collection<IngatlanUgynok> findByTelefonszam(String phoneNr);
}
