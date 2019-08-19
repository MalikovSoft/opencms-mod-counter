package ru.soft.malikov.web.opencms.counter;

import java.util.TreeMap;

public interface CounterManager {

    /**
     * Метод для удаления счетчика
     *
     * @param counterKey Уникальный идентификатор счетчика
     * @return "true" в случае успеха
     */
    boolean deleteCounter(String counterKey);

    /**
     * Метод для получения текущего значения счетчика по заданному идентификатору
     *
     * @param counterKey Уникальный идентификатор счетчика
     * @return Текущее значение счетчика
     */
    Integer getCounterValue(String counterKey);

    /**
     * Метод для установки значения счетчика по заданному идентификатору
     *
     * @param counterKey Уникальный идентификатор счетчика
     * @param newValue   Новое значение счетчика
     * @return "true" в случае успеха
     */
    boolean setCounterValue(String counterKey, int newValue);

    /**
     * Метод для получения всех счетчиков, зарегистрированных в системе
     *
     * @return Коллекцию всех счетчиков
     */
    TreeMap getAllCounters();

    /**
     * Метод для инкрементации значения счетчика с заданным идентификатором
     *
     * @param counterKey Уникальный идентификатор счетчика
     * @return Результирующее значение счетчика
     */
    int incrementCounter(String counterKey);

}
