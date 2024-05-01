package edu.bbte.idde.baim2115.spring.controller;

import edu.bbte.idde.baim2115.spring.controller.dto.IngatlanInDto;
import edu.bbte.idde.baim2115.spring.controller.dto.IngatlanOutDto;
import edu.bbte.idde.baim2115.spring.mapper.IngatlanMapper;
import edu.bbte.idde.baim2115.spring.model.Ingatlan;
import edu.bbte.idde.baim2115.spring.repository.IngatlanDao;
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
@RequestMapping("/ingatlanok") // localhost:8080/ingtalanok
public class IngatlanController {
    //    @Autowired
    private final IngatlanDao ingatlanDao;

    // @Autowired
    private final IngatlanMapper ingatlanMapper;

    @GetMapping(params = {"price"})
    public Collection<IngatlanOutDto> findByPrice(@RequestParam Integer price) {
        Collection<Ingatlan> optionalIngatlan = ingatlanDao.findByTermekAra(price);
        if (optionalIngatlan.isEmpty()) {
            throw new RepositoryExeption("Hiba merult fel az ar alapjan toteno keresesben.");
        }
        return ingatlanMapper.dtosFromIngatlans(optionalIngatlan);
    }


    @GetMapping("/{id}")
    public IngatlanOutDto findById(@PathVariable Long id) {
        if (id == null) {
            throw new RepositoryExeption("Nincs megadva id");
        }
        Optional<Ingatlan> optionalIngatlan = ingatlanDao.findById(id);
        if (optionalIngatlan.isEmpty()) {
            throw new RepositoryExeption("Hibas az on altal megadott id.");
        }
        return ingatlanMapper.dtoFromIngatlan(optionalIngatlan.get());

        // return ingatlanMapper.dtoFromIngatlan(ingatlanDao.findById(id));
    }

    @GetMapping
    public Collection<IngatlanOutDto> findAll() {
        return ingatlanMapper.dtosFromIngatlans(ingatlanDao.findAll());
    }

    @PostMapping
    public IngatlanOutDto create(@RequestBody @Valid IngatlanInDto ingatlanInDto) {
        Ingatlan ingatlan = ingatlanMapper.ingatlanFromDto(ingatlanInDto);
        try {
            ingatlan = ingatlanDao.saveAndFlush(ingatlan);
        } catch (RepositoryExeption e) {
            throw new RepositoryExeption("Hiba tortent az inagtaln letrehozasaban!", e);
        }
        return ingatlanMapper.dtoFromIngatlan(ingatlan);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody @Valid IngatlanInDto ingatlanInDto) {
        Ingatlan ingatlan = ingatlanMapper.ingatlanFromDto(ingatlanInDto);
        // ingatlan = ingatlanDao.update(ingatlan, id);
        ingatlanDao.update(ingatlan, id);
        // return ingatlanMapper.dtoFromIngatlan(ingatlan);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ingatlanDao.deleteById(id);
    }

}
