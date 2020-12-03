package ru.mumu.bot.bean.db;

import ru.mumu.bot.db.IDBOperations;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Default;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Default
@Singleton
@Startup
public class DBImpl implements IDBOperations {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private Connection connection;

    @PostConstruct
    public void initialize() {
        try {
            logger.log(Level.SEVERE, "Initial datasource and connection...");
            DataSource dataSource = (DataSource) new InitialContext().lookup("java:jboss/datasources/SqliteDS");
            connection = dataSource.getConnection();
            logger.log(Level.SEVERE, "Initial done.");
        } catch (SQLException | NamingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String selectDataFromDB(String daysOfWeek) {
        logger.log(Level.INFO, "Select data ...");
        String selectQuery = "select * from menuItems where dayOfWeek = ?;";
        String menu = null;
        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setString(1, daysOfWeek);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                menu = resultSet.getString("menu");
                logger.log(Level.INFO, "RESULT SELECT - {0}", menu);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return menu;
    }

    @Override
    public void insertDataToDB(String daysOfWeek, String menu) {
        logger.log(Level.INFO, "Insert data ...");
        String insertQuery = "insert into menuItems (dayOfWeek, menu) values (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, daysOfWeek);
            statement.setString(2, menu);
            statement.execute();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void updateDataToDB(String daysOfWeek, String menu) {
        logger.log(Level.INFO, "Update data ...");
        String updateQuery = "update menuItems set menu = ? where dayOfWeek = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, menu);
            statement.setString(2, daysOfWeek);
            statement.execute();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            logger.log(Level.SEVERE, "Close connection...");
            connection.close();
            connection = null;
            logger.log(Level.SEVERE, "Connection was closed...");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}