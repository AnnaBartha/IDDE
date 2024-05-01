package edu.bbte.idde.baim2115.spring.repository.jpa;

import edu.bbte.idde.baim2115.spring.model.Ingatlan;
import edu.bbte.idde.baim2115.spring.repository.IngatlanDao;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public interface JpaIngatlanDao extends JpaRepository<Ingatlan, Long>, IngatlanDao {

    @Query("UPDATE Ingatlan ing SET ing.orszag=:#{#ingatlan.orszag}, ing.varos=:#{#ingatlan.varos},"
            + " ing.negyzetmeter=:#{#ingatlan.negyzetmeter}, ing.termekAra=:#{#ingatlan.termekAra},"
            + " ing.tulajNeve=:#{#ingatlan.tulajNeve}, ing.elerhetoseg=:#{#ingatlan.elerhetoseg}"
            + " WHERE ing.id=:#{#id}")
    @Transactional
    @Modifying
    @Override
    void update(@Param("ingatlan") Ingatlan ingatlan, @Param("id") Long id);
}
