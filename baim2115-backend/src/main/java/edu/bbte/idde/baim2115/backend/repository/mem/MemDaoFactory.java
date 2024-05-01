package edu.bbte.idde.baim2115.backend.repository.mem;

import edu.bbte.idde.baim2115.backend.model.Ingatlan;
import edu.bbte.idde.baim2115.backend.model.IngatlanUgynok;
import edu.bbte.idde.baim2115.backend.repository.AbstractDaoFactory;
import edu.bbte.idde.baim2115.backend.repository.IngatlanDao;
import edu.bbte.idde.baim2115.backend.repository.IngatlanUgynokDao;

import java.util.ArrayList;

// ingatlan dao vagy ingatlan ugynok dao (IN MEMORY)
public class MemDaoFactory extends AbstractDaoFactory {
    private static IngatlanDaoImplementation ingatlanDao;
    private static IngatlanUgynokDaoImplementation ugynokDao;


    @Override
    public IngatlanDao getAbstractIngatlanDao() {
        synchronized (MemDaoFactory.class) {
            if (ingatlanDao == null) {
                ingatlanDao = new IngatlanDaoImplementation();
                ingatlanDao.create(new Ingatlan("Roman",
                        "Marosvasarhely", 12, 12, "Anna", "0754"));
            }
        }
        return ingatlanDao;
    }

    @Override
    public IngatlanUgynokDao getAbstractIngatlanUgynokDao() {
        synchronized (MemDaoFactory.class) {
            if (ugynokDao == null) {
                ugynokDao = new IngatlanUgynokDaoImplementation();
                ugynokDao.create(new IngatlanUgynok("barthaanna", "0754", new ArrayList<>()));
            }
        }
        return ugynokDao;
    }
}
