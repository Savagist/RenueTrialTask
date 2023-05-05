package org.example.service;

import org.example.trie.AirportTrie;
import org.example.trie.Trie;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс, представляющий собой загрузчик данных из CSV-файла и сохраняющий их в Trie-структуру.
 * Используется файл "airports.csv" в корневой директории проекта.
 */
public class Data {
    /**
     * Имя CSV-файла, из которого будут загружены данные.
     */
    private static final String FILE_NAME = "airports.csv";

    /**
     * Загружает данные из CSV-файла и сохраняет их в Trie-структуру.
     *
     * @return Trie-структуру, содержащую данные об аэропортах.
     * @throws IOException если не удается прочитать файл.
     */
    public static Trie readData() throws IOException {
        List<String> lines;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_NAME))) {
            lines = reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new IOException("Не удается прочитать файл: " + FILE_NAME, e);
        }
        return loadDataIntoTrie(lines);
    }

    /**
     * Загружает данные из списка строк в Trie-структуру.
     *
     * @param lines список строк, содержащих данные об аэропортах в формате CSV.
     * @return Trie-структуру, содержащую данные об аэропортах.
     */
    private static Trie loadDataIntoTrie(List<String> lines) {
        Trie trie = new AirportTrie();
        Iterator<String> iterator = lines.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            String[] content = parseCsvLine(line);
            String airportName = extractNameAirport(content);
            Object[] airportInfo = extractInformationAboutAirport(content);
            trie.insert(airportName, airportInfo);
            iterator.remove();
        }
        return trie;
    }

    /**
     * Извлекает информацию об аэропорте из строки CSV-файла.
     *
     * @param content массив строк, содержащий информацию об аэропорте.
     * @return массив объектов, содержащий информацию об аэропорте.
     */
    private static Object[] extractInformationAboutAirport(String[] content) {
        Object[] data = new Object[content.length - 1];
        int index = 0;
        for (int i = 0; i < content.length && index < data.length; i++) {
            switch (i) {
                case 0:
                case 8: {
                    data[index] = Integer.parseInt(content[i]);
                    index++;
                    break;
                }
                case 2:
                case 3:
                case 4:
                case 5:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13: {
                    data[index] = content[i];
                    index++;
                    break;
                }
                case 6:
                case 7: {
                    data[index] = Double.parseDouble(content[i]);
                    index++;
                    break;
                }
            }
        }
        return data;
    }

    /**
     * Извлекает имя аэропорта из строки CSV-файла.
     *
     * @param content массив строк, содержащий информацию об аэропорте.
     * @return имя аэропорта.
     */
    private static String extractNameAirport(String[] content) {
        return content[1];
    }

    /**
     * Разбивает строку CSV-файла на отдельные поля.
     *
     * @param line строка CSV-файла.
     * @return массив строк, содержащий отдельные поля.
     */
    private static String[] parseCsvLine(String line) {
        String[] parts = new String[14];
        StringBuilder sb = new StringBuilder();
        boolean counter_flag = false;
        for (int i = 0, j = 0; j < line.length(); j++) {
            char current = line.charAt(j);
            switch (current) {
                case ',':
                    if (counter_flag) {
                        sb.append(current);
                    } else {
                        parts[i] = sb.toString();
                        sb = new StringBuilder();
                        i++;
                    }
                    break;
                case '\"':
                    counter_flag = !counter_flag;
                    break;
                default:
                    sb.append(current);
                    break;
            }
            parts[i] = sb.toString();
        }
        return parts;

    }
}
