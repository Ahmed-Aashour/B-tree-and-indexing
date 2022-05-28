package org.dslabs;


import java.util.*;

public class Main {
    public static void main(String[] args) {
        ISearchEngine searchEngine = new SearchEngine();
        searchEngine.indexDirectory("datasets/");
        List<ISearchResult> searchResults = searchEngine.searchByMultipleWordWithRanking("slab serif");
        for (ISearchResult searchResult : searchResults) {
            System.out.println(searchResult.getId() + " " + searchResult.getRank());
        }
    }
}