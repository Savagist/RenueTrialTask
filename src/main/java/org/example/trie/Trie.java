package org.example.trie;

import org.example.exception.FilterException;

/**
 * Интерфейс для реализации префиксного дерева (trie).
 */
public interface Trie {
    /**
     * Метод для вставки слова и связанной с ним информации в дерево.
     *
     * @param word Слово, которое необходимо добавить в дерево.
     * @param info Массив объектов с информацией, связанной со словом.
     */
    void insert(String word, Object[] info);

    /**
     * Метод для поиска слов, начинающихся с заданного префикса, и фильтрации результатов по заданному фильтру.
     *
     * @param prefix Префикс для поиска слов.
     * @param filter Фильтр для фильтрации результатов.
     * @throws FilterException Исключение, которое может быть вызвано в случае, если фильтр не может быть применен к результатам.
     */
    void find(String prefix, String filter) throws FilterException;
}
