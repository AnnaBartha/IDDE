package edu.bbte.idde.baim2115.backend.repository.jdbc;

import edu.bbte.idde.baim2115.backend.model.Ingatlan;
import edu.bbte.idde.baim2115.backend.repository.IngatlanDao;
import edu.bbte.idde.baim2115.backend.repository.RepositoryExeption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class JdbcIngatlanDaoImplementation implements IngatlanDao, Serializable {
    private final DataSource dataSource;
    private static final Logger log = LoggerFactory.getLogger(JdbcIngatlanDaoImplementation.class);

    public JdbcIngatlanDaoImplementation() {
        dataSource = DataSourceFactory.getDataSource();
    }

    private Ingatlan createIngatlan(ResultSet resultSet) throws SQLException {
        Ingatlan ingatlan = new Ingatlan(
                resultSet.getString("orszag"),
                resultSet.getString("varos"),
                resultSet.getInt("negyzetmeter"),
                resultSet.getInt("termekAra"),
                resultSet.getString("tulajNeve"),
                resultSet.getString("elerhetoseg")
        );
        ingatlan.setId(resultSet.getLong("id"));
        return ingatlan;
    }

    private Ingatlan createIngatlanFromResultSet(ResultSet resultSet, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Ingatlan WHERE id = ?");
        preparedStatement.setInt(1, resultSet.getInt("id"));
        ResultSet resultSet1 = preparedStatement.executeQuery();
        // ha letezik ilyen ingatlan
        if (resultSet1.next()) {
            return createIngatlan(resultSet1);
        }
        return null;
    }

    @Override
    public Collection<Ingatlan> findAll() {
        log.info("FINDALL");
        Collection<Ingatlan> ingatlan = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Ingatlan;");
            ResultSet resultSet = preparedStatement.executeQuery();
            log.info("get-ben");
            while (resultSet.next()) {
                ingatlan.add(createIngatlanFromResultSet(resultSet, connection));
            }
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen listazas", e);
        }
        return ingatlan;
    }

    @Override
    public Ingatlan findById(Long id) {
        log.info("FINDBYID");
        // LEHET HOGY JO
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Ingatlan WHERE id = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return createIngatlanFromResultSet(resultSet, connection);
            }
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen listazas id alapjan", e);
        }
        return null;
    }

    @Override
    public Ingatlan create(Ingatlan entity) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Ingatlan "
                    + "VALUES(DEFAULT,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getOrszag());
            preparedStatement.setString(2, entity.getVaros());
            preparedStatement.setInt(3, entity.getNegyzetmeter());
            preparedStatement.setInt(4, entity.getTermekAra());
            preparedStatement.setString(5, entity.getTulajNeve());
            preparedStatement.setString(6, entity.getElerhetoseg());

            // amikor nem varunk vissz avaalszt
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();

            if (keys.next()) {
                long id = keys.getLong(1);

                entity.setId(id);
                return entity;
            } else {
                return null;
            }
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen letrehozas", e);
            // return null;
        }
    }

    @Override
    public Ingatlan update(Ingatlan entity, Long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Ingatlan SET "
                    + "orszag = ?, varos = ?,negyzetmeter = ? ,termekAra = ?,tulajNeve = ?"
                    + ",elerhetoseg = ? WHERE id = ?", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getOrszag());
            preparedStatement.setString(2, entity.getVaros());
            preparedStatement.setInt(3, entity.getNegyzetmeter());
            preparedStatement.setInt(4, entity.getTermekAra());
            preparedStatement.setString(5, entity.getTulajNeve());
            preparedStatement.setString(6, entity.getElerhetoseg());
            preparedStatement.setLong(7, id);

            // amikor nem varunk vissz avaalszt
            preparedStatement.executeUpdate();

            return findById(id);
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen modositas", e);
        }
        // return entity;
    }

    @Override
    public void delete(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Kapcsolat "
                    + "WHERE ingatlanId = ?");
            preparedStatement.setLong(1, id);
            // amikor nem varunk vissz avaalszt
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("DELETE FROM Ingatlan WHERE id = ?");
            preparedStatement.setLong(1, id);
            // amikor nem varunk vissz avaalszt
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen torles", e);
        }
    }

    @Override
    public Ingatlan findByPrice(Integer price) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Ingatlan "
                    + "WHERE termekAra = ?");
            preparedStatement.setInt(1, price);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return createIngatlanFromResultSet(resultSet, connection);
            }
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen kereses ar alapjan", e);
        }
        return null;
    }
}
