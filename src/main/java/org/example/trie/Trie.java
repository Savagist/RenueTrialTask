package org.example.trie;

import org.example.exception.FilterException;

public interface Trie {
    void insert(String word, Object[] info);
    void find(String prefix, String filter) throws FilterException;
}
