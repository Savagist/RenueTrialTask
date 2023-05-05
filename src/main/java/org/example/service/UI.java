package org.example.service;

import org.example.exception.FilterException;
import org.example.trie.Trie;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

public class UI {
    private static final String END_WORK = "!quit";

    private static String getUserInput(BufferedReader reader, String request) throws IOException {
        System.out.print(request);
        return reader.readLine();
    }

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
