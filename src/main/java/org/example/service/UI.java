package org.example.service;

import org.example.exception.FilterException;
import org.example.trie.Trie;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

/**
 * Класс UI предоставляет методы для взаимодействия с пользователем через консоль.
 */
public class UI {
    /**
     * Константа, обозначающая окончание работы программы.
     */
    private static final String END_WORK = "!quit";

    /**
     * Получает ввод пользователя через консоль.
     *
     * @param reader  объект BufferedReader для чтения ввода пользователя.
     * @param request строка, содержащая запрос к пользователю.
     * @return ввод пользователя в виде строки.
     * @throws IOException если возникает ошибка ввода-вывода.
     */
    private static String getUserInput(BufferedReader reader, String request) throws IOException {
        System.out.print(request);
        return reader.readLine();
    }

    /**
     * Обрабатывает запросы пользователя до тех пор, пока не будет получена команда завершения работы программы.
     *
     * @param reader   объект BufferedReader для чтения ввода пользователя.
     * @param airports объект Trie, содержащий данные об аэропортах.
     * @throws IOException     если возникает ошибка ввода-вывода.
     * @throws FilterException если возникает ошибка при фильтрации данных.
     */
    public static void processUserRequest(BufferedReader reader, Trie airports) throws IOException, FilterException {
        String filter;
        String prefix;
        while (!Objects.equals(filter = getUserInput(reader, "Введите фильтр или оставте поле пустым (для окончания работы программы напишите '!quit'): "), END_WORK)) {
            prefix = getUserInput(reader, "Введите префикс аэропорта или оставьте его пустым (для окончания работы программы напишите '!quit'): ");
            if (!Objects.equals(prefix, END_WORK)) {
                airports.find(prefix, filter);
            } else {
                break;
            }
        }
        System.out.println("Конец работы программы");
    }

}
