package org.scottsoft.boggle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
        NORTH_WEST(rowColumn -> new RowColumn(rowColumn.row - 1, rowColumn.column - 1)),
        NORTH(rowColumn -> new RowColumn(rowColumn.row - 1, rowColumn.column)),
        NORTH_EAST(rowColumn -> new RowColumn(rowColumn.row - 1, rowColumn.column + 1)),
        EAST(rowColumn -> new RowColumn(rowColumn.row, rowColumn.column + 1)),
        SOUTH_EAST(rowColumn -> new RowColumn(rowColumn.row + 1, rowColumn.column + 1)),
        SOUTH(rowColumn -> new RowColumn(rowColumn.row + 1, rowColumn.column)),
        SOUTH_WEST(rowColumn -> new RowColumn(rowColumn.row + 1, rowColumn.column - 1)),
        WEST(rowColumn -> new RowColumn(rowColumn.row, rowColumn.column - 1));

        private final Function<RowColumn, RowColumn> directionCoordFunction;

        Direction(Function<RowColumn, RowColumn> directionCoordFunction) {
            this.directionCoordFunction = directionCoordFunction;
        }
    }

    // TODO: consider returning path of found word?
    List<String> findWords(char[][] board) {
        List<FoundWord> foundWords = new ArrayList<>();
        for (int row = 0;row < board.length;row++) {
            for (int col = 0;col < board[row].length;col++) {
                findWords(board, row, col, foundWords);
            }
        }

        return foundWords.stream()
            .peek(word -> log.info("Found word={}, path={}", word.getFoundWord(), word.getPath()))
            .map(FoundWord::getFoundWord)
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
            foundWords.add(new FoundWord(candidateWord, updatedPathString));
        }

        if (isPrefix(candidateWord)) {
            // word is a prefix to a known word, keep searching
            updatedPathString = pathString + "(" + row + ", " + col + ") --> ";
            for (Direction dir : Direction.values()) {
                // visit each adjacent tile that has not already been visited
                RowColumn rowCol = dir.directionCoordFunction.apply(new RowColumn(row, col));
                if (isWithinBoardBorder(board, rowCol.row, rowCol.column) && !isVisited(visited, rowCol)) {
                    findWords(board, visited, rowCol.row, rowCol.column, candidateWord, foundWords, updatedPathString);
                }
            }
        }

        visited.put(new RowColumn(row, col), false);
    }

    private boolean isVisited(Map<RowColumn, Boolean> visitedMap, RowColumn rowColumn) {
        return visitedMap.getOrDefault(rowColumn, false);
    }

    private boolean isWithinBoardBorder(char[][] board, Integer row, Integer col) {
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
