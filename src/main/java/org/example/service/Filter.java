package org.example.service;


import org.example.exception.FilterException;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс Filter предназначен для лексического анализа и фильтрации текстового выражения.
 */
public class Filter {
    /**
     * Enum для задания типов лексем.
     */
    public enum LexemeType {
        LEFT_BRACKET, RIGHT_BRACKET, OP_OR, OP_AND, OP_EQUALS, OP_LARGER, OP_LESS, OP_NOT_EQUALS, COLUMN, EOF, VALUE
    }

    /**
     * Класс LexemeBuffer является буфером для списка лексем.
     */
    public static class LexemeBuffer {

        private final Object[] info;

        private int pos;

        public List<Lexeme> lexemes;

        /**
         * Конструктор класса.
         *
         * @param lexemes список лексем.
         * @param info    информация.
         */
        public LexemeBuffer(List<Lexeme> lexemes, Object[] info) {
            this.lexemes = lexemes;
            this.info = info;
        }

        /**
         * Метод получения следующей лексемы.
         *
         * @return следующую лексему.
         */
        public Lexeme next() {
            return lexemes.get(pos++);
        }

        /**
         * Метод возврата на одну лексему назад.
         */
        public void back() {
            pos--;
        }

        /**
         * Метод получения текущей позиции в списке лексем.
         *
         * @return текущую позицию в списке лексем.
         */
        public int getPos() {
            return pos;
        }


    }

    /**
     * Класс Lexeme содержит информацию о лексеме.
     */
    public static class Lexeme {
        LexemeType type;
        String value;

        /**
         * Конструктор класса.
         *
         * @param type  тип лексемы.
         * @param value значение лексемы.
         */
        public Lexeme(LexemeType type, String value) {
            this.type = type;
            this.value = value;
        }

        /**
         * Конструктор класса.
         *
         * @param type  тип лексемы.
         * @param value значение лексемы.
         */
        public Lexeme(LexemeType type, Character value) {
            this.type = type;
            this.value = value.toString();
        }

        /**
         * Метод преобразования объекта в строку.
         *
         * @return строковое представление объекта.
         */
        @Override
        public String toString() {
            return "Lexeme{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }

        /**
         * Метод лексического анализа текстового выражения.
         *
         * @param exText текстовое выражение.
         * @return список лексем.
         * @throws FilterException если текстовое выражение не является корректным.
         */
        public static List<Lexeme> lexAnalyze(String exText) throws FilterException {
            exText = exText.replaceAll("column\\[(\\d+)]", "[$1]");
            ArrayList<Lexeme> lexemes = new ArrayList<>();
            int pos = 0;
            while (pos < exText.length()) {
                char c = exText.charAt(pos);
                switch (c) {
                    case '(': {
                        lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                        pos++;
                        break;
                    }
                    case ')': {
                        lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                        pos++;
                        break;
                    }
                    case '&': {
                        lexemes.add(new Lexeme(LexemeType.OP_AND, c));
                        pos++;
                        break;
                    }
                    case '=': {
                        lexemes.add(new Lexeme(LexemeType.OP_EQUALS, c));
                        pos++;
                        break;
                    }
                    case '>': {
                        lexemes.add(new Lexeme(LexemeType.OP_LARGER, c));
                        pos++;
                        break;
                    }
                    case '[': {
                        StringBuilder sb = new StringBuilder();
                        pos++;
                        if (pos >= exText.length()) {
                            throw new FilterException("Не валидное выражение " + c);
                        }
                        c = exText.charAt(pos);
                        while (c != ']') {
                            sb.append(c);
                            pos++;
                            if (pos >= exText.length()) {
                                throw new FilterException("Не валидное выражение " + c);
                            }
                            c = exText.charAt(pos);
                        }
                        lexemes.add(new Lexeme(LexemeType.COLUMN, sb.toString()));
                        pos++;
                        break;
                    }
                    case '\"': {
                        StringBuilder sb = new StringBuilder();
                        pos++;
                        if (pos >= exText.length()) {
                            throw new FilterException("Не валидное выражение " + c);
                        }
                        c = exText.charAt(pos);
                        while (c != '\"') {
                            sb.append(c);
                            pos++;
                            if (pos >= exText.length()) {
                                throw new FilterException("Не валидное выражение " + c);
                            }
                            c = exText.charAt(pos);
                        }
                        lexemes.add(new Lexeme(LexemeType.VALUE, sb.toString()));
                        pos++;
                        break;
                    }
                    default: {
                        if (c <= '9' && c >= '0') {
                            StringBuilder sb = new StringBuilder();
                            do {
                                sb.append(c);
                                pos++;
                                if (pos >= exText.length()) {
                                    break;
                                }
                                c = exText.charAt(pos);
                            } while (c <= '9' && c >= '0');
                            lexemes.add(new Lexeme(LexemeType.VALUE, sb.toString()));
                        } else if (c == '|') {
                            StringBuilder sb = new StringBuilder();
                            sb.append(c);
                            pos++;
                            if (pos >= exText.length()) {
                                throw new FilterException("Неожиданный символ: " + c);
                            }
                            c = exText.charAt(pos);
                            if (c == '|') {
                                sb.append(c);
                                lexemes.add(new Lexeme(LexemeType.OP_OR, sb.toString()));
                                pos++;
                            } else {
                                throw new FilterException("Неожиданный символ: " + c);
                            }
                        } else if (c == '<') {
                            StringBuilder sb = new StringBuilder();
                            sb.append(c);
                            pos++;
                            if (pos >= exText.length()) {
                                throw new FilterException("Не валидное выражение " + c);
                            }
                            c = exText.charAt(pos);
                            if (c != '>') {
                                lexemes.add(new Lexeme(LexemeType.OP_LESS, sb.toString()));
                            } else {
                                sb.append(c);
                                lexemes.add(new Lexeme(LexemeType.OP_NOT_EQUALS, sb.toString()));
                                pos++;
                            }
                        } else {
                            if (c != ' ') {
                                throw new FilterException("Не валидное выражение " + c);
                            }
                            pos++;
                        }
                    }
                }
            }
            lexemes.add(new Lexeme(LexemeType.EOF, ""));
            return lexemes;
        }
    }

    public static Boolean and(LexemeBuffer lexemes) throws FilterException {
        Boolean expression = factor(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_AND: {
                    expression = expression & factor(lexemes);
                }
                default: {
                    lexemes.back();
                    return expression;
                }
            }
        }

    }

    public static Boolean or(LexemeBuffer lexemes) throws FilterException {
        boolean expression = and(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_OR:
                    expression = expression | and(lexemes);
                default: {
                    lexemes.back();
                    return expression;
                }
            }
        }
    }

    public static Boolean expr(LexemeBuffer lexemes) throws FilterException {
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeType.EOF) {
            throw new FilterException("Не верное количество скобок");
        } else {
            lexemes.back();
            return or(lexemes);
        }
    }

    public static Boolean factor(LexemeBuffer lexemes) throws FilterException {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type) {
            case COLUMN: {
                int index = Integer.parseInt(lexeme.value);
                if (index == 1) {
                    index--;
                } else if (index > 2 && index < 15) {
                    index -= 2;
                } else {
                    throw new ArrayIndexOutOfBoundsException("Такого индекса нет в массиве");
                }
                Object currentColumnValue = lexemes.info[index];
                lexeme = lexemes.next();
                switch (lexeme.type) {
                    case OP_LARGER: {
                        lexeme = lexemes.next();
                        if (currentColumnValue instanceof Integer) {
                            return (Integer) currentColumnValue > Integer.parseInt(lexeme.value);
                        } else if (currentColumnValue instanceof Double) {
                            return (Double) currentColumnValue > Double.parseDouble(lexeme.value);
                        } else {
                            throw new FilterException("Операция не применима к этому индексу");
                        }
                    }
                    case OP_LESS: {
                        lexeme = lexemes.next();
                        if (currentColumnValue instanceof Integer) {
                            return (Integer) currentColumnValue < Integer.parseInt(lexeme.value);
                        } else if (currentColumnValue instanceof Double) {
                            return (Double) currentColumnValue < Double.parseDouble(lexeme.value);
                        } else {
                            throw new FilterException("Операция не применима к этому индексу");
                        }
                    }
                    case OP_EQUALS: {
                        lexeme = lexemes.next();
                        if (currentColumnValue instanceof String) {
                            if (isNumeric(lexeme.value)) {
                                return ((String) currentColumnValue).equalsIgnoreCase(lexeme.value);
                            } else if (isDouble(lexeme.value)) {
                                return ((String) currentColumnValue).equalsIgnoreCase(lexeme.value);
                            }
                            return ((String) currentColumnValue).equalsIgnoreCase(lexeme.value);
                        } else if (currentColumnValue instanceof Integer) {
                            return (Integer) currentColumnValue == Integer.parseInt(lexeme.value);
                        } else if (currentColumnValue instanceof Double) {
                            return (Double) currentColumnValue == Double.parseDouble(lexeme.value);
                        }
                    }
                    case OP_NOT_EQUALS: {
                        lexeme = lexemes.next();
                        if (currentColumnValue instanceof String) {
                            if (isNumeric(lexeme.value)) {
                                return !((String) currentColumnValue).equalsIgnoreCase(lexeme.value);
                            } else if (isDouble(lexeme.value)) {
                                return !((String) currentColumnValue).equalsIgnoreCase(lexeme.value);
                            }
                            return !((String) currentColumnValue).equalsIgnoreCase(lexeme.value);
                        } else if (currentColumnValue instanceof Integer) {
                            return (Integer) currentColumnValue != Integer.parseInt(lexeme.value);
                        } else if (currentColumnValue instanceof Double) {
                            return (Double) currentColumnValue != Double.parseDouble(lexeme.value);
                        }
                    }
                }
            }
            case LEFT_BRACKET: {
                boolean expression = expr(lexemes);
                lexeme = lexemes.next();
                if (lexeme.type != LexemeType.RIGHT_BRACKET) {
                    throw new FilterException("Не корректное выражение  " + lexeme.value + " на месте " + lexemes.getPos());
                }
                return expression;
            }
            default:
                throw new FilterException("Не корректное выражение " + lexeme.value + " на месте " + lexemes.getPos());
        }
    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}




