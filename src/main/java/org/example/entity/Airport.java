package org.example.entity;

import java.util.ArrayList;
import java.util.List;


/**
 * Класс, представляющий аэропорт.
 */
public class Airport {
    /**
     * Список информации об аэропорте.
     */
    private final List<Object[]> information;

    /**
     * Конструктор по умолчанию. Создает пустой список информации об аэропорте.
     */
    public Airport() {
        this.information = new ArrayList<>();
    }

    /**
     * Получает список информации об аэропорте.
     * @return список информации об аэропорте
     */
    public List<Object[]> getInformation() {
        return information;
    }

    /**
     * Добавляет информацию об аэропорте в список.
     * @param information информация об аэропорте
     */
    public void addInformation(Object[] information) {
        this.information.add(information);
    }
}