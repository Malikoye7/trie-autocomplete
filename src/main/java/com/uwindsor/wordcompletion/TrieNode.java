package com.uwindsor.wordcompletion;

/**
 * ============================================================
 * TrieNode – one node in the Trie data structure.
 *
 * A Trie (also called a prefix tree) stores words character
 * by character. Each node represents one letter and holds:
 *
 *   children  – an array of 26 slots, one per lowercase letter
 *               (index 0 = 'a', index 25 = 'z'). A null slot
 *               means no word continues through that letter.
 *
 *   isEndOfWord – true when this node marks the last letter of
 *                 a complete word that was inserted into the Trie.
 *
 * Example – inserting "rate" and "rat":
 *   root -> r -> a -> t -> e  (isEndOfWord = true for 'e')
 *                        ^--- (isEndOfWord = true for 't' as well)
 *
 * @author Oyewole Malik
 * ============================================================
 */
public class TrieNode {

    // 26 child pointers, one for each letter a–z.
    // children[0] points to the node for 'a', children[25] for 'z'.
    // A null entry means no word in the vocabulary uses that letter
    // at this position.
    TrieNode[] children;

    // Marks whether this node is the end of a valid vocabulary word.
    // Without this flag we could not tell "rat" from the prefix "rat"
    // inside "rate".
    boolean isEndOfWord;

    /**
     * Initialises a new node with no children and not yet marked
     * as the end of any word.
     */
    public TrieNode() {
        children = new TrieNode[26];
        isEndOfWord = false;
    }
}
