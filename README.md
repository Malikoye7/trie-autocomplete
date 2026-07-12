# Autocomplete Engine — Trie Data Structure

A word completion engine built entirely from scratch in Java using a custom **Trie (prefix tree)** data structure.

Feed it a prefix, get back every matching word — instantly.

## How it works

The program reads CSV data from multiple real-world sources, extracts and filters vocabulary, inserts every unique word into a Trie, then performs depth-first search from any prefix node to return all completions in alphabetical order.

- **Insert:** O(L) — walks one node per character, creates nodes as needed
- **Search:** O(L) — exact match in linear time
- **Autocomplete:** O(L + W) — L to reach prefix node, W to collect results

## Features

- Parses word tokens from any CSV data sources placed in `data/`
- Builds a de-duplicated vocabulary and serves prefix queries from it
- Returns prefix completions in alphabetical order automatically
- Supports exact word lookup (like a dictionary)
- Handles full-text CSV cells with two-level parsing

## Example

```
Prefix: "cur"  →  currencies, currency, currencyconverter
Prefix: "tra"  →  track, transaction, transfer, transfers
Prefix: "rat"  →  rate, rates
```

## Tech Stack

- Java 11
- Maven
- No external libraries — pure Java implementation

## Project Structure

```
src/
  main/java/com/uwindsor/wordcompletion/
    TrieNode.java       — single node (26 children + isEndOfWord flag)
    Trie.java           — insert, search, startsWith, getWordsWithPrefix
    WordCompletion.java — CSV parser + demo runner
data/
  *.csv                 — real-world currency and book data sources
```

## Run it

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.uwindsor.wordcompletion.WordCompletion"
```
