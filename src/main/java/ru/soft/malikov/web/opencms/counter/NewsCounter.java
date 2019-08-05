package ru.soft.malikov.web.opencms.counter;

import org.apache.commons.logging.Log;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс, описывающий счетчик посещения для новостей
 *
 * @author Alexandr Malikov
 */
public class NewsCounter implements Counter, AutoCloseable {

    private static final String DB_TABLE_NAME = "CMS_NCFU_NEWS_COUNTERS";
    private static final String SQL_CHECK_DB = "SELECT count(*) FROM " + DB_TABLE_NAME + ";";

    private static final String SQL_CREATE_COUNTER_ENTRY = "INSERT INTO "
            + DB_TABLE_NAME
            + " (COUNTER_KEY, COUNTER_COUNT) VALUES(?,?);";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + DB_TABLE_NAME
            + " ("
            + "  COUNTER_KEY varchar(255) default NULL,"
            + "  COUNTER_COUNT int default NULL"
            + ");";

    private static final String SQL_DELETE_COUNTER_ENTRY = "DELETE FROM " + DB_TABLE_NAME + " WHERE COUNTER_KEY= ?;";

    private static final String SQL_GET_COUNTER_ENTRY = "SELECT COUNTER_COUNT FROM "
            + DB_TABLE_NAME
            + " WHERE COUNTER_KEY = ?;";

    private static final String SQL_GET_COUNTERS = "SELECT * FROM " + DB_TABLE_NAME + ";";

    private static final String SQL_UPDATE_COUNTER_ENTRY = "UPDATE "
            + DB_TABLE_NAME
            + " SET COUNTER_COUNT=? WHERE COUNTER_KEY = ?;";

    private static final Log LOG = CmsLog.getLog(NewsCounter.class);

    private Connection connection = null;

    /**
     * Создает объект типа "NewsCounter"
     */
    public NewsCounter() {
        setConnection(null);
    }

    /**
     * Создает объект типа "NewsCounter"
     *
     * @param connection
     */
    public NewsCounter(Connection connection) {
        setConnection(connection);

    }

    /**
     * @param entryKey Уникальный идентификатор счетчика
     * @return
     */
    public boolean deleteCounterEntry(String entryKey) {
        boolean result = false;
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(entryKey)) return result;
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(SQL_DELETE_COUNTER_ENTRY)) {
            preparedStatement.setString(1, entryKey);
            preparedStatement.executeUpdate();
            getConnection().commit();
            result = true;
        } catch (SQLException e) {
            try {
                getConnection().rollback();
            } catch (SQLException ex) {
                LOG.error(ex);
            }
            LOG.error(e);
        }
        return result;
    }

    /**
     * @param entriesKeysList Список уникальных идентификаторов счетчиков
     * @return
     */
    public boolean deleteCounterEntries(List<String> entriesKeysList) {
        boolean result = false;
        if (entriesKeysList.size() == 0 || entriesKeysList == null) return result;
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(SQL_DELETE_COUNTER_ENTRY)) {
            for (String entryKey : entriesKeysList) {
                preparedStatement.setString(1, entryKey);
                preparedStatement.executeUpdate();
                preparedStatement.clearParameters();
            }
            getConnection().commit();
            result = true;
        } catch (SQLException e) {
            try {
                getConnection().rollback();
            } catch (SQLException ex) {
                LOG.error(ex);
            }
            LOG.error(e);
        }
        return result;
    }

    /**
     * @param entryKey Уникальный идентификатор счетчика
     * @return
     */
    public int getCounterEntry(String entryKey) {
        int result = 0;
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(entryKey)) return result;
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(SQL_GET_COUNTER_ENTRY)) {
            preparedStatement.setString(1, entryKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            getConnection().commit();
            while (resultSet.next()) {
                result = resultSet.getInt("COUNTER_COUNT");
            }
            resultSet.close();
        } catch (SQLException e) {
            try {
                getConnection().rollback();
            } catch (SQLException ex) {
                LOG.error(ex);
            }
            LOG.error(e);
        }

        return result;
    }

    /**
     * @return Отображение всех счетчиков из БД
     */
    public Map<String, Integer> getAllCounterEntries() {
        Map<String, Integer> allCounterEntries = new ConcurrentHashMap<>();
        try (Statement statement = getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_GET_COUNTERS);
            getConnection().commit();
            while (resultSet.next()) {
                allCounterEntries.put(resultSet.getString("COUNTER_KEY"), resultSet.getInt("COUNTER_COUNT"));
            }
            resultSet.close();
        } catch (SQLException e) {
            try {
                getConnection().rollback();
            } catch (SQLException ex) {
                LOG.error(ex);
            }
            LOG.error(e);
        }
        return allCounterEntries;
    }

    /**
     * @param entryKey        Уникальный идентификатор счетчика
     * @param counterNewValue Новое целочисленное значения счетчика
     * @return
     */
    public boolean updateCounter(String entryKey, int counterNewValue) {
        boolean result = false;
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(entryKey)) return result;
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(SQL_UPDATE_COUNTER_ENTRY)) {
            preparedStatement.setInt(1, counterNewValue);
            preparedStatement.setString(2, entryKey);
            if (preparedStatement.executeUpdate() > 0) result = true;
        } catch (SQLException e) {
            try {
                getConnection().rollback();
            } catch (SQLException ex) {
                LOG.error(ex);
            }
            LOG.error(e);
        }

        return result;
    }

    /**
     * @param entryKey Уникальный идентификатор счетчика
     */
    public void incrementCounter(String entryKey) {

        int counterTotalCount = 0;
        counterTotalCount = getCounterEntry(entryKey);
        if (counterTotalCount >= 0) {
            counterTotalCount++;
            updateCounter(entryKey, counterTotalCount);
        } else {
            createCounterEntry(entryKey);
        }

    }

    /**
     * @param entryKey Уникальный идентификатор счетчика
     * @return
     */
    public boolean createCounterEntry(String entryKey) {
        boolean result = false;
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(SQL_CREATE_COUNTER_ENTRY)) {
            preparedStatement.setString(1, entryKey);
            preparedStatement.setInt(2, 0);
            preparedStatement.executeUpdate();
            getConnection().commit();
            result = true;
        } catch (SQLException e) {
            try {
                getConnection().rollback();
            } catch (SQLException ex) {
                LOG.error(ex);
            }
            LOG.error(e);
        }

        return result;
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * @param connection Соединение с БД
     */
    public void setConnection(Connection connection) {

        if (connection == null) {
            try {
                OpenCms.getSqlManager().getConnection("default");
            } catch (SQLException e) {
                LOG.error(e);
            }
        } else {
            this.connection = connection;
        }

        try {
            getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            LOG.error(e);
        }
    }

    @Override
    public void close() throws Exception {
        getConnection().close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewsCounter)) return false;
        NewsCounter that = (NewsCounter) o;
        return getConnection().equals(that.getConnection());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getConnection());
    }

    @Override
    public String toString() {
        return "NewsCounter{" +
                "connection=" + connection +
                '}';
    }
}
