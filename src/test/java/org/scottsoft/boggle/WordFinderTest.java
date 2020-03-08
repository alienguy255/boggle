package org.scottsoft.boggle;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class WordFinderTest {

    @Test
    void testFindWords() {
        char[][] board = new char[][] {
            {'a', 't', 'e', 'g'},
            {'y', 'p', 'p', 'r'},
            {'o', 'o', 'l', 'e'},
            {'t', 'c', 'v', 't'}
        };

        WordTrie wordTrie = new WordTrie();
        wordTrie.loadWords();

        WordFinder wordFinder = new WordFinder(wordTrie);
        List<String> words = wordFinder.findWords(board);
        log.info("Found words={}", words);
    }

}
