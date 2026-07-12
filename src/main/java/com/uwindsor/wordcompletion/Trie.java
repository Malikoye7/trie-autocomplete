package com.uwindsor.wordcompletion;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * Trie – a prefix tree that stores the vocabulary extracted
 * from all group CSV files and supports fast word completion.
 *
 * How a Trie works:
 *   Every word is stored letter by letter, starting from the
 *   root. Shared prefixes share the same path. For example,
 *   "rate" and "rate" share the path r→a→t, then branch at
 *   "e" vs end-of-word for "rat".
 *
 * Operations provided:
 *   insert(word)             – add a word to the Trie
 *   search(word)             – check if an exact word exists
 *   startsWith(prefix)       – check if any word starts with prefix
 *   getWordsWithPrefix(prefix) – return all words starting with prefix
 *   getWordCount()           – return total unique words stored
 *
 * Time complexity:
 *   insert / search / startsWith – O(L) where L = word length
 *   getWordsWithPrefix           – O(L + W) where W = number of results
 *
 * @author Oyewole Malik
 * Student ID: 110215789
 * Course: COMP 8547 – Advanced Computing Concepts
 * Instructor: Dr. Olena Syrotkina
 * University of Windsor – Summer 2026
 * ============================================================
 */
public class Trie {

    // The root node has no letter of its own. It is the starting
    // point for every word inserted into the Trie.
    private final TrieNode root;

    /**
     * Creates an empty Trie with a blank root node.
     */
    public Trie() {
        root = new TrieNode();
    }

    /**
     * Inserts a word into the Trie character by character.
     *
     * For each letter, the method maps it to an index 0–25
     * (a=0, b=1, … z=25). If no child node exists at that index,
     * a new TrieNode is created. After the last letter, the node
     * is flagged as isEndOfWord = true so we know a full word ends here.
     *
     * Non-alphabetic characters are skipped so that numeric tokens
     * or punctuation from the CSV files do not corrupt the Trie.
     *
     * @param word the lowercase word to insert
     */
    public void insert(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            // Skip any character outside the a–z range
            if (index < 0 || index >= 26) continue;
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
        }
        current.isEndOfWord = true;
    }

    /**
     * Returns true if the exact word exists in the Trie.
     *
     * Traverses the Trie following each letter of the word.
     * If any letter is missing (no child node at that index),
     * returns false immediately. At the end, the node must be
     * marked isEndOfWord = true for the word to be considered
     * part of the vocabulary.
     *
     * @param word the word to look up
     * @return true if the word was inserted into the Trie
     */
    public boolean search(String word) {
        TrieNode node = getNode(word);
        return node != null && node.isEndOfWord;
    }

    /**
     * Returns true if at least one word in the Trie starts with
     * the given prefix.
     *
     * Unlike search(), this does NOT require the prefix itself
     * to be a complete word — it only checks that the path through
     * those characters exists.
     *
     * @param prefix the prefix to check
     * @return true if any vocabulary word begins with prefix
     */
    public boolean startsWith(String prefix) {
        return getNode(prefix) != null;
    }

    /**
     * Returns a list of all words in the Trie that start with
     * the given prefix.
     *
     * Step 1: Traverse from the root to the node at the end of
     *         the prefix. If this path does not exist, no words
     *         match, so we return an empty list immediately.
     *
     * Step 2: From that node, perform a depth-first search (DFS)
     *         through all children. Every time we reach a node
     *         where isEndOfWord = true, we record the word built
     *         so far (prefix + letters appended during DFS).
     *
     * The StringBuilder is passed by reference and a character is
     * appended before each recursive call, then removed after so
     * that backtracking works correctly.
     *
     * @param prefix the search prefix typed by the user
     * @return list of all matching words, alphabetically ordered
     *         (alphabetical order comes for free because children
     *          are stored in a–z order at each node)
     */
    public List<String> getWordsWithPrefix(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = getNode(prefix);
        if (node == null) {
            // No word in the vocabulary begins with this prefix
            return results;
        }
        // DFS from the prefix endpoint to collect all completions
        collectWords(node, new StringBuilder(prefix), results);
        return results;
    }

    /**
     * Returns the total number of unique words stored in the Trie.
     * Used to confirm how many vocabulary entries were loaded.
     *
     * @return total word count
     */
    public int getWordCount() {
        return countWords(root);
    }

    // ---------------------------------------------------------------
    // Private helper methods
    // ---------------------------------------------------------------

    /**
     * Traverses the Trie following each character of the given string
     * and returns the node at the end of that path, or null if the
     * path does not exist.
     *
     * Used by both search() and getWordsWithPrefix() to avoid
     * duplicating the traversal logic.
     *
     * @param str the string whose path to follow
     * @return the TrieNode at the end of str, or null if not found
     */
    private TrieNode getNode(String str) {
        TrieNode current = root;
        for (char c : str.toCharArray()) {
            int index = c - 'a';
            if (index < 0 || index >= 26) return null;
            if (current.children[index] == null) return null;
            current = current.children[index];
        }
        return current;
    }

    /**
     * Recursive DFS that collects every complete word reachable
     * from the given node.
     *
     * At each node:
     *   – if isEndOfWord is true, the current StringBuilder content
     *     is a complete word — add it to the results list.
     *   – for each non-null child (index i), append the letter
     *     ('a' + i), recurse, then remove that letter (backtrack).
     *
     * @param node    the current node in the DFS traversal
     * @param current the word built so far (prefix + letters added)
     * @param results the list accumulating all found words
     */
    private void collectWords(TrieNode node, StringBuilder current,
                              List<String> results) {
        if (node.isEndOfWord) {
            results.add(current.toString());
        }
        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                current.append((char) ('a' + i));
                collectWords(node.children[i], current, results);
                current.deleteCharAt(current.length() - 1);
            }
        }
    }

    /**
     * Recursively counts all nodes in the Trie where isEndOfWord
     * is true. Called by getWordCount() starting from the root.
     *
     * @param node the current node
     * @return count of complete words at or below this node
     */
    private int countWords(TrieNode node) {
        int count = node.isEndOfWord ? 1 : 0;
        for (TrieNode child : node.children) {
            if (child != null) {
                count += countWords(child);
            }
        }
        return count;
    }
}
