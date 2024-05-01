package edu.bbte.idde.baim2115.backend.repository.jdbc;

import edu.bbte.idde.baim2115.backend.repository.AbstractDaoFactory;
import edu.bbte.idde.baim2115.backend.repository.IngatlanDao;
import edu.bbte.idde.baim2115.backend.repository.IngatlanUgynokDao;

public class JdbcDaoFactory extends AbstractDaoFactory {
    private static JdbcIngatlanDaoImplementation ingatlanDao;
    private static JdbcIngatlanUgynokDaoImplementation ugynokDao;


    @Override
    public IngatlanDao getAbstractIngatlanDao() {
        synchronized (JdbcDaoFactory.class) {
            if (ingatlanDao == null) {
                ingatlanDao = new JdbcIngatlanDaoImplementation();
            }
        }
        return ingatlanDao;
    }

    @Override
    public IngatlanUgynokDao getAbstractIngatlanUgynokDao() {
        synchronized (JdbcDaoFactory.class) {
            if (ugynokDao == null) {
                ugynokDao = new JdbcIngatlanUgynokDaoImplementation();
            }
        }
        return ugynokDao;
    }
}
