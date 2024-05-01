package edu.bbte.idde.baim2115.spring.repository.mem;

import edu.bbte.idde.baim2115.spring.model.Ingatlan;
import edu.bbte.idde.baim2115.spring.repository.IngatlanDao;
import edu.bbte.idde.baim2115.spring.repository.RepositoryExeption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Profile("inmemory")
@Repository
@RequiredArgsConstructor
public class IngatlanDaoImplementation implements IngatlanDao {
    private final ConcurrentHashMap<Long, Ingatlan> ingatlanokLongHashMap = new ConcurrentHashMap<>();
    private static final AtomicLong idAtomic = new AtomicLong();

    @Override
    public Collection<Ingatlan> findAll() {
        log.info("(inmemory) A finall metodusban.");
        return ingatlanokLongHashMap.values();
    }

    @Override
    public Optional<Ingatlan> findById(Long id) {
        log.info("(inmemory) A findById metodusban.");
        Ingatlan ingatlan = ingatlanokLongHashMap.get(id);
        // return ingatlanokLongHashMap.get(id);
        return Optional.of(ingatlan);
    }

    @Override
    public Ingatlan saveAndFlush(Ingatlan entity) {
        log.info("(inmemory) A create metodusban.");
        Long id = idAtomic.getAndIncrement();
        entity.setId(id);
        ingatlanokLongHashMap.put(id, entity);
        return entity;
    }

    @Override
    public void update(Ingatlan entity, Long id) {
        log.info("(inmemory) Az update metodusban.");
        ingatlanokLongHashMap.remove(id);
        entity.setId(id);
        ingatlanokLongHashMap.put(id, entity);
        // return entity;
    }

    @Override
    public void deleteById(Long id) {
        log.info("(inmemory) A delete metodusban.");
        ingatlanokLongHashMap.remove(id);
    }

    @Override
    public Collection<Ingatlan> findByTermekAra(Integer price) {
        log.info("(inmemory) A findByPrice metodusban.");
        Collection<Ingatlan> ingatlanok = new ArrayList<>();
        for (Ingatlan ingatlan : ingatlanokLongHashMap.values()) {
            if (ingatlan.getTermekAra() != null && ingatlan.getTermekAra().equals(price)) {
                log.info("findByPrice talalt Ingatlan: " + ingatlan);
                // return Optional.of(ingatlan);
                ingatlanok.add(ingatlan);
            }
        }
        if (ingatlanok.isEmpty()) {
            log.info("findByPrice nem talalt megfelelo Ingatlant az ár alapján.");
            throw new RepositoryExeption("Hiba az ar alapjan torteno keresesben.");
        }
        return ingatlanok;
    }
}
