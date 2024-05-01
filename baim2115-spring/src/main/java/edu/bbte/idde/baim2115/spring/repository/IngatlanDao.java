package edu.bbte.idde.baim2115.spring.repository;

import edu.bbte.idde.baim2115.spring.model.Ingatlan;

import java.util.Collection;

public interface IngatlanDao extends DAO<Ingatlan> {
    Collection<Ingatlan> findByTermekAra(Integer price); // opotional
}
