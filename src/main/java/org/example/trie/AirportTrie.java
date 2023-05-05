package org.example.trie;

import org.example.exception.FilterException;
import org.example.service.Filter;
import org.example.entity.Airport;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.example.service.Filter.Lexeme.lexAnalyze;
import static org.example.service.Filter.expr;

class AirportTrieNode {
    private final Map<Character, AirportTrieNode> children;
    private final Map<String, String> originalName;
    private final Airport airport;
    private boolean isWord;

    public AirportTrieNode() {
        this.airport = new Airport();
        this.children = new HashMap<>();
        this.originalName = new HashMap<>();
        this.isWord = false;
    }

    public Map<Character, AirportTrieNode> getChildren() {
        return children;
    }

    public void setWord(boolean value) {
        isWord = value;
    }

    public boolean isWord() {
        return isWord;
    }

    public Map<String, String> getOriginalName() {
        return originalName;
    }

    public Airport getAirport() {
        return airport;
    }
}

public class AirportTrie implements Trie {
    private final AirportTrieNode root;

    public AirportTrie() {
        this.root = new AirportTrieNode();
    }

    @Override
    public void insert(String word, Object[] info) {
        AirportTrieNode current = root;

        String pureWord = word.toLowerCase().replace("\"", "");
        for (char c : pureWord.toCharArray()) {
            current = current.getChildren().computeIfAbsent(c, l -> new AirportTrieNode());
            current.getAirport().addInformation(info);
        }
        current.setWord(true);
        if (!current.getOriginalName().containsKey(pureWord)) {
            current.getOriginalName().put(pureWord, word);
        }
    }

    @Override
    public void find(String prefix, String filter) throws FilterException {
        Instant start = Instant.now();
        AirportTrieNode current = root;
        String prefixLowerCase = prefix.toLowerCase();
        for (char c : prefixLowerCase.toCharArray()) {
            AirportTrieNode node = current.getChildren().get(c);
            if (node == null) {
                System.out.println("По префиксу '" + prefix + "' нет строк");
                return;
            }
            current = node;
        }
        List<String> airports = new ArrayList<>();
        StringBuilder sb = new StringBuilder(prefixLowerCase);
        List<Filter.Lexeme> lexemes = null;
        if (!filter.isEmpty()) {
            lexemes = lexAnalyze(filter);
        }
        getAllAirports(current, sb, airports, lexemes);
        Instant stop = Instant.now();
        airports.sort((o1, o2) -> {
            String[] a1 = o1.split("\"");
            String[] a2 = o2.split("\"");
            return a1[1].compareTo(a2[1]);
        });
        for (String airport : airports) {
            System.out.println(airport);
        }
        System.out.println("Количество найденных строк: " + airports.size());
        System.out.println("Время, затраченное на поиск: " + Duration.between(start, stop).toMillis() + " мс");
    }

    public void getAllAirports(AirportTrieNode node, StringBuilder sb, List<String> result, List<Filter.Lexeme> lexemes) throws FilterException {
        if (node.isWord()) {
            String originalName = node.getOriginalName().get(sb.toString());
            for (Object[] information : node.getAirport().getInformation()) {
                String toString = Arrays.toString(information);
                if (lexemes == null) {
                    result.add(originalName + " " + toString);
                } else {
                    Filter.LexemeBuffer buffer = new Filter.LexemeBuffer(lexemes, information);
                    if (Boolean.TRUE.equals(expr(buffer)))
                        result.add(originalName + " " + toString);
                }
            }
        } else {
            for (Map.Entry<Character, AirportTrieNode> entry : node.getChildren().entrySet()) {
                sb.append(entry.getKey());
                getAllAirports(entry.getValue(), sb, result, lexemes);
                sb.deleteCharAt(sb.length() - 1);
            }
        }
    }
}
