package org.dslabs;


import java.util.List;

public class Main {
    public static void main(String[] args) {
        ISearchEngine searchEngine = new SearchEngine();
        searchEngine.indexDirectory("datasets/");
        List<ISearchResult> searchResults = searchEngine.searchByMultipleWordWithRanking("slab serif");
        for (ISearchResult searchResult : searchResults) {
            System.out.println(searchResult.getId() + " " + searchResult.getRank());
        }

        BTree<Integer, String> btree = new BTree<>(2);
        Integer[] keys = {1, 5, 16, 18, 4, 3, 19, 2, 10, 20, 17, 7, 8, 9, 6, 15, 13, 12, 14, 11};
        for (Integer key : keys) {
            btree.insert(key, key.toString());
        }
        btree.inorderBTree(btree.getRoot());
        for (Integer key : keys) {
            System.out.println(">>>>>>>>>>>>>> Deleting " + key);
            btree.delete(key);
            btree.inorderBTree(btree.getRoot()); //print them inorder after deletion
        }
    }
}