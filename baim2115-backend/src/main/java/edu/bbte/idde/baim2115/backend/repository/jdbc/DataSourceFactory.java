package edu.bbte.idde.baim2115.backend.repository.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.bbte.idde.baim2115.backend.config.Config;
import edu.bbte.idde.baim2115.backend.config.ConfigFactory;
import edu.bbte.idde.baim2115.backend.repository.RepositoryExeption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataSourceFactory {
    private static DataSource dataSource;

    private static final Logger log = LoggerFactory.getLogger(DataSourceFactory.class);

    public static synchronized void initialize() {

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS "
                    + "Ingatlan(id INT AUTO_INCREMENT PRIMARY KEY, orszag VARCHAR(100),"
                    + " varos VARCHAR(100), negyzetmeter INT,"
                    + " termekAra INT, tulajNeve VARCHAR(100), elerhetoseg VARCHAR(100));");
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS "
                    + "Ugynok(id INT AUTO_INCREMENT PRIMARY KEY, email VARCHAR(100), telefonszam VARCHAR(100));");
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS "
                    + "Kapcsolat(ingatlanId INT,ugynokId INT, FOREIGN KEY(ingatlanId) "
                    + "REFERENCES Ingatlan(id), FOREIGN KEY(ugynokId) REFERENCES Ugynok(id), "
                    + "PRIMARY KEY(ingatlanId, ugynokId));");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen tabla letrehozas", e);
        }

    }


    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            Config config = ConfigFactory.getConfig();
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setDriverClassName(config.getJdbcDriver());
            hikariConfig.setJdbcUrl(config.getUrl());
            hikariConfig.setUsername(config.getUsername());
            hikariConfig.setPassword(config.getPassword());
            hikariConfig.setMaximumPoolSize(config.getPoolsize());
            dataSource = new HikariDataSource(hikariConfig);
        }
        initialize();
        return dataSource;
    }
}
