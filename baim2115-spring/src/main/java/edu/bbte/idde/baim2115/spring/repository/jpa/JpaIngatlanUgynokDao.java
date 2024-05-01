package edu.bbte.idde.baim2115.spring.repository.jpa;

import edu.bbte.idde.baim2115.spring.model.IngatlanUgynok;
import edu.bbte.idde.baim2115.spring.repository.IngatlanUgynokDao;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public interface JpaIngatlanUgynokDao extends JpaRepository<IngatlanUgynok, Long>, IngatlanUgynokDao {

    @Query("UPDATE IngatlanUgynok iUgy SET iUgy.email=:#{#ingatlanUgy.email}, "
            + "iUgy.telefonszam=:#{#ingatlanUgy.telefonszam} "
            + "WHERE iUgy.id=:#{#id}"
    )
    @Transactional
    @Modifying
    @Override
    void update(@Param("ingatlanUgy") IngatlanUgynok ingatlanUgy, @Param("id") Long id);
}
