package edu.bbte.idde.baim2115.spring.controller;

import edu.bbte.idde.baim2115.spring.controller.dto.IngatlanUgynokInDto;
import edu.bbte.idde.baim2115.spring.controller.dto.IngatlanUgynokOutDto;
import edu.bbte.idde.baim2115.spring.mapper.IngatlanUgynokMapper;
import edu.bbte.idde.baim2115.spring.model.IngatlanUgynok;
import edu.bbte.idde.baim2115.spring.repository.IngatlanDao;
import edu.bbte.idde.baim2115.spring.repository.IngatlanUgynokDao;
import edu.bbte.idde.baim2115.spring.repository.RepositoryExeption;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/ugynok") // localhost:8080/ugynok
public class IngatlanUgynokController {

    private final IngatlanUgynokDao ingatlanUgynokDao;
    private final IngatlanDao ingatlanDao;
    private final IngatlanUgynokMapper ingatlanUgynokMapper;

    @GetMapping(params = {"phonenr"})
    public Collection<IngatlanUgynokOutDto> findByPhoneNumber(@RequestParam String phonenr) {
        Collection<IngatlanUgynok> optionalIngatlanUgynok = ingatlanUgynokDao.findByTelefonszam(phonenr);
        if (optionalIngatlanUgynok.isEmpty()) {
            throw new RepositoryExeption("Hiba a telefonszam alapu kereses kozben.");
        }
        log.info("{}", optionalIngatlanUgynok);
        var outDto = ingatlanUgynokMapper.dtosFromIngatlanUgynoks(optionalIngatlanUgynok);
        log.info("{}", outDto);

        return outDto;
    }

    @GetMapping("/{id}")
    public IngatlanUgynokOutDto findById(@PathVariable Long id) {
        if (id == null) {
            throw new RepositoryExeption("Hianyzo id");
        }
        Optional<IngatlanUgynok> optionalIngatlanUgynok = ingatlanUgynokDao.findById(id);
        if (optionalIngatlanUgynok.isEmpty()) {
            throw new RepositoryExeption("Hibas az on altal megadott id");
        }
        return ingatlanUgynokMapper.dtoFromIngatlanUgynok(optionalIngatlanUgynok.get());
    }

    @GetMapping
    public Collection<IngatlanUgynokOutDto> findAll() {
        Collection<IngatlanUgynok> ingatlanUgynokList = ingatlanUgynokDao.findAll();
        // Collection<IngatlanUgynokOutDto> outDto = dtosFromIngatlanUgynoks(ingatlanUgynokList);
        log.info("~~~~LOOOOOOOOOOOOOOOOOG~~~ " + ingatlanUgynokList);
        return ingatlanUgynokMapper.dtosFromIngatlanUgynoks(ingatlanUgynokList);

        // return ingatlanUgynokMapper.dtosFromIngatlanUgynoks(ingatlanUgynokDao.findAll());
    }

    @PostMapping
    public IngatlanUgynokOutDto create(@RequestBody @Valid IngatlanUgynokInDto ingatlanUgynokInDto) {
        IngatlanUgynok ingatlanUgynok = ingatlanUgynokMapper.ingatlanUgynokFromDto(ingatlanUgynokInDto);
        try {
            log.info("LOOOOOGGGGGGG SAVEANDFLUSH" + ingatlanUgynok);
            ingatlanUgynok = ingatlanUgynokDao.saveAndFlush(ingatlanUgynok);
        } catch (RepositoryExeption e) {
            throw new RepositoryExeption("Hiba tortent az ingatlan ugynok letrehozas kozben.", e);
        }
        log.info("{}", ingatlanUgynok);
        var outDto = ingatlanUgynokMapper.dtoFromIngatlanUgynok(ingatlanUgynok);
        log.info("{}", outDto);

        return outDto;
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @RequestBody @Valid IngatlanUgynokInDto ingatlanUgynokInDto) {
        IngatlanUgynok ingatlanUgynok = ingatlanUgynokMapper.ingatlanUgynokFromDto(ingatlanUgynokInDto);
        // ingatlanUgynok = ingatlanUgynokDao.update(ingatlanUgynok, id);
        ingatlanUgynokDao.update(ingatlanUgynok, id);
        // return ingatlanUgynokMapper.dtoFromIngatlanUgynok(ingatlanUgynok);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ingatlanUgynokDao.deleteById(id);
    }

}
