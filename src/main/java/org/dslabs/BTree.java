package org.dslabs;


public class BTree<K extends Comparable<K>, V> implements IBTree<K, V>, Comparable<K>{

    private BTreeNode<K, V> root;
    private final int minDegree;
    private final int maxDegree;
    private final int minNumOfKeys;
    private final int maxNumOfKeys;

    public BTree(int minDeg){
        this.minDegree    = minDeg;
        this.maxDegree    = minDeg * 2;
        this.minNumOfKeys = minDeg - 1;
        this.maxNumOfKeys = minDeg * 2 - 1;
        this.root = null;
    }
    @Override
    public int getMinimumDegree() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public IBTreeNode<K, V> getRoot() {
        return this.root;
    }
    

    @Override
    public void insert(K key, V value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public V search(K key) {
        return _helpSearch(key,root);
    }
    private V _helpSearch(K key, BTreeNode<K,V> root){
        int pointer = 0;
        if (root == null) return null;
        for (int i = 0; i< root.getKeys().size();i++){
            if (key.compareTo((K) root.getKeys().get(i)) == 0){
                return root.getValues().get(i);
            }
            else if (key.compareTo((K) root.getKeys().get(i)) < 0){
                BTreeNode para =  root.isLeaf() ? null : (BTreeNode) root.getChildren().get(pointer);
                return (V) _helpSearch(key, para );
            }
            else pointer++;
        }
        BTreeNode para = root.isLeaf() ? null : (BTreeNode) root.getChildren().get(pointer);
        return (V) _helpSearch(key, para);
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
