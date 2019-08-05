package ru.soft.malikov.web.opencms.counter;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Интерфейс для работы со счетчиком
 *
 * @author Alexandr Malikov
 */
public interface Counter {

    /**
     * Метод, отвечающий за удаление счетчика с заданным идентификатором
     *
     * @param entryKey Уникальный идентификатор счетчика
     * @return "true" - в случае успеха выполнения операции
     */
    boolean deleteCounterEntry(String entryKey);

    /**
     * Метод, отвечающий за групповое удаление счетчиков
     *
     * @param entriesKeysList Список уникальных идентификаторов счетчиков
     * @return "true" - в случае успеха выполнения операции
     */
    boolean deleteCounterEntries(List<String> entriesKeysList);

    /**
     * Метод, отвечающий за возврат целочисленного значения счетчика по его идентификатору
     *
     * @param entryKey Уникальный идентификатор счетчика
     * @return Целочисленное значение счетчика с заданным идентификатором
     */
    int getCounterEntry(String entryKey);

    /**
     * Метод, отвечающий за возврат отображения всех счетчиков из БД
     *
     * @return Отображение всех счетчиков из БД
     */
    Map<String, Integer> getAllCounterEntries();

    /**
     * Метод, отвечающий за обновление значения счетчика с заданным идентификатором
     *
     * @param entryKey        Уникальный идентификатор счетчика
     * @param counterNewValue Новое целочисленное значения счетчика
     * @return "true" - в случае успеха выполнения операции
     */
    boolean updateCounter(String entryKey, int counterNewValue);

    /**
     * Метод, отвечающий за инкрементацию счетчика с заданным идентификатором
     *
     * @param entryKey Уникальный идентификатор счетчика
     */
    void incrementCounter(String entryKey);

    /**
     * Метод, отвечающий за создание счетчика с заданным идентификатором
     *
     * @param entryKey Уникальный идентификатор счетчика
     * @return "true" - в случае успеха выполнения операции
     */
    boolean createCounterEntry(String entryKey);

    /**
     * Метод, отвечающий за возврат соединения с БД
     *
     * @return Соединение с БД
     */
    Connection getConnection();

    /**
     * Метод, отвечающий за установку соединения с БД
     *
     * @param connection Соединение с БД
     */
    void setConnection(Connection connection);

}
