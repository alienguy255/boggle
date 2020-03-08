package org.scottsoft.boggle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class WordFinder {

    private final WordTrie wordTrie;

    private enum Direction {
        NORTH_WEST((row, col) -> new RowColumn(row - 1, col - 1)),
        NORTH((row, col) -> new RowColumn(row - 1, col)),
        NORTH_EAST((row, col) -> new RowColumn(row - 1, col + 1)),
        EAST((row, col) -> new RowColumn(row, col + 1)),
        SOUTH_EAST((row, col) -> new RowColumn(row + 1, col + 1)),
        SOUTH((row, col) -> new RowColumn(row + 1, col)),
        SOUTH_WEST((row, col) -> new RowColumn(row + 1, col - 1)),
        WEST((row, col) -> new RowColumn(row, col - 1));

        private final BiFunction<Integer, Integer, RowColumn> directionCoordFunction;

        Direction(BiFunction<Integer, Integer, RowColumn> directionCoordFunction) {
            this.directionCoordFunction = directionCoordFunction;
        }
    }

    List<String> findWords(char[][] board) {
        List<FoundWord> foundWords = new ArrayList<>();
        for (int row = 0;row < board.length;row++) {
            for (int col = 0;col < board[row].length;col++) {
                findWords(board, row, col, foundWords);
            }
        }

        return foundWords.stream()
            .peek(fw -> log.debug("Found word={}, path={}", fw.getFoundWord(), fw.getPath()))
            .map(FoundWord::getFoundWord)
            // remove duplicates for values returned to client
            .distinct()
            .collect(Collectors.toList());
    }

    private void findWords(char[][] board, int row, int col, List<FoundWord> foundWords) {
        findWords(board, new HashMap<>(), row, col, "", foundWords, "");
    }

    private void findWords(char[][] board, HashMap<RowColumn, Boolean> visited, int row, int col, String partialWord, List<FoundWord> foundWords, String pathString) {
        // mark the current tile as visited:
        visited.put(new RowColumn(row, col), true);

        // add the current positions letter:
        String candidateWord = partialWord + board[row][col];

        String updatedPathString;
        if (isKnownWord(candidateWord)) {
            // found a known word, add it to the found words list
            updatedPathString = pathString + "(" + row + ", " + col + ")";

            // NOTE: a list is used here in the case when the same word is found from different paths
            foundWords.add(new FoundWord(candidateWord.toLowerCase(), updatedPathString));
        }

        if (isPrefix(candidateWord)) {
            // word is a prefix to a known word, keep searching
            updatedPathString = pathString + "(" + row + ", " + col + ") --> ";
            for (Direction dir : Direction.values()) {
                // visit each adjacent tile that has not already been visited
                RowColumn rowCol = dir.directionCoordFunction.apply(row, col);
                if (isWithinBorder(board, rowCol.row, rowCol.column) && !isVisited(visited, rowCol)) {
                    findWords(board, visited, rowCol.row, rowCol.column, candidateWord, foundWords, updatedPathString);
                }
            }
        }

        // set visited to false in order for next adjacent tile to be considered in search
        visited.put(new RowColumn(row, col), false);
    }

    private boolean isVisited(Map<RowColumn, Boolean> visitedMap, RowColumn rowColumn) {
        return visitedMap.getOrDefault(rowColumn, false);
    }

    private boolean isWithinBorder(char[][] board, Integer row, Integer col) {
        return row >= 0 && row < board.length && col >=0 && col < board[row].length;
    }

    private boolean isKnownWord(String word) {
        // only check for validity of word if it contains 3 or more characters, according to Boggle rules
        return word.length() >= 3 && wordTrie.isKnown(word.toLowerCase());
    }

    private boolean isPrefix(String word) {
        return wordTrie.isPrefix(word.toLowerCase());
    }

    @Data
    private static class FoundWord {
        private final String foundWord;
        private final String path;
    }

    @Data
    private static class RowColumn {
        private final Integer row;
        private final Integer column;
    }

}
