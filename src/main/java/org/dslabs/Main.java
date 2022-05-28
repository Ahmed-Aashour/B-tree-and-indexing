package org.dslabs;


import java.util.*;

public class Main {
    public static void main(String[] args) {
        ISearchEngine searchEngine = new SearchEngine();
        searchEngine.indexDirectory("datasets/");
        List<ISearchResult> searchResults;
        System.out.println("Searching for \"Ziad\"");
        searchResults = searchEngine.searchByWordWithRanking("Ziad");
        for (ISearchResult searchResult : searchResults) {
            System.out.println("Doc Id: " + searchResult.getId());
            System.out.println("Rank: " + searchResult.getRank());
        }
        System.out.println("Searching for \"Ahmed\"");
        searchResults = searchEngine.searchByWordWithRanking("Ahmed");
        for (ISearchResult searchResult : searchResults) {
            System.out.println("Doc Id: " + searchResult.getId());
            System.out.println("Rank: " + searchResult.getRank());
        }
        System.out.println("=========================================>");
        System.out.println("Searching for \"Slab\"");
        searchResults = searchEngine.searchByWordWithRanking("Slab");
        for (ISearchResult searchResult : searchResults) {
            System.out.println("Doc Id: " + searchResult.getId());
            System.out.println("Rank: " + searchResult.getRank());
        }
        System.out.println("=========================================>");
        System.out.println("Searching for \"slab\"");
        searchResults = searchEngine.searchByWordWithRanking("slab");
        for (ISearchResult searchResult : searchResults) {
            System.out.println("Doc Id: " + searchResult.getId());
            System.out.println("Rank: " + searchResult.getRank());
        }
        System.out.println("=========================================>");
        System.out.println("Searching for \"slab serif\"");
        searchResults = searchEngine.searchByMultipleWordWithRanking("slab serif");
        for (ISearchResult searchResult : searchResults) {
            System.out.println("Doc Id: " + searchResult.getId());
            System.out.println("Rank: " + searchResult.getRank());
        }
        System.out.println("=========================================>");
        System.out.println("Searching for \"two forms of source routing the first\"");
        for (ISearchResult searchResult : searchResults) {
            System.out.println("Doc Id: " + searchResult.getId());
            System.out.println("Rank: " + searchResult.getRank());
        }

    }
}