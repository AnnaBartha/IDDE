package edu.bbte.idde.baim2115.spring.repository.jdbc;

import edu.bbte.idde.baim2115.spring.model.Ingatlan;
import edu.bbte.idde.baim2115.spring.model.IngatlanUgynok;
import edu.bbte.idde.baim2115.spring.repository.IngatlanUgynokDao;
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
import java.util.List;
import java.util.Optional;

@Slf4j
@Profile("jdbc")
@Repository
@RequiredArgsConstructor
public class JdbcIngatlanUgynokDaoImplementation implements IngatlanUgynokDao {

    @Autowired
    private final DataSource dataSource;

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

    private IngatlanUgynok createIngatlanUgynokFromResultSet(ResultSet resultSet,
                                                             Connection connection) throws SQLException {
        IngatlanUgynok ingatlanUgynok = new IngatlanUgynok(resultSet.getString("email"),
                resultSet.getString("telefonszam"), new ArrayList<>());
        ingatlanUgynok.setId(resultSet.getLong("id"));
        // kapcsolat tablabol valo lekeresek
        String query = "SELECT Ingatlan.id, orszag, varos, negyzetmeter, termekAra, tulajNeve, elerhetoseg FROM "
                + "Kapcsolat INNER JOIN Ingatlan on Kapcsolat.ingatlanId = Ingatlan.id "
                + "WHERE ugynokId = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, resultSet.getInt("id"));
        ResultSet resultSet1 = preparedStatement.executeQuery();
        while (resultSet1.next()) {
            List<Ingatlan> ingatlanList = ingatlanUgynok.getIngatlanok();
            ingatlanList.add(createIngatlanFromResultSet(resultSet1, connection));
            ingatlanUgynok.setIngatlanok(ingatlanList);
        }
        return ingatlanUgynok;
    }

    @Override
    public Collection<IngatlanUgynok> findAll() {
        log.info("FINDALL");
        Collection<IngatlanUgynok> ingatlanUgynok = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM "
                    + "Ugynok;", Statement.RETURN_GENERATED_KEYS);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ingatlanUgynok.add(createIngatlanUgynokFromResultSet(resultSet, connection));
            }
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen listazas", e);
        }
        return ingatlanUgynok;
    }

    @Override
    public Optional<IngatlanUgynok> findById(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Ugynok WHERE id = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                IngatlanUgynok optionalIngatlanUgynok = createIngatlanUgynokFromResultSet(resultSet, connection);
                // return createIngatlanUgynokFromResultSet(resultSet, connection);
                return Optional.of(optionalIngatlanUgynok);
            }
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen listazas id alapjan", e);
        }
        throw new RepositoryExeption("sikertelen listazas id alapjan");
    }

    @Override
    public IngatlanUgynok saveAndFlush(IngatlanUgynok entity) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Ugynok "
                    + "VALUES(DEFAULT,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getEmail());
            preparedStatement.setString(2, entity.getTelefonszam());
            // amikor nem varunk vissz avaalszt
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();

            if (keys.next()) {
                log.info("CREATE6");
                long id = keys.getLong(1);
                entity.setId(id);


                // Beszurom a kapcsolat tablaba is az uj Ugynokhoz tartozo ingatlanokat
                List<Ingatlan> ingatlanokKapcsolatban = entity.getIngatlanok();
                log.info("CREATE-BOL keresem az ingatlanokat:" + ingatlanokKapcsolatban.toString());

                String query = "INSERT INTO Kapcsolat VALUES(?,?);";
                PreparedStatement preparedStatement1 = connection.prepareStatement(query);
                for (Ingatlan i : ingatlanokKapcsolatban) {
                    // beszurom a kapcsolat tablaba is a megfelelo ugynokhoz tartozo ingatlanokat
                    // hogy a kapcsolat letrejojjon adatbazis szinten is
                    log.info("CREATEBOL INAGTLAN ID + UGYNOK ID:" + i.getId().toString() + "," + id);

                    preparedStatement1.clearParameters();

                    preparedStatement1.setLong(1, i.getId());
                    preparedStatement1.setLong(2, id);
                    preparedStatement1.addBatch();
                }
                preparedStatement1.executeBatch();
                return entity;
            } else {
                return null;
            }
        } catch (SQLException e) {
            log.info("Sikertelen CREATE");
            throw new RepositoryExeption("Sikertelen tabla letrehozas", e);
        }
    }

    @Override
    public void update(IngatlanUgynok entity, Long id) {
        log.info("UPDATE");
        try (Connection connection = dataSource.getConnection()) {

            PreparedStatement preparedStatement1 = connection.prepareStatement("DELETE FROM Kapcsolat "
                    + "WHERE ugynokId = ?");
            preparedStatement1.setLong(1, id);
            preparedStatement1.executeUpdate();

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Ugynok SET "
                    + "email = ?, telefonszam = ? WHERE id = ?;", Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, entity.getEmail());
            preparedStatement.setString(2, entity.getTelefonszam());
            preparedStatement.setLong(3, id);
            // amikor nem varunk vissz avaalszt
            preparedStatement.executeUpdate();
            // ResultSet keys = preparedStatement.getGeneratedKeys();

            entity.setId(id);
            // Ujraallitom az ugynokom modositott indgatlanait
            List<Ingatlan> ingatlanokKapcsolatban = entity.getIngatlanok();
            log.info("create uj ingatlanok: " + ingatlanokKapcsolatban);
            String query = "INSERT INTO Kapcsolat VALUES(?,?);";
            PreparedStatement preparedStatement2 = connection.prepareStatement(query);
            for (Ingatlan i : ingatlanokKapcsolatban) {
                // beszurom a kapcsolat tablaba is a megfelelo ugynokhoz tartozo ingatlanokat
                // hogy a kapcsolat letrejojjon adatbazis szinten is
                log.info("CREATEBOL INAGTLAN ID + UGYNOK ID:" + i.getId().toString() + "," + id);

                preparedStatement2.clearParameters();
                log.info("ID : " + id + entity.getId());

                preparedStatement2.setLong(1, i.getId());
                preparedStatement2.setLong(2, id);
                preparedStatement2.addBatch();
            }
            preparedStatement2.executeBatch();
            // return entity;
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen modositas", e);
        }
        // return null;
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Kapcsolat "
                    + "WHERE ugynokId = ?");
            preparedStatement.setLong(1, id);
            // amikor nem varunk vissz avaalszt
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("DELETE FROM Ugynok WHERE id = ?");
            preparedStatement.setLong(1, id);
            // amikor nem varunk vissz avaalszt
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen modositas", e);
        }
    }

    @Override
    public Collection<IngatlanUgynok> findByTelefonszam(String phoneNr) {
        log.info("FIND BY PHONE NR" + phoneNr);
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Ugynok "
                    + "WHERE telefonszam = ?");
            preparedStatement.setString(1, phoneNr);
            ResultSet resultSet = preparedStatement.executeQuery();
            Collection<IngatlanUgynok> ugynokCollection = new ArrayList<>();
            if (resultSet.next()) {
                IngatlanUgynok optionalIngatlanUgynok = createIngatlanUgynokFromResultSet(resultSet, connection);
                // return createIngatlanUgynokFromResultSet(resultSet, connection);
                // return Optional.of(optionalIngatlanUgynok);
                ugynokCollection.add(optionalIngatlanUgynok);
            }
            if (ugynokCollection.isEmpty()) {
                throw new RepositoryExeption("Nem talalhato ilyen telefonszammal rendelkezo ugynok");
            }
            return ugynokCollection;
        } catch (SQLException e) {
            log.info("Sikertelen");
            throw new RepositoryExeption("Sikertelen listazas telefonszam alapjan", e);
        }
    }
}
