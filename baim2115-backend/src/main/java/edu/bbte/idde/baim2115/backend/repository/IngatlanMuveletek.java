package edu.bbte.idde.baim2115.backend.repository;

import edu.bbte.idde.baim2115.backend.model.Ingatlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class IngatlanMuveletek implements IngatlanMuveletekInterface, Serializable {
    private final ConcurrentHashMap<Long, Ingatlan> ingatlanokLongHashMap = new ConcurrentHashMap<>();
    private static final AtomicLong idAtomic = new AtomicLong();

    private static final Logger LOGGER = LoggerFactory.getLogger(IngatlanMuveletek.class);

    private static IngatlanMuveletek instance;

    // akarhanyszor meghivom a metodust -> 1x hozza letre CSAK!!!!
    // sync => threadsafe
    public static synchronized IngatlanMuveletek getInstance() {
        if (instance == null) {
            instance = new IngatlanMuveletek();
        }
        return instance;
    }

    private IngatlanMuveletek() {
    }

    @Override
    public Ingatlan createIngatlan(Ingatlan ujIngatlan) {
        Long id = idAtomic.getAndIncrement();
        ujIngatlan.setId(id);
        ingatlanokLongHashMap.put(id, ujIngatlan);
        LOGGER.info("CREATE lefutott sikeresen");
        return ujIngatlan;
    }

    @Override
    public Ingatlan updateIngatlan(Ingatlan frissitettIngatlan, Long id) {
        LOGGER.debug("UPDATE meghivodott: adatok = " + frissitettIngatlan + ", id = " + id);
        ingatlanokLongHashMap.remove(id);
        frissitettIngatlan.setId(id);
        ingatlanokLongHashMap.put(id, frissitettIngatlan);
        LOGGER.info("UPDATE lefutott sikeresen.");
        return frissitettIngatlan;
    }

    @Override
    public void deleteIngatlan(Long ingatlanId) {
        LOGGER.debug("DELETE meghivodott: id = " + ingatlanId);
        Ingatlan toroltIngatlan = ingatlanokLongHashMap.get(ingatlanId);
        LOGGER.info("DELETE hatasara torlodott: " + toroltIngatlan);
        ingatlanokLongHashMap.remove(ingatlanId);
    }

    @Override
    public List<Ingatlan> readIngatlan() {
        Collection<Ingatlan> ertekek = ingatlanokLongHashMap.values();
        return new ArrayList<>(ertekek);
    }

    @Override
    public Ingatlan getIngatlanById(Long id) {
        return ingatlanokLongHashMap.get(id);
    }

}
