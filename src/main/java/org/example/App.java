package org.example;

import org.example.exception.FilterException;
import org.example.service.Data;
import org.example.service.UI;
import org.example.trie.Trie;

import java.io.*;

public class App {
    public static void main(String[] args) throws IOException, FilterException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            Trie airports = Data.readData();
            UI.processUserRequest(reader, airports);
        }
    }
}