package edu.bbte.idde.baim2115.backend.repository.mem;

import edu.bbte.idde.baim2115.backend.model.IngatlanUgynok;
import edu.bbte.idde.baim2115.backend.repository.IngatlanUgynokDao;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public final class IngatlanUgynokDaoImplementation implements IngatlanUgynokDao, Serializable {

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
    public IngatlanUgynok findById(Long id) {
        return ingatlanokLongHashMap.get(id);
    }

    @Override
    public IngatlanUgynok create(IngatlanUgynok entity) {
        Long id = idAtomic.getAndIncrement();
        entity.setId(id);
        ingatlanokLongHashMap.put(id, entity);
        log.info("CREATE ingatlan ugynok lefutott sikeresen");
        return entity;
    }

    @Override
    public IngatlanUgynok update(IngatlanUgynok entity, Long id) {
        log.debug("UPDATE meghivodott: adatok = " + entity + ", id = " + id);
        ingatlanokLongHashMap.remove(id);
        entity.setId(id);
        ingatlanokLongHashMap.put(id, entity);
        log.info("UPDATE ingatlan ugynok lefutott sikeresen.");
        return entity;
    }

    @Override
    public void delete(Long id) {
        log.debug("DELETE meghivodott: id = " + id);
        IngatlanUgynok toroltIngatlanUgynok = ingatlanokLongHashMap.get(id);
        log.info("DELETE ingatlan ugynok hatasara torlodott: " + toroltIngatlanUgynok);
        ingatlanokLongHashMap.remove(id);
    }

    @Override
    public IngatlanUgynok findByPhoneNumber(String phoneNr) {
        for (IngatlanUgynok ingatlanUgynok : ingatlanokLongHashMap.values()) {
            if (ingatlanUgynok.getTelefonszam() != null && ingatlanUgynok.getTelefonszam().equals(phoneNr)) {
                log.info("findByPhoneNumber talalt Ingatlan ugynok: " + ingatlanUgynok);
                return ingatlanUgynok;
            }
        }
        log.info("findByPhoneNumber nem talalt megfelelo Ingatlan ugynokot a telefonszam alapján.");
        return null;
    }
}
