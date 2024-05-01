package edu.bbte.idde.baim2115.backend.repository.mem;

import edu.bbte.idde.baim2115.backend.model.Ingatlan;
import edu.bbte.idde.baim2115.backend.repository.IngatlanDao;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public final class IngatlanDaoImplementation implements IngatlanDao, Serializable {
    private final ConcurrentHashMap<Long, Ingatlan> ingatlanokLongHashMap = new ConcurrentHashMap<>();
    private static final AtomicLong idAtomic = new AtomicLong();

    private static IngatlanDaoImplementation instance;

    public static synchronized IngatlanDaoImplementation getInstance() {
        if (instance == null) {
            instance = new IngatlanDaoImplementation();
        }
        return instance;
    }


    @Override
    public List<Ingatlan> findAll() {
        Collection<Ingatlan> ertekek = ingatlanokLongHashMap.values();
        return new ArrayList<>(ertekek);
    }

    @Override
    public Ingatlan findById(Long id) {
        return ingatlanokLongHashMap.get(id);
    }

    @Override
    public Ingatlan create(Ingatlan entity) {
        Long id = idAtomic.getAndIncrement();
        entity.setId(id);
        ingatlanokLongHashMap.put(id, entity);
        log.info("CREATE lefutott sikeresen");
        return entity;
    }

    @Override
    public Ingatlan update(Ingatlan entity, Long id) {
        log.debug("UPDATE meghivodott: adatok = " + entity + ", id = " + id);
        ingatlanokLongHashMap.remove(id);
        entity.setId(id);
        ingatlanokLongHashMap.put(id, entity);
        log.info("UPDATE lefutott sikeresen.");
        return entity;
    }

    @Override
    public void delete(Long id) {
        log.debug("DELETE meghivodott: id = " + id);
        Ingatlan toroltIngatlan = ingatlanokLongHashMap.get(id);
        log.info("DELETE hatasara torlodott: " + toroltIngatlan);
        ingatlanokLongHashMap.remove(id);
    }

    @Override
    public Ingatlan findByPrice(Integer price) {
        for (Ingatlan ingatlan : ingatlanokLongHashMap.values()) {
            if (ingatlan.getTermekAra() != null && ingatlan.getTermekAra().equals(price)) {
                log.info("findByPrice talalt Ingatlan: " + ingatlan);
                return ingatlan;
            }
        }
        log.info("findByPrice nem talalt megfelelo Ingatlant az ár alapján.");
        return null;
    }
}
