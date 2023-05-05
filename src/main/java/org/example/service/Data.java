package org.example.service;

import org.example.trie.AirportTrie;
import org.example.trie.Trie;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Data {

    private static final String FILE_NAME = "airports.csv";

    public static Trie readData() {
        List<String> lines;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_NAME))) {
            lines = reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Не удается прочитать файл: " + FILE_NAME, e);
        }
        return loadDataIntoTrie(lines);
    }

    private static Trie loadDataIntoTrie(List<String> lines) {
        Trie trie = new AirportTrie();
        for (String line : lines) {
            String[] content = parseCsvLine(line);
            String airportName = extractNameAirport(content);
            Object[] airportInfo = extractInformationAboutAirport(content);
            trie.insert(airportName, airportInfo);
        }
        return trie;
    }

    private static Object[] extractInformationAboutAirport(String[] content) {
        Object[] data = new Object[content.length - 1];
        int index = 0;
        for (int i = 0; i < content.length && index < data.length; i++) {
            switch (i) {
                case 0:case 8: {
                    data[index] = Integer.valueOf(content[i]);
                    index++;
                    break;
                }
                case 2:case 3:case 4:case 5:case 9:case 10:case 11:case 12:case 13: {
                    data[index] = content[i];
                    index++;
                    break;
                }
                case 6:case 7:{
                    data[index] = Double.valueOf(content[i]);
                    index++;
                    break;
                }
            }
        }
        return data;
    }

    private static String extractNameAirport(String[] content) {
        return content[1];
    }

    private static String[] parseCsvLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    }
}
