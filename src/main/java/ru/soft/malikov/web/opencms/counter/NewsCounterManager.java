package ru.soft.malikov.web.opencms.counter;

import org.apache.commons.logging.Log;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

public class NewsCounterManager implements CounterManager {

    private static final String DB_TABLE_NAME = "CMS_NCFU_NEWS_COUNTERS";
    private static final String SQL_CHECK_DB = "SELECT count(*) FROM " + DB_TABLE_NAME + ";";
    private static final String SQL_CHECK_COUNTER = "SELECT count(*) FROM " + DB_TABLE_NAME + " WHERE COUNTER_KEY= ?;";

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

    private static final Log LOG = CmsLog.getLog(NewsCounterManager.class);


    public NewsCounterManager() {
    }

    /**
     * Метод для удаления счетчика
     *
     * @param counterKey Уникальный идентификатор счетчика
     * @return "true" в случае успеха
     */
    public boolean deleteCounter(String counterKey) {
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(counterKey)) return false;
        try (Connection conn = OpenCms.getSqlManager().getConnection("default");
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_COUNTER_ENTRY)
        ) {
            stmt.setString(1, counterKey);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOG.error(ex);
            return false;
        }

        return true;
    }

    /**
     * Метод для получения текущего значения счетчика по заданному идентификатору
     *
     * @param counterKey Уникальный идентификатор счетчика
     * @return Текущее значение счетчика
     */
    public Integer getCounterValue(String counterKey) {
        Integer result = 0;
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(counterKey)) return result;
        try (Connection conn = OpenCms.getSqlManager().getConnection("default");
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_COUNTER_ENTRY)
        ) {
            stmt.setString(1, counterKey);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result = rs.getInt("COUNTER_COUNT");
            }
            rs.close();
        } catch (SQLException ex) {
            LOG.error(ex);
        }
        return result;
    }

    /**
     * Метод для установки значения счетчика по заданному идентификатору
     *
     * @param counterKey Уникальный идентификатор счетчика
     * @param newValue   Новое значение счетчика
     * @return "true" в случае успеха
     */
    public boolean setCounterValue(String counterKey, int newValue) {
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(counterKey)) return false;
        if (!findCounter(counterKey)) createCounter(counterKey, newValue);
        try (Connection conn = OpenCms.getSqlManager().getConnection("default");
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_COUNTER_ENTRY)
        ) {
            stmt.setInt(1, newValue);
            stmt.setString(2, counterKey);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOG.error(ex);
        }

        return false;
    }

    /**
     * Метод для получения всех счетчиков, зарегистрированных в системе
     *
     * @return Коллекцию всех счетчиков
     */
    public TreeMap getAllCounters() {
        TreeMap result = new TreeMap();
        try (Connection conn = OpenCms.getSqlManager().getConnection("default");
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_COUNTERS)
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getString(1), rs.getInt(2));
            }
            rs.close();
        } catch (SQLException ex) {
            LOG.error(ex);
        }
        return result;
    }

    /**
     * Метод для инкрементации значения счетчика с заданным идентификатором
     *
     * @param counterKey Уникальный идентификатор счетчика
     * @return Результирующее значение счетчика
     */
    public int incrementCounter(String counterKey) {
        int result = 0;
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(counterKey)) return result;
        if (!findCounter(counterKey)) {
            result++;
            createCounter(counterKey, result);
            return result;
        } else {
            result = getCounterValue(counterKey);
            result++;
            setCounterValue(counterKey, result);
        }

        return result;
    }

    private boolean findCounter(String counterKey) {
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(counterKey)) return false;
        try (Connection conn = OpenCms.getSqlManager().getConnection("default");
             PreparedStatement stmt = conn.prepareStatement(SQL_CHECK_COUNTER)
        ) {
            stmt.setString(1, counterKey);
            ResultSet rs = stmt.executeQuery();
            int res = 0;
            while (rs.next()) {
                res = rs.getInt("count");
            }
            rs.close();
            if (res > 0) return true;
        } catch (SQLException ex) {
            LOG.error(ex);
        }

        return false;
    }

    private boolean createCounter(String counterKey, int counterValue) {
        try (Connection conn = OpenCms.getSqlManager().getConnection("default");
             PreparedStatement stmt = conn.prepareStatement(SQL_CREATE_COUNTER_ENTRY)
        ) {
            stmt.setString(1, counterKey);
            stmt.setInt(2, counterValue);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            LOG.error(ex);
        }
        return false;
    }

}
