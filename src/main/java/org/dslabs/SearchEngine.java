package org.dslabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class SearchEngine implements ISearchEngine {

    IBTree<String, HashMap<Integer, Integer>> index = new BTree<>(2);

    @Override
    public void indexWebPage(String filePath) {
        try {
            File inputFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList docs = doc.getElementsByTagName("doc");
            for (int temp = 1; temp < docs.getLength(); temp++) {
                int docId = Integer.parseInt(docs.item(temp).getAttributes().getNamedItem("id").getNodeValue());
                String[] words = docs.item(temp).getTextContent().split("\\s+");
                for (String word : words) {
                    HashMap<Integer, Integer> docFreq = this.index.search(word);
                    if (docFreq != null) {
                        if (docFreq.containsKey(docId)) {
                            docFreq.put(docId, docFreq.get(docId) + 1);
                        } else {
                            docFreq.put(docId, 1);
                        }
                    } else {
                        docFreq = new HashMap<>();
                        docFreq.put(docId, 1);
                        this.index.insert(word, docFreq);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void indexDirectory(String directoryPath) {
        File dir = new File(directoryPath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.isFile()) {
                    this.indexWebPage(child.getAbsolutePath());
                } else if (child.isDirectory()) {
                    this.indexDirectory(child.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public void deleteWebPage(String filePath) {
        try {
            File inputFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList docs = doc.getElementsByTagName("doc");
            for (int temp = 1; temp < docs.getLength(); temp++) {
                int docId = Integer.parseInt(docs.item(temp).getAttributes().getNamedItem("id").getNodeValue());
                String[] words = docs.item(temp).getTextContent().split("\\s+");
                for (String word : words) {
                    HashMap<Integer, Integer> docFreq = this.index.search(word);
                    if (docFreq != null) {
                        docFreq.remove(docId);
                        if (docFreq.isEmpty()) {
                            this.index.delete(word);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ISearchResult> searchByWordWithRanking(String word) {
        List<ISearchResult> results = new ArrayList<>();
        HashMap<Integer, Integer> docFreq = this.index.search(word);
        if (docFreq != null) {
            for (Integer docId : docFreq.keySet()) {
                SearchResult result = new SearchResult();
                result.setId(docId.toString());
                result.setRank(docFreq.get(docId));
                results.add(result);
            }
        }
        return results;
    }

    @Override
    public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
        List<ISearchResult> results = new ArrayList<>();
        String[] words = sentence.split("\\s+");
        HashMap<Integer, Integer> docFreq = new HashMap<>();
        for (String word : words) {
            HashMap<Integer, Integer> temp = this.index.search(word);
            if (temp != null) {
                for (Integer docId : temp.keySet()) {
                    if (docFreq.containsKey(docId)) {
                        docFreq.put(docId, docFreq.get(docId) + temp.get(docId));
                    } else {
                        docFreq.put(docId, temp.get(docId));
                    }
                }
            }
        }
        for (Integer docId : docFreq.keySet()) {
            SearchResult result = new SearchResult();
            result.setId(docId.toString());
            result.setRank(docFreq.get(docId));
            results.add(result);
        }
        return results;
    }

}
