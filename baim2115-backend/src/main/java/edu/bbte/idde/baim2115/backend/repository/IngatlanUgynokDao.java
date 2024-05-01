package edu.bbte.idde.baim2115.backend.repository;

import edu.bbte.idde.baim2115.backend.model.IngatlanUgynok;


public interface IngatlanUgynokDao extends Dao<IngatlanUgynok> {
    // sajatos kereses
    IngatlanUgynok findByPhoneNumber(String phoneNr);
}
