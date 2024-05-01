package edu.bbte.idde.baim2115.backend.repository;

import edu.bbte.idde.baim2115.backend.model.Ingatlan;

import java.util.List;

public interface IngatlanMuveletekInterface {


    Ingatlan createIngatlan(Ingatlan ujIngatlan);


    Ingatlan updateIngatlan(Ingatlan frissitettIngatlan, Long id);


    void deleteIngatlan(Long ingatlanId);

    List<Ingatlan> readIngatlan();

    Ingatlan getIngatlanById(Long id);


}
