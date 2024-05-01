package edu.bbte.idde.baim2115.spring.repository.mem;

import edu.bbte.idde.baim2115.spring.model.IngatlanUgynok;
import edu.bbte.idde.baim2115.spring.repository.IngatlanUgynokDao;
import edu.bbte.idde.baim2115.spring.repository.RepositoryExeption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Profile("inmemory")
@Repository
@RequiredArgsConstructor
public class IngatlanUgynokDaoImplementation implements IngatlanUgynokDao {

    private final ConcurrentHashMap<Long, IngatlanUgynok> ingatlanokLongHashMap = new ConcurrentHashMap<>();
    private static final AtomicLong idAtomic = new AtomicLong();

    private static IngatlanUgynokDaoImplementation instance;


    public static synchronized IngatlanUgynokDaoImplementation getInstance() {
        if (instance == null) {
            instance = new IngatlanUgynokDaoImplementation();
        }
        return instance;
    }


    @Override
    public List<IngatlanUgynok> findAll() {
        Collection<IngatlanUgynok> ertekek = ingatlanokLongHashMap.values();
        return new ArrayList<>(ertekek);
    }

    @Override
    public Optional<IngatlanUgynok> findById(Long id) {
        IngatlanUgynok optionalIngatlanUgynok = ingatlanokLongHashMap.get(id);
        //return ingatlanokLongHashMap.get(id);
        return Optional.of(optionalIngatlanUgynok);
    }

    @Override
    public IngatlanUgynok saveAndFlush(IngatlanUgynok entity) {
        Long id = idAtomic.getAndIncrement();
        entity.setId(id);
        ingatlanokLongHashMap.put(id, entity);
        log.info("CREATE ingatlan ugynok lefutott sikeresen");
        return entity;
    }

    @Override
    public void update(IngatlanUgynok entity, Long id) {
        log.debug("UPDATE meghivodott: adatok = " + entity + ", id = " + id);
        ingatlanokLongHashMap.remove(id);
        entity.setId(id);
        ingatlanokLongHashMap.put(id, entity);
        log.info("UPDATE ingatlan ugynok lefutott sikeresen.");
        // return entity;
    }

    @Override
    public void deleteById(Long id) {
        log.debug("DELETE meghivodott: id = " + id);
        IngatlanUgynok toroltIngatlanUgynok = ingatlanokLongHashMap.get(id);
        log.info("DELETE ingatlan ugynok hatasara torlodott: " + toroltIngatlanUgynok);
        ingatlanokLongHashMap.remove(id);
    }

    @Override
    public Collection<IngatlanUgynok> findByTelefonszam(String phoneNr) {
        Collection<IngatlanUgynok> ugynokCollection = new ArrayList<>();
        for (IngatlanUgynok ingatlanUgynok : ingatlanokLongHashMap.values()) {
            if (ingatlanUgynok.getTelefonszam() != null && ingatlanUgynok.getTelefonszam().equals(phoneNr)) {
                log.info("findByPhoneNumber talalt Ingatlan ugynok: " + ingatlanUgynok);
                // return Optional.of(ingatlanUgynok);
                ugynokCollection.add(ingatlanUgynok);
            }
        }
        if (ugynokCollection.isEmpty()) {
            log.info("findByPhoneNumber nem talalt megfelelo Ingatlan ugynokot a telefonszam alapján.");
            throw new RepositoryExeption("Hiba a telefonszam alapu keresesben");
        }
        return ugynokCollection;
    }
}
