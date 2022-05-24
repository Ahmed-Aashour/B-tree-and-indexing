package org.dslabs;

import java.util.List;

public class BTreeNode<K extends Comparable<K>, V> implements IBTreeNode<K, V> {

    private List<K> keys;
    private List<V> values;
    private List<IBTreeNode<K, V>> children;
    private boolean isLeaf;
    private int numOfKeys;

    public BTreeNode(List<K> keys, List<V> values, List<IBTreeNode<K, V>> children, boolean isLeaf, int numOfKeys) {
        this.keys = keys;
        this.values = values;
        this.children = children;
        this.isLeaf = isLeaf;
        this.numOfKeys = numOfKeys;
    }

    @Override
    public int getNumOfKeys() {
        return this.numOfKeys;
    }

    @Override
    public void setNumOfKeys(int numOfKeys) {
        this.numOfKeys = numOfKeys;
    }

    @Override
    public boolean isLeaf() {
        return this.isLeaf;
    }

    @Override
    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    @Override
    public List<K> getKeys() {
        return this.keys;
    }

    @Override
    public void setKeys(List<K> keys) {
        this.keys = keys;
    }

    @Override
    public List<V> getValues() {
        return this.values;
    }

    @Override
    public void setValues(List<V> values) {
        this.values = values;
    }

    @Override
    public List<IBTreeNode<K, V>> getChildren() {
        return this.children;
    }

    @Override
    public void setChildren(List<IBTreeNode<K, V>> children) {
        this.children = children;
    }

}
