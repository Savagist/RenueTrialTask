package org.example.trie;

import org.example.exception.FilterException;
import org.example.service.Filter;
import org.example.entity.Airport;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.example.service.Filter.Lexeme.lexAnalyze;
import static org.example.service.Filter.expr;

/**
 * Класс AirportTrieNode представляет узел для префиксного дерева (Trie) для хранения аэропортов и их названий.
 */
class AirportTrieNode {
    /**
     * Коллекция, хранящая ссылки на дочерние узлы этого узла.
     */
    private final Map<Character, AirportTrieNode> children;
    /**
     * Оригинальное название аэропорта и его перевод на английский язык.
     */
    private Map<String, String> originalName;
    /**
     * Аэропорт, хранимый в данном узле.
     */
    private Airport airport;
    /**
     * Признак того, является ли данное слово (название аэропорта) полным словом в Trie.
     */
    private boolean isWord;

    /**
     * Конструктор класса AirportTrieNode.
     * Инициализирует дочерние узлы, оригинальное название аэропорта и признак того, является ли слово полным в Trie.
     */
    public AirportTrieNode() {
        this.airport = null;
        this.children = new HashMap<>();
        this.originalName = null;
        this.isWord = false;
    }

    /**
     * Метод, возвращающий коллекцию, хранящую ссылки на дочерние узлы данного узла.
     *
     * @return Коллекция дочерних узлов.
     */
    public Map<Character, AirportTrieNode> getChildren() {
        return children;
    }

    /**
     * Метод, устанавливающий признак того, является ли слово (название аэропорта) полным в Trie.
     *
     * @param value Признак того, является ли слово полным в Trie.
     */
    public void setWord(boolean value) {
        isWord = value;
    }

    /**
     * Метод, возвращающий признак того, является ли слово (название аэропорта) полным в Trie.
     *
     * @return Признак того, является ли слово полным в Trie.
     */
    public boolean isWord() {
        return isWord;
    }

    /**
     * Метод, возвращающий оригинальное название аэропорта и название в нижнем регистре.
     *
     * @return Коллекция, содержащая оригинальное название аэропорта и название в нижнем регистре.
     */
    public Map<String, String> getOriginalName() {
        return originalName;
    }

    /**
     * Метод, создающий коллекцию с оригинальным названием аэропорта и его значение в нижнем регистре.
     *
     * @param name         Название аэропорта в нижнем регистре.
     * @param originalName Оригинальное название аэропорта.
     */
    public void setOriginalName(String name, String originalName) {
        this.originalName = new HashMap<>();
        getOriginalName().put(name, originalName);
    }

    /**
     * Метод, возвращающий  объект аэропорт.
     *
     * @return Объект, содержащий дополнительную информацию о аэропорте.
     */
    public Airport getAirport() {
        return airport;
    }

    /**
     * Метод, устанавливает аэропорт.
     *
     * @param airport Объект аэропорта на который будет указывать текущий узел.
     */
    public void setAirport(Airport airport) {
        this.airport = airport;
    }
}

/**
 * Этот класс представляет собой реализацию префиксного дерева для поиска информации о аэропортах.
 * Реализация основана на использовании класса TrieNode.
 */
public class AirportTrie implements Trie {
    private final AirportTrieNode root;

    /**
     * Конструктор класса, создающий новый объект префиксного дерева.
     */
    public AirportTrie() {
        this.root = new AirportTrieNode();
    }

    /**
     * Метод для добавления нового слова в префиксное дерево.
     *
     * @param word слово, которое нужно добавить в дерево.
     * @param info массив информации об аэропорте.
     */
    @Override
    public void insert(String word, Object[] info) {
        AirportTrieNode current = root;
        String pureWord = word.toLowerCase();
        for (char c : pureWord.toCharArray()) {
            AirportTrieNode childNode = current.getChildren().get(c);
            if (childNode == null) {
                childNode = new AirportTrieNode();
                current.getChildren().put(c, childNode);
            }
            current = childNode;
        }
        if (current.getAirport() == null) {
            current.setAirport(new Airport());
            current.setWord(true);
            current.setOriginalName(pureWord, word);
        }
        current.getAirport().addInformation(info);
    }

    /**
     * Метод для поиска всех слов в дереве, начинающихся с заданного префикса и удовлетворяющих заданному фильтру.
     *
     * @param prefix префикс, с которого начинаются слова, которые нужно найти.
     * @param filter фильтр, который нужно применить к результатам поиска.
     * @throws FilterException если фильтр содержит неверное выражение.
     */
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

    /**
     * Вспомогательный метод для рекурсивного поиска всех слов, начинающихся с заданного префикса, в дереве.
     *
     * @param node    узел, с которого начинается поиск.
     * @param sb      StringBuilder, который используется для формирования слова в процессе поиска.
     * @param result  список строк, в которые добавляются найденные слова.
     * @param lexemes список лексем, которые нужно применить к найденным словам.
     * @throws FilterException если фильтр содержит неверное выражение.
     */
    public void getAllAirports(AirportTrieNode node, StringBuilder sb, List<String> result, List<Filter.Lexeme> lexemes) throws FilterException {
        if (node.isWord()) {
            for (Object[] information : node.getAirport().getInformation()) {
                String originalName;
                if (lexemes == null) {
                    originalName = node.getOriginalName().get(sb.toString());
                    result.add(output(information, originalName));
                } else {
                    Filter.LexemeBuffer buffer = new Filter.LexemeBuffer(lexemes, information);
                    if (Boolean.TRUE.equals(expr(buffer))) {
                        originalName = node.getOriginalName().get(sb.toString());
                        result.add(output(information, originalName));
                    }
                }
            }
        }
        for (Map.Entry<Character, AirportTrieNode> entry : node.getChildren().entrySet()) {
            sb.append(entry.getKey());
            getAllAirports(entry.getValue(), sb, result, lexemes);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    /**
     * Вспомогательный метод для формирования строки вывода информации об аэропорте в требуемом формате.
     *
     * @param information массив информации об аэропорте.
     * @param name        название аэропорта.
     * @return строка вывода информации об аэропорте.
     */
    private String output(Object[] information, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(name);
        sb.append("\"");
        sb.append(" [");
        for (int i = 0; i < information.length; i++) {
            if (information[i] instanceof String && i < information.length - 1) {
                if (i == 8) {
                    sb.append(information[i]);
                    sb.append(", ");
                    continue;
                }
                sb.append("\"");
                sb.append(information[i]);
                sb.append("\"");
                sb.append(", ");
            } else if (i == information.length - 1) {
                sb.append("\"");
                sb.append(information[i]);
                sb.append("\"");
            } else {
                sb.append(information[i]);
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
