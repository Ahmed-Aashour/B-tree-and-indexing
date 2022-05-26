package org.dslabs;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {

    private final int minDegree;
    private final int maxDegree;
    private final int minNumOfKeys;
    private final int maxNumOfKeys;

    private final int middleIndex;
    private IBTreeNode<K, V> root;

    public BTree(int minDeg) {
        this.minDegree = minDeg;
        this.maxDegree = minDeg * 2;
        this.minNumOfKeys = minDeg - 1;
        this.maxNumOfKeys = minDeg * 2 - 1;
        this.middleIndex = (this.maxNumOfKeys + 1) / 2;
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
            z.getChildren().set(i, left);
            z.getChildren().add(i + 1, right);
            if (z.getNumOfKeys() < this.maxNumOfKeys) {
                z.getKeys().add(i, keyToPushIn);
                z.getValues().add(i, valueToPushIn);
                z.setNumOfKeys(z.getNumOfKeys() + 1);
                keyToPushIn = null;
                break;
            } else {
                z.getKeys().add(i, keyToPushIn);
                z.getValues().add(i, valueToPushIn);
                keyToPushIn = z.getKeys().remove(middleIndex);
                valueToPushIn = z.getValues().remove(middleIndex);
                List<K> leftKeys = new ArrayList<>(z.getKeys().subList(0, middleIndex));
                List<V> leftValues = new ArrayList<>(z.getValues().subList(0, middleIndex));
                List<IBTreeNode<K, V>> leftChildren =
                        z.isLeaf() ? new ArrayList<>(Collections.nCopies(this.maxNumOfKeys + 1, null))
                                : new ArrayList<>(z.getChildren().subList(0, middleIndex + 1));
                List<K> rightKeys = new ArrayList<>(z.getKeys().subList(middleIndex, z.getKeys().size()));
                List<V> rightValues = new ArrayList<>(z.getValues().subList(middleIndex, z.getValues().size()));
                List<IBTreeNode<K, V>> rightChildren =
                        z.isLeaf() ? new ArrayList<>(Collections.nCopies(this.maxNumOfKeys + 1, null))
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
        if(search(key) == null)
            return false;
        else{
            delete(this.root, key);
            return true;
        }
    }

    //remember to update the leaf nodes attribute <--------------
    //remember to see Predecessor & Successor (Merging problem ~ deletion of leaf nooe) <----------------
    private void delete(IBTreeNode<K, V> node, K key){
        if (node != null && node.getKeys().contains(key)){
            //handling internal node deletion ----> Case II
            System.out.println("case II: line 148");
            if(!node.isLeaf()){
                int index = node.getKeys().indexOf(key);
                IBTreeNode<K, V> leftChild  = node.getChildren().get(index);
                IBTreeNode<K, V> rightChild = node.getChildren().get(index+1);
                //Inorder predecessor - left child
                if(leftChild.getNumOfKeys() > this.minNumOfKeys){
                    IBTreeNode<K, V> predecessorNode = Predecessor(leftChild);
                    int last = predecessorNode.getNumOfKeys()-1;
                    node.getKeys().set(index, predecessorNode.getKeys().remove(last));
                    node.getValues().set(index, predecessorNode.getValues().remove(last));
                    predecessorNode.setNumOfKeys(predecessorNode.getNumOfKeys()-1);
                }
                //Inorder successor - right child
                else if(rightChild.getNumOfKeys() > this.minNumOfKeys){
                    IBTreeNode<K, V> successorNode = Successor(rightChild);
                    node.getKeys().set(index, successorNode.getKeys().remove(0));
                    node.getValues().set(index, successorNode.getValues().remove(0));
                    successorNode.setNumOfKeys(successorNode.getNumOfKeys()-1);
                }
                //Merging left & right children of the internal node
                else {
                    node.getValues().remove(index); //remove value & key
                    node.getKeys().remove(index);
                    node.setNumOfKeys(node.getNumOfKeys() - 1);
                    for(int i = 0; i < rightChild.getNumOfKeys(); i++) { //append to left child
                        leftChild.getKeys().add(rightChild.getKeys().get(i));
                        leftChild.getValues().add(rightChild.getValues().get(i));
                    }
                    leftChild.setNumOfKeys(leftChild.getNumOfKeys() + rightChild.getNumOfKeys());
                    node.getChildren().remove(rightChild); //remove right child
                }
            }
            else{ //leaf node ----> Case I
                System.out.println("case I: line 182");
                node.getValues().remove(node.getKeys().indexOf(key)); //remove value & key
                node.getKeys().remove(key);
                node.setNumOfKeys(node.getNumOfKeys() - 1); //numOfKeys--;
            }
        }
        else if(node != null){
            List<K> keys = node.getKeys();
            int nkeys = node.getNumOfKeys();
            for (int i = 0; i <= nkeys; i++){
                IBTreeNode<K, V> child = new BTreeNode<>(null, null, null, false, this.maxNumOfKeys);
                if(i < nkeys && key.compareTo(keys.get(i)) < 0 || i == nkeys){
                    child = node.getChildren().get(i);
                    delete(child, key);
                }
                //if deletion happens && it violates the B-tree property
                if(child != null && child.getNumOfKeys() < minNumOfKeys){
                    //In a leaf node -----> Case I
                    if (child.isLeaf()) {
                        System.out.println("case I: line 201");
                        /* borrow from left/right sibling */
                        //left borrow -> right rotation
                        if (i != 0 && node.getChildren().get(i-1).getNumOfKeys() > this.minNumOfKeys) {
                            System.out.println("\tleft borrow");
                            IBTreeNode<K, V> leftSibling = node.getChildren().get(i-1);
                            child.getKeys().add(0, node.getKeys().get(i-1)); //parent to child
                            child.getValues().add(0, node.getValues().get(i-1));
                            child.setNumOfKeys(child.getNumOfKeys()+1); //
                            node.getKeys().set(i-1, leftSibling.getKeys().remove(leftSibling.getNumOfKeys()-1)); //leftSibling to parent
                            node.getValues().set(i-1, leftSibling.getValues().remove(leftSibling.getNumOfKeys()-1));
                            leftSibling.setNumOfKeys(leftSibling.getNumOfKeys()-1);
                        }
                        //right borrow -> left rotation
                        else if(i != nkeys && node.getChildren().get(i+1).getNumOfKeys() > this.minNumOfKeys){
                            System.out.println("\tright borrow");
                            IBTreeNode<K, V> rightSibling = node.getChildren().get(i+1);
                            child.getKeys().add(node.getKeys().get(i)); //parent to child
                            child.getValues().add(node.getValues().get(i));
                            child.setNumOfKeys(child.getNumOfKeys()+1);
                            node.getKeys().set(i, rightSibling.getKeys().remove(0));   //rightSibling to parent
                            node.getValues().set(i, rightSibling.getValues().remove(0));
                            rightSibling.setNumOfKeys(rightSibling.getNumOfKeys()-1);
                        }
                        /* Merging */
                        else if( i != 0){ //left merging
                            System.out.println("\tleft merging");
                            IBTreeNode<K, V> leftSibling = node.getChildren().get(i-1);
                            leftSibling.getKeys().add(node.getKeys().remove(i-1));
                            leftSibling.getValues().add(node.getValues().remove(i-1));
                            node.setNumOfKeys(node.getNumOfKeys()-1);
                            for(int j = 0; j < child.getNumOfKeys(); j++){
                                leftSibling.getKeys().add(child.getKeys().get(j));
                                leftSibling.getValues().add(child.getValues().get(j));
                            }
                            leftSibling.setNumOfKeys(leftSibling.getNumOfKeys() + child.getNumOfKeys() + 1);
                            node.getChildren().remove(i);
                        }
                        else{ //right merging
                            System.out.println("\tright merging");
                            IBTreeNode<K, V> rightSibling = node.getChildren().get(i+1);
                            rightSibling.getKeys().add(0, node.getKeys().remove(i));
                            rightSibling.getValues().add(0, node.getValues().remove(i));
                            node.setNumOfKeys(node.getNumOfKeys()-1);
                            for(int j = 0; j < child.getNumOfKeys(); j++){
                                rightSibling.getKeys().add(0, child.getKeys().get(j));
                                rightSibling.getValues().add(0, child.getValues().get(j));
                            }
                            rightSibling.setNumOfKeys(rightSibling.getNumOfKeys() + child.getNumOfKeys() + 1);
                            node.getChildren().remove(i);
                        }
                        //In an internal node -----> Case III
                    } else {
                        System.out.println("case III: line 254");
                        /* borrow from left/right sibling */
                        //left borrow -> right rotation
                        if(i != 0 && node.getChildren().get(i-1).getNumOfKeys() > this.minNumOfKeys){
                            System.out.println("\tleft borrow");
                            IBTreeNode<K, V> leftSibling = node.getChildren().get(i-1);
                            K k = node.getKeys().get(i-1);
                            V v = node.getValues().get(i-1);
                            child.getKeys().add(0, k); //parent to child
                            child.getValues().add(0, v);
                            child.setNumOfKeys(child.getNumOfKeys()+1);
                            node.getKeys().set(i-1, leftSibling.getKeys().remove(leftSibling.getNumOfKeys()-1)); //leftSibling to parent
                            node.getValues().set(i-1, leftSibling.getValues().remove(leftSibling.getNumOfKeys()-1));
                            leftSibling.setNumOfKeys(leftSibling.getNumOfKeys()-1);
                            child.getChildren().add(0, leftSibling.getChildren().remove(leftSibling.getNumOfKeys()+1)); //move leftSibling's child to current child
                        }
                        //right borrow -> left rotation
                        else if (i != nkeys && node.getChildren().get(i+1).getNumOfKeys() > this.minNumOfKeys) {
                            System.out.println("\tright borrow");
                            IBTreeNode<K, V> rightSibling = node.getChildren().get(i+1);
                            K k = node.getKeys().get(i);
                            V v = node.getValues().get(i);
                            child.getKeys().add(k); //parent to child
                            child.getValues().add(v);
                            child.setNumOfKeys(child.getNumOfKeys()+1);
                            node.getKeys().set(i, rightSibling.getKeys().remove(0)); //rightSibling to parent
                            node.getValues().set(i, rightSibling.getValues().remove(0));
                            rightSibling.setNumOfKeys(rightSibling.getNumOfKeys()-1);
                            child.getChildren().add(rightSibling.getChildren().remove(0)); //move rightSibling's child to current child
                        }
                        /* Merging (This will shrink the tree height if the parent has minNumOfKeys) */
                        else if(i != 0){ //left merging
                            System.out.println("\tleft merging");
                            IBTreeNode<K, V> leftSibling = node.getChildren().get(i-1);
                            leftSibling.getKeys().add(node.getKeys().remove(i-1)); //append a key from parent to leftSibling
                            leftSibling.getValues().add(node.getValues().remove(i-1));
                            node.setNumOfKeys(node.getNumOfKeys()-1);
                            for(int j = 0; j < child.getNumOfKeys(); j++){ //append child's keys & children to leftSibling
                                leftSibling.getKeys().add(child.getKeys().get(j));
                                leftSibling.getValues().add(child.getValues().get(j));
                                leftSibling.getChildren().add(child.getChildren().get(j));
                                if(j == child.getNumOfKeys()-1)
                                    leftSibling.getChildren().add(child.getChildren().get(j+1));
                            }
                            leftSibling.setNumOfKeys(leftSibling.getNumOfKeys() + child.getNumOfKeys() + 1); //update numOfKeys of leftSibling
                            node.getChildren().remove(i); //child got removed
                            if(node == this.root && node.getNumOfKeys() == 0)
                                this.root = leftSibling;
                        }
                        else{ //right merging
                            System.out.println("\tright merging");
                            IBTreeNode<K, V> rightSibling = node.getChildren().get(i+1);
                            rightSibling.getKeys().add(0, node.getKeys().remove(i));
                            rightSibling.getValues().add(0, node.getValues().remove(i));
                            node.setNumOfKeys(node.getNumOfKeys()-1);
                            rightSibling.getChildren().add(0, child.getChildren().get(child.getNumOfKeys()));
                            for(int j = child.getNumOfKeys() - 1; j >= 0; j--){
                                rightSibling.getKeys().add(0, child.getKeys().get(j));
                                rightSibling.getValues().add(0, child.getValues().get(j));
                                rightSibling.getChildren().add(0, child.getChildren().get(j));
                            }
                            rightSibling.setNumOfKeys(rightSibling.getNumOfKeys() + child.getNumOfKeys() + 1);
                            node.getChildren().remove(i);
                            if(node == this.root && node.getNumOfKeys() == 0)
                                this.root = rightSibling;
                        }
                        //if root has no children then it's a leaf node
                        if(node.getChildren().size() == 0){
                            this.root.setLeaf(true);
                        }
                    }
                }
            }
        }
    }


    private IBTreeNode<K, V> Successor(IBTreeNode<K,V> node) {
        if(node.isLeaf()){
            return node;
        }
        else{ //go left-most
            return Successor(node.getChildren().get(0));
        }
    }

    private IBTreeNode<K, V> Predecessor(IBTreeNode<K,V> node) {
        if(node.isLeaf()){
            return node;
        }
        else{ //go right-most
            return Predecessor(node.getChildren().get(node.getNumOfKeys()));
        }
    }

    public void InorderBTree(IBTreeNode<K, V> node){
        if (node != null)
        {
            for (int i = 0; i < node.getNumOfKeys(); i++)
            {
                InorderBTree(node.getChildren().get(i));
                System.out.print(node.getKeys().get(i) + " ");
                if (i == node.getNumOfKeys()-1) {
                    InorderBTree(node.getChildren().get(i + 1));
                }
            }
        }
    }

}
