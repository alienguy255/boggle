package org.scottsoft.boggle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WordTrie {

    private final Node root = new Node();

    void add(String word) {
        Node n = root;
        for (int index = 0; index < word.length(); index++) {
            n = n.addChild(word.charAt(index));
        }
        n.setKnownWord(true);
    }

    boolean isKnown(String word) {
        Node n = searchNodes(word);
        return n != null && n.isKnownWord();
    }

    boolean isPrefix(String word) {
        return searchNodes(word) != null;
    }

    private Node searchNodes(String word) {
        Node n = root;
        for (int index=0;index < word.length();index++) {
            char ch = word.charAt(index);
            if (n.getChild(ch) != null) {
                n = n.getChild(ch);
            } else {
                return null;
            }
        }
        return n;
    }

    @Data
    private static class Node {
        private final Map<Character, Node> childNodes = new HashMap<>();
        private boolean knownWord;

        Node addChild(Character ch) {
            return childNodes.computeIfAbsent(ch, c -> new Node());
        }

        Node getChild(Character c) {
            return childNodes.get(c);
        }
    }

    @PostConstruct
    public void loadWords() {
        InputStream wordsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("en-US.dic");
        if (wordsStream == null) {
            throw new IllegalStateException("Words text file could not be loaded");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(wordsStream))) {
            log.info("Begin loading trie with english words...");
            int numWordsLoaded = 0;
            while (br.ready()) {
                String word = br.readLine();
                add(word.toLowerCase());
                numWordsLoaded++;
            }
            log.info("Completed loading {} words into memory", numWordsLoaded);
        } catch (IOException e) {
            log.error("An error occurred loading trie", e);
        }
    }

}

