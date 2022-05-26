package org.dslabs;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V>, Comparable<K> {

    private final int minDegree;
    private final int maxDegree;
    private final int minNumOfKeys;
    private final int maxNumOfKeys;
    private IBTreeNode<K, V> root;

    public BTree(int minDeg) {
        this.minDegree = minDeg;
        this.maxDegree = minDeg * 2;
        this.minNumOfKeys = minDeg - 1;
        this.maxNumOfKeys = minDeg * 2 - 1;
        this.root = new BTreeNode<K, V>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(Collections.nCopies(60, null)), true, 0);
    }

    @Override
    public int getMinimumDegree() {
        return this.minDegree;
    }

    @Override
    public IBTreeNode<K, V> getRoot() {
        return this.root;
    }

    @Override
    public void insert(K key, V value) {
        IBTreeNode<K, V> x = this.root;
        List<IBTreeNode<K, V>> ancestors = new ArrayList<>();
        List<Integer> ancestorsIndexes = new ArrayList<>();
        while (!x.isLeaf()) {
            int childIndex = 0;
            for (K k : x.getKeys()) {
                if (key.compareTo(k) < 0) {
                    break;
                }
                childIndex++;
            }
            ancestors.add(0, x);
            ancestorsIndexes.add(0, childIndex);
            x = x.getChildren().get(childIndex);
        }
        ancestors.add(null);
        ancestorsIndexes.add(-1);
        int insertionIndex = 0;
        for (K k : x.getKeys()) {
            if (key.compareTo(k) < 0) {
                break;
            }
            insertionIndex++;
        }
        IBTreeNode<K, V> z = x;
        int i = insertionIndex;
        K keyToPushIn = key;
        V valueToPushIn = value;
        IBTreeNode<K, V> left = null;
        IBTreeNode<K, V> right = null;
        for (IBTreeNode<K, V> ancestor : ancestors) {
            if (z.getNumOfKeys() < this.maxNumOfKeys) {
                z.getKeys().add(i, keyToPushIn);
                z.getValues().add(i, valueToPushIn);
                if (left != null) {
                    z.getChildren().set(i, left);
                    z.getChildren().add(i + 1, right);
                }
                z.setNumOfKeys(z.getNumOfKeys() + 1);
                keyToPushIn = null;
                valueToPushIn = null;
                break;
            } else {
                int middleIndex = (this.maxNumOfKeys + 1) / 2;
                z.getKeys().add(i, keyToPushIn);
                z.getValues().add(i, valueToPushIn);
                if (left != null) {
                    z.getChildren().set(i, left);
                    z.getChildren().add(i + 1, right);
                }
                keyToPushIn = z.getKeys().remove(middleIndex);
                valueToPushIn = z.getValues().remove(middleIndex);
                List<K> leftKeys = new ArrayList<>(z.getKeys().subList(0, middleIndex));
                List<V> leftValues = new ArrayList<>(z.getValues().subList(0, middleIndex));
                List<IBTreeNode<K, V>> leftChildren =
                        z.isLeaf() ? new ArrayList<>(Collections.nCopies(60, null))
                                : new ArrayList<>(z.getChildren().subList(0, middleIndex + 1));
                List<K> rightKeys = new ArrayList<>(z.getKeys().subList(middleIndex, z.getKeys().size()));
                List<V> rightValues = new ArrayList<>(z.getValues().subList(middleIndex, z.getValues().size()));
                List<IBTreeNode<K, V>> rightChildren = z.isLeaf() ? new ArrayList<>(Collections.nCopies(60, null))
                        : new ArrayList<>(z.getChildren().subList(middleIndex + 1, z.getChildren().size()));
                left = new BTreeNode<>(leftKeys, leftValues, leftChildren, z.isLeaf(), leftKeys.size());
                right = new BTreeNode<>(rightKeys, rightValues, rightChildren, z.isLeaf(), rightKeys.size());
                z = ancestor;
                i = ancestorsIndexes.get(ancestors.indexOf(ancestor));
            }
        }
        if (keyToPushIn != null) {
            this.root = new BTreeNode<K, V>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, 1);
            this.root.getKeys().add(keyToPushIn);
            this.root.getValues().add(valueToPushIn);
            this.root.getChildren().add(left);
            this.root.getChildren().add(right);
        }
        System.out.println(this.root.getKeys());
    }

    @Override
    public V search(K key) {
        return _helpSearch(key, root);
    }

    private V _helpSearch(K key, IBTreeNode<K, V> root) {
        int pointer = 0;
        if (root == null) return null;
        for (int i = 0; i < root.getNumOfKeys(); i++) {
            if (key.compareTo(root.getKeys().get(i)) == 0) {
                return root.getValues().get(i);
            } else if (key.compareTo(root.getKeys().get(i)) < 0) {
                IBTreeNode<K, V> para = root.isLeaf() ? null : root.getChildren().get(pointer);
                return _helpSearch(key, para);
            } else pointer++;
        }
        IBTreeNode<K, V> para = root.isLeaf() ? null : root.getChildren().get(pointer);
        return _helpSearch(key, para);
    }

    @Override
    public boolean delete(K key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int compareTo(K arg0) {
        //provide implementation here
        //See effective java for appropriate implementation conditions
        return 0;
    }

}
