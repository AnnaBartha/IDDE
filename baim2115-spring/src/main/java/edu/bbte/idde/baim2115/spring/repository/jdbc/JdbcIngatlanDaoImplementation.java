package edu.bbte.idde.baim2115.spring.repository.jdbc;

import edu.bbte.idde.baim2115.spring.model.Ingatlan;
import edu.bbte.idde.baim2115.spring.repository.IngatlanDao;
import edu.bbte.idde.baim2115.spring.repository.RepositoryExeption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Profile("jdbc")
@Repository
@RequiredArgsConstructor
public class JdbcIngatlanDaoImplementation implements IngatlanDao {

    // Datasource = a javax belso sql csomagja
    @Autowired
    private DataSource dataSource;

    private Ingatlan createIngatlan(ResultSet resultSet) throws SQLException {
        Ingatlan ingatlan = new Ingatlan();
        ingatlan.setOrszag(resultSet.getString("orszag"));
        ingatlan.setVaros(resultSet.getString("varos"));
        ingatlan.setNegyzetmeter(resultSet.getInt("negyzetmeter"));
        ingatlan.setTermekAra(resultSet.getInt("termekAra"));
        ingatlan.setTulajNeve(resultSet.getString("tulajNeve"));
        ingatlan.setElerhetoseg(resultSet.getString("elerhetoseg"));

        ingatlan.setId(resultSet.getLong("id"));
        return ingatlan;
    }

    @Override
    public Collection<Ingatlan> findAll() {
        log.info("(jdbc) A findall metodusban.");
        Collection<Ingatlan> ingatlan = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Ingatlan;");
            ResultSet resultSet = preparedStatement.executeQuery();
            log.info("get-ben");
            while (resultSet.next()) {
                // ingatlan.add(createIngatlanFromResultSet(resultSet, connection));
                ingatlan.add(createIngatlan(resultSet));
            }
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen listazas", e);
        }
        return ingatlan;
        // return Collections.emptyList();
    }

    @Override
    public Optional<Ingatlan> findById(Long id) {
        log.info("(jdbc) A findById metodusban.");
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Ingatlan WHERE id = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Ingatlan ingatlan = createIngatlan(resultSet);
                return Optional.of(ingatlan);
                // return createIngatlan(resultSet); // lementem egy valtozoba aztan return Optional.of(valtozom)
            } else {
                throw new RepositoryExeption("Az on altal megadott id helyetelen");
            }
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen listazas id alapjan", e);
        }
    }

    @Override
    public Ingatlan saveAndFlush(Ingatlan entity) {
        log.info("(jdbc) A create metodusban.");
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
                throw new RepositoryExeption("Hiba merult fel az Ingatlan letrehozasanal");
            }
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen letrehozas", e);
        }
    }

    @Override
    public void update(Ingatlan entity, Long id) {
        log.info("(jdbc) Az update metodusban.");
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

            entity.setId(id);
            // return entity;
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen modositas", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("(jdbc) A delete metodusban.");
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
    public Collection<Ingatlan> findByTermekAra(Integer price) {
        log.info("(jdbc) A findByPrice metodusban.");
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Ingatlan "
                    + "WHERE termekAra = ?");
            preparedStatement.setInt(1, price);
            ResultSet resultSet = preparedStatement.executeQuery();
            Collection<Ingatlan> ingatlanok = new ArrayList<>();
            while (resultSet.next()) {
                // return createIngatlanFromResultSet(resultSet, connection);
                Ingatlan optionalIngatlan = createIngatlan(resultSet);
                // return createIngatlan(resultSet);
                // return Optional.of(optionalIngatlan);
                ingatlanok.add(optionalIngatlan);
            }
            if (ingatlanok.isEmpty()) {
                throw new RepositoryExeption("Nem talalhato ilyen arral rendelkezo ingatlan");
            }
            return ingatlanok;
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen kereses ar alapjan", e);
        }
    }
}
