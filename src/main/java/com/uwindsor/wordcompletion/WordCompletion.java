package com.uwindsor.wordcompletion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * ============================================================
 * WordCompletion — Trie-based Autocomplete Engine
 * ============================================================
 *
 * This program implements a word completion feature using a Trie
 * data structure. It reads every CSV file in the "data" folder
 * (for example, Bank of Canada exchange-rate data), builds a
 * vocabulary Trie from every valid word found, and then
 * demonstrates word completion by showing all vocabulary words
 * that start with a given prefix.
 *
 * The three steps this program performs:
 *
 * STEP 1 – Build Vocabulary Trie:
 *   Reads every .csv file from the "data" folder.
 *   Splits each line by comma, then splits each cell by any
 *   non-alphabetic character to extract individual words.
 *   Filters out stop words, short tokens, and duplicates.
 *   Inserts each valid unique word into the Trie.
 *
 * STEP 2 – Demonstrate Word Completion:
 *   For each test prefix, calls Trie.getWordsWithPrefix() which
 *   traverses to the prefix endpoint and performs a DFS to collect
 *   all words reachable from there. Results are listed in
 *   alphabetical order (natural order of the Trie's children array).
 *
 * STEP 3 – Exact Word Search Demo:
 *   Uses Trie.search() to confirm whether specific words exist
 *   in the vocabulary, demonstrating the O(L) lookup capability.
 *
 * Why a Trie for word completion?
 *   A Trie stores shared prefixes only once. Searching for all
 *   words beginning with a prefix takes O(L) to reach the prefix
 *   node, then O(W) to collect results — far faster than scanning
 *   every word in a plain array or hash table.
 *
 * Data source:
 *   Any .csv files placed in the "data" folder — for example,
 *   output from a Bank of Canada Selenium scraper. Sample rate
 *   files are included to demonstrate the program.
 *
 * @author Oyewole Malik
 * ============================================================
 */
public class WordCompletion {

    // Folder containing all group CSV files
    private static final String DATA_FOLDER = "data";

    // Minimum word length – single and two-letter tokens are not
    // useful as vocabulary entries and would clutter completions.
    private static final int MIN_WORD_LENGTH = 3;

    // Common English stop words excluded from the vocabulary because
    // they appear thousands of times in the Wise.com page text but
    // carry no meaningful domain information about currencies.
    private static final HashSet<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "the", "and", "for", "are", "but", "not", "you", "all", "can",
        "was", "one", "our", "out", "get", "has", "how", "its", "may",
        "more", "new", "now", "see", "who", "did", "had", "let", "put",
        "say", "she", "too", "use", "per", "any", "via", "when", "that",
        "this", "with", "from", "they", "will", "your", "each", "been",
        "have", "into", "also", "then", "than", "them", "were", "what",
        "over", "such", "even", "most", "after", "about", "other", "which"
    ));

    public static void main(String[] args) {

        System.out.println("=====================================================");
        System.out.println("  Task 2: Word Completion Using Tries");
        System.out.println("  Trie-based Autocomplete Engine");
        System.out.println("=====================================================\n");

        // ---------------------------------------------------------------
        // STEP 1: Build the vocabulary Trie from all CSV files
        // ---------------------------------------------------------------
        System.out.println("--- Step 1: Loading CSV files and building Trie ---\n");

        Trie trie = new Trie();

        File folder = new File(DATA_FOLDER);
        File[] csvFiles = folder.listFiles(
            (dir, name) -> name.toLowerCase().endsWith(".csv")
        );

        if (csvFiles == null || csvFiles.length == 0) {
            System.out.println("ERROR: No CSV files found in '" + DATA_FOLDER + "' folder.");
            System.out.println("Please place one or more .csv files in the 'data' folder.");
            return;
        }

        System.out.println("CSV files found: " + csvFiles.length);
        int totalTokens = 0;
        int totalInserted = 0;

        // Process each CSV file and insert words into the Trie
        for (File file : csvFiles) {
            int[] counts = processFile(file, trie);
            System.out.println("  [" + file.getName() + "] -> "
                + counts[0] + " tokens read, "
                + counts[1] + " new words inserted into Trie");
            totalTokens  += counts[0];
            totalInserted += counts[1];
        }

        System.out.println("\nTotal tokens processed  : " + totalTokens);
        System.out.println("Total unique words in Trie: " + trie.getWordCount());

        // ---------------------------------------------------------------
        // STEP 2: Word Completion Demonstration
        // ---------------------------------------------------------------
        // These prefixes are drawn from the currency domain so they
        // produce meaningful completions from the vocabulary.
        System.out.println("\n--- Step 2: Word Completion Results ---\n");

        String[] prefixes = {"cur", "exc", "rat", "dol", "can", "tra", "pay", "mon"};

        for (String prefix : prefixes) {
            List<String> completions = trie.getWordsWithPrefix(prefix);
            System.out.println("Prefix: \"" + prefix + "\"");
            if (completions.isEmpty()) {
                System.out.println("  -> No words found with this prefix.");
            } else {
                System.out.println("  -> " + completions.size() + " word(s) found:");
                // Print up to 10 results per prefix to keep output readable
                int limit = Math.min(10, completions.size());
                for (int i = 0; i < limit; i++) {
                    System.out.println("     " + (i + 1) + ". " + completions.get(i));
                }
                if (completions.size() > 10) {
                    System.out.println("     ... and " + (completions.size() - 10) + " more.");
                }
            }
            System.out.println();
        }

        // ---------------------------------------------------------------
        // STEP 3: Exact Word Search Demo
        // ---------------------------------------------------------------
        // Demonstrates the Trie's O(L) exact-match lookup. These are
        // currency-related words expected to appear in the vocabulary.
        System.out.println("--- Step 3: Exact Word Search Demo ---\n");

        String[] lookupWords = {"dollar", "exchange", "currency", "rate",
                                "transfer", "payment", "convert", "canadian"};

        for (String word : lookupWords) {
            boolean found = trie.search(word);
            System.out.println("  search(\"" + word + "\") -> "
                + (found ? "FOUND in vocabulary" : "NOT found in vocabulary"));
        }

        System.out.println("\n=====================================================");
        System.out.println("  Word completion complete.");
        System.out.println("  Unique vocabulary words  : " + trie.getWordCount());
        System.out.println("  Data structure used      : Trie (prefix tree)");
        System.out.println("  Completion time complexity: O(L + W)");
        System.out.println("  L = prefix length, W = number of matching words");
        System.out.println("=====================================================");
    }

    /**
     * Reads one CSV file, extracts valid words, and inserts new
     * (not yet seen) words into the Trie.
     *
     * Two-level splitting is used:
     *   Level 1 – split each line by comma (CSV column separator).
     *   Level 2 – split each cell by any non-alphabetic character
     *             using the regex [^a-zA-Z]+. This is necessary
     *             because Wise.com files contain full paragraphs
     *             inside a single CSV cell — without this second
     *             split the entire paragraph would be treated as
     *             one token.
     *
     * Each token is trimmed and lowercased before validation.
     * The method returns a two-element array: [tokensRead, newWordsInserted].
     *
     * @param file the CSV file to read
     * @param trie the Trie to insert new words into
     * @return int[]{tokensRead, newWordsInserted}
     */
    private static int[] processFile(File file, Trie trie) {
        int tokensRead = 0;
        int newWords   = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {

                // Level 1: split by CSV column separator
                String[] tokens = line.split(",");

                for (String token : tokens) {
                    // Level 2: split by any non-alphabetic character
                    // to handle full-text cells from Wise.com pages
                    String[] subTokens = token.split("[^a-zA-Z]+");

                    for (String sub : subTokens) {
                        String word = sub.trim().toLowerCase();
                        if (!isValidWord(word)) continue;

                        tokensRead++;
                        // Only insert if this word is not already in the Trie
                        if (!trie.search(word)) {
                            trie.insert(word);
                            newWords++;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("  ERROR reading " + file.getName()
                + ": " + e.getMessage());
        }
        return new int[]{tokensRead, newWords};
    }

    /**
     * Returns true if a token qualifies as a vocabulary word.
     *
     * Rejected tokens:
     *   – empty strings or whitespace-only strings
     *   – words shorter than MIN_WORD_LENGTH (less than 3 letters)
     *   – common English stop words (see STOP_WORDS set above)
     *
     * Filtering short tokens removes single-letter abbreviations
     * and two-letter currency codes like "gb" or "us" that appear
     * frequently but are not useful completion suggestions.
     *
     * @param word the cleaned, lowercased token to evaluate
     * @return true if the word should be added to the Trie
     */
    private static boolean isValidWord(String word) {
        if (word.isEmpty()) return false;
        if (word.length() < MIN_WORD_LENGTH) return false;
        if (STOP_WORDS.contains(word)) return false;
        return true;
    }
}
