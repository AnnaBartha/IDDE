package edu.bbte.idde.baim2115.backend.repository;

import edu.bbte.idde.baim2115.backend.config.Config;
import edu.bbte.idde.baim2115.backend.config.ConfigFactory;
import edu.bbte.idde.baim2115.backend.repository.jdbc.JdbcDaoFactory;
import edu.bbte.idde.baim2115.backend.repository.mem.MemDaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public abstract class AbstractDaoFactory {
    private static AbstractDaoFactory instance;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDaoFactory.class);

    public abstract IngatlanDao getAbstractIngatlanDao();

    public abstract IngatlanUgynokDao getAbstractIngatlanUgynokDao();

    // hasznalat:
    // inst = AbstractDaoFactory.getInstance()
    // -> inst.getAbstract......
    public static synchronized AbstractDaoFactory getInstance() {
        LOG.info("elkezdodott az abstarct dao");
        if (instance == null) {
            LOG.info("instance kezdetben semmi");
            // jdbc vagy mem megyen
            Config config = ConfigFactory.getConfig();
            if (Objects.equals(config.getProfile(), "jdbc")) {
                LOG.info("instance = jdbc");
                // JDBC
                instance = new JdbcDaoFactory();
                LOG.info("JDBC DAO FACT LETREHOZBA");
            } else {
                // In Memory
                instance = new MemDaoFactory();
                LOG.info("MEMORY DAO MUKODJ");
            }
        }
        return instance;
    }
}
