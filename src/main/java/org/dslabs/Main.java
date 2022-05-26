package org.dslabs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BTree<Integer, String> btree = new BTree<>(2);
        Integer[] keys = {1, 5, 4, 3, 2 ,10, 7, 8, 9, 6};
        for (int i = 0; i < keys.length; i++){
            btree.insert(keys[i], keys[i].toString());
        }
        btree.delete(10);
        btree.InorderBTree(btree.getRoot());
    }

}