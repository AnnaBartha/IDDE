package edu.bbte.idde.baim2115.spring.controller;

import edu.bbte.idde.baim2115.spring.controller.dto.IngatlanAndUgynokInDto;
import edu.bbte.idde.baim2115.spring.controller.dto.IngatlanOutDto;
import edu.bbte.idde.baim2115.spring.controller.dto.IngatlanUgynokOutDto;
import edu.bbte.idde.baim2115.spring.mapper.IngatlanMapper;
import edu.bbte.idde.baim2115.spring.mapper.IngatlanUgynokMapper;
import edu.bbte.idde.baim2115.spring.model.Ingatlan;
import edu.bbte.idde.baim2115.spring.model.IngatlanUgynok;
import edu.bbte.idde.baim2115.spring.repository.IngatlanDao;
import edu.bbte.idde.baim2115.spring.repository.IngatlanUgynokDao;
import edu.bbte.idde.baim2115.spring.repository.RepositoryExeption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/kozos")
public class IngatlanAndUgynokController {
    private final IngatlanUgynokDao ingatlanUgynokDao;
    private final IngatlanDao ingatlanDao;
    private final IngatlanUgynokMapper ingatlanUgynokMapper;
    private final IngatlanMapper ingatlanMapper;

    @PostMapping("/{ugynokId}/ingatlanok")
    public IngatlanUgynokOutDto ujIngatlanHozzaadas(@PathVariable Long ugynokId,
                                                    @RequestBody IngatlanAndUgynokInDto inIngatlanId) {
        try {
            Optional<IngatlanUgynok> ingatlanUgynok = ingatlanUgynokDao.findById(ugynokId);
            Long ingatlanId = inIngatlanId.getIngatlanId();
            Optional<Ingatlan> ingatlan = ingatlanDao.findById(ingatlanId);
            if (ingatlanUgynok.isEmpty() || ingatlan.isEmpty()) {
                throw new RepositoryExeption("ilyen ingatlan ugynok nem letezik");
            }
            ingatlanUgynok.get().getIngatlanok().add(ingatlan.get());
            ingatlan.get().setIngatlanUgynok(ingatlanUgynok.get());
            ingatlanUgynokDao.saveAndFlush(ingatlanUgynok.get());
            return ingatlanUgynokMapper.dtoFromIngatlanUgynok(ingatlanUgynok.get());
        } catch (RepositoryExeption e) {
            throw new RepositoryExeption("hiba az entitas beszurasa soran", e);
        }
    }

    @DeleteMapping("/{ugynokId}/ingatlanok/{ingatlanId}")
    public void deleteKapcsolat(@PathVariable Long ugynokId, @PathVariable Long ingatlanId) {
        try {

            Optional<IngatlanUgynok> ingatlanUgynok = ingatlanUgynokDao.findById(ugynokId);
            if (ingatlanUgynok.isEmpty()) {
                throw new RepositoryExeption("ilyen id-ju ugynok nem letezik");
            }
            Optional<Ingatlan> ingatlan = ingatlanDao.findById(ingatlanId);
            if (ingatlan.isEmpty()) {
                throw new RepositoryExeption("ilyen id-ju ingatlan nincsen");
            }

            IngatlanUgynok ugynok = ingatlanUgynok.get();
            Ingatlan ingatlanok = ingatlan.get();

            ugynok.getIngatlanok().remove(ingatlanok);
            ingatlanUgynokDao.saveAndFlush(ugynok);
            ingatlanDao.deleteById(ugynok.getId());

        } catch (RepositoryExeption e) {
            throw new RepositoryExeption("Hiba a torlesben", e);
        }
    }

    @GetMapping("/{ugynokId}/ingatlanok")
    public Collection<IngatlanOutDto> listazas(@PathVariable Long ugynokId) {
        try {
            Optional<IngatlanUgynok> ingatlanUgynok = ingatlanUgynokDao.findById(ugynokId);
            if (ingatlanUgynok.isEmpty()) {
                throw new RepositoryExeption("Ilyen ugynok nem letezik.");
            }
            Collection<Ingatlan> ingatlanokLista = ingatlanUgynok.get().getIngatlanok();
            return ingatlanMapper.dtosFromIngatlans(ingatlanokLista);

        } catch (RepositoryExeption e) {
            throw new RepositoryExeption("hiba a listazasnal", e);
        }

    }
}
