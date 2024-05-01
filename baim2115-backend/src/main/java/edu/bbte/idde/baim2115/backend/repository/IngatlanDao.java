package edu.bbte.idde.baim2115.backend.repository;

import edu.bbte.idde.baim2115.backend.model.Ingatlan;


public interface IngatlanDao extends Dao<Ingatlan> {
    // sajatos kereses
    Ingatlan findByPrice(Integer price);
}
