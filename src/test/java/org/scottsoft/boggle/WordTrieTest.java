package org.scottsoft.boggle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class WordTrieTest {

    @Test
    void testPrefixCheck() {
        WordTrie wordTrie = new WordTrie();
        wordTrie.add("horse");
        wordTrie.add("horses");
        wordTrie.add("house");
        wordTrie.add("horror");

        // prefix checking works
        assertTrue(wordTrie.isPrefix("h"));
        assertTrue(wordTrie.isPrefix("ho"));
        assertTrue(wordTrie.isPrefix("hor"));
        assertTrue(wordTrie.isPrefix("hors"));

        // assert that "hor" is not a known word
        assertFalse(wordTrie.isKnown("hor"));

        assertTrue(wordTrie.isKnown("horse"));
        assertTrue(wordTrie.isKnown("horses"));
        assertTrue(wordTrie.isKnown("house"));
        assertTrue(wordTrie.isKnown("horror"));
    }

    @Test
    void testSingularAndPlural() {
        WordTrie wordTrie = new WordTrie();
        wordTrie.add("boat");
        wordTrie.add("boats");

        assertTrue(wordTrie.isKnown("boat"));
        assertTrue(wordTrie.isKnown("boats"));
    }

    @Test
    void testEmptyTrie() {
        WordTrie wordTrie = new WordTrie();
        assertFalse(wordTrie.isKnown("house"));
        assertFalse(wordTrie.isKnown("cat"));
    }

}
