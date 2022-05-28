package org.dslabs;


import java.util.*;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {

    protected final int minNumOfKeys;
    protected final int maxNumOfKeys;
    private final int minDegree;
    private final int middleIndex;
    protected IBTreeNode<K, V> root;

    public BTree(int minDeg) {
        this.minDegree = minDeg;
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
                int compResult = key.compareTo(k);
                if (compResult < 0) {
                    break;
                } else if (compResult == 0) {
                    return;
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
        if (search(key) == null)
            return false;
        else {
            _delete(this.root, key);
            return true;
        }
    }

    protected void _delete(IBTreeNode<K, V> node, K key) {
        //if key found :)
        if (node != null && node.getKeys().contains(key)) {
            //Internal node deletion ----> Case II
            if (!node.isLeaf()) {
                int index = node.getKeys().indexOf(key);
                IBTreeNode<K, V> leftChild = node.getChildren().get(index);
                IBTreeNode<K, V> rightChild = node.getChildren().get(index + 1);
                //Inorder predecessor - left child
                if (leftChild.getNumOfKeys() > this.minNumOfKeys && leftChild.isLeaf() || !leftChild.isLeaf()) {
                    IBTreeNode<K, V> predecessorNode = Predecessor(leftChild);
                    int last = predecessorNode.getNumOfKeys() - 1; //Predecessor index
                    node.getKeys().set(index, predecessorNode.getKeys().get(last)); //set the predecessor
                    node.getValues().set(index, predecessorNode.getValues().get(last));
                    _delete(leftChild, predecessorNode.getKeys().get(last)); //Recursively _delete the Predecessor
                }
                //Inorder successor - right child
                else if (rightChild.getNumOfKeys() > this.minNumOfKeys && rightChild.isLeaf() || !rightChild.isLeaf()) {
                    IBTreeNode<K, V> successorNode = Successor(rightChild);
                    node.getKeys().set(index, successorNode.getKeys().get(0)); //set the successor
                    node.getValues().set(index, successorNode.getValues().get(0));
                    _delete(rightChild, successorNode.getKeys().get(0)); //Recursively _delete the Successor
                }
                //Merging left & right children of the internal node
                else {
                    node.getValues().remove(index); //remove value & key
                    node.getKeys().remove(index);
                    node.setNumOfKeys(node.getNumOfKeys() - 1);
                    for (int i = 0; i < rightChild.getNumOfKeys(); i++) { //append to left child
                        leftChild.getKeys().add(rightChild.getKeys().get(i));
                        leftChild.getValues().add(rightChild.getValues().get(i));
                        if (!leftChild.isLeaf()) { //not leaf then append children
                            leftChild.getChildren().add(rightChild.getChildren().get(i));
                            if (i == rightChild.getNumOfKeys() - 1)
                                leftChild.getChildren().add(rightChild.getChildren().get(i + 1));
                        }
                    }
                    leftChild.setNumOfKeys(leftChild.getNumOfKeys() + rightChild.getNumOfKeys());
                    node.getChildren().remove(rightChild); //remove right child
                    if (node == this.root && node.getNumOfKeys() == 0) {
                        this.root = leftChild;
                    }
                }
            }
            //leaf node deletion ----> Case I
            else {
                node.getValues().remove(node.getKeys().indexOf(key)); //remove value & key
                node.getKeys().remove(key);
                node.setNumOfKeys(node.getNumOfKeys() - 1);
            }
        }
        //if key not found :(
        else if (node != null) {
            List<K> keys = node.getKeys();
            int nkeys = node.getNumOfKeys();
            for (int i = 0; i <= nkeys; i++) {
                IBTreeNode<K, V> child = new BTreeNode<>(null, null, null, false, this.maxNumOfKeys);
                if (i < nkeys && key.compareTo(keys.get(i)) < 0 || i == nkeys) { //Search for the key recursively to be deleted
                    child = node.getChildren().get(i);
                    _delete(child, key);
                }
                //if deletion happens && it violates the B-tree property
                if (child != null && child.getNumOfKeys() < minNumOfKeys) {
                    //Leaf node deletion -----> Case I
                    if (child.isLeaf()) {
                        /* borrow from left/right sibling */
                        //left borrow -> right rotation
                        if (i != 0 && node.getChildren().get(i - 1).getNumOfKeys() > this.minNumOfKeys) {
                            IBTreeNode<K, V> leftSibling = node.getChildren().get(i - 1);
                            child.getKeys().add(0, node.getKeys().get(i - 1)); //parent to child
                            child.getValues().add(0, node.getValues().get(i - 1));
                            child.setNumOfKeys(child.getNumOfKeys() + 1);
                            node.getKeys().set(i - 1, leftSibling.getKeys().remove(leftSibling.getNumOfKeys() - 1)); //leftSibling to parent
                            node.getValues().set(i - 1, leftSibling.getValues().remove(leftSibling.getNumOfKeys() - 1));
                            leftSibling.setNumOfKeys(leftSibling.getNumOfKeys() - 1);
                        }
                        //right borrow -> left rotation
                        else if (i != nkeys && node.getChildren().get(i + 1).getNumOfKeys() > this.minNumOfKeys) {
                            IBTreeNode<K, V> rightSibling = node.getChildren().get(i + 1);
                            child.getKeys().add(node.getKeys().get(i)); //parent to child
                            child.getValues().add(node.getValues().get(i));
                            child.setNumOfKeys(child.getNumOfKeys() + 1);
                            node.getKeys().set(i, rightSibling.getKeys().remove(0));   //rightSibling to parent
                            node.getValues().set(i, rightSibling.getValues().remove(0));
                            rightSibling.setNumOfKeys(rightSibling.getNumOfKeys() - 1);
                        }
                        /* Merging (This will shrink the tree height if the root becomes empty) */
                        //left merging
                        else if (i != 0) {
                            IBTreeNode<K, V> leftSibling = node.getChildren().get(i - 1);
                            leftSibling.getKeys().add(node.getKeys().remove(i - 1));
                            leftSibling.getValues().add(node.getValues().remove(i - 1));
                            node.setNumOfKeys(node.getNumOfKeys() - 1);
                            for (int j = 0; j < child.getNumOfKeys(); j++) {
                                leftSibling.getKeys().add(child.getKeys().get(j));
                                leftSibling.getValues().add(child.getValues().get(j));
                            }
                            leftSibling.setNumOfKeys(leftSibling.getNumOfKeys() + child.getNumOfKeys() + 1);
                            node.getChildren().remove(i);
                            if (node == this.root && node.getNumOfKeys() == 0) //if root become empty
                                this.root = leftSibling;
                            //right merging
                        } else {
                            IBTreeNode<K, V> rightSibling = node.getChildren().get(i + 1);
                            rightSibling.getKeys().add(0, node.getKeys().remove(i));
                            rightSibling.getValues().add(0, node.getValues().remove(i));
                            node.setNumOfKeys(node.getNumOfKeys() - 1);
                            for (int j = child.getNumOfKeys() - 1; j >= 0; j--) {
                                rightSibling.getKeys().add(0, child.getKeys().get(j));
                                rightSibling.getValues().add(0, child.getValues().get(j));
                            }
                            rightSibling.setNumOfKeys(rightSibling.getNumOfKeys() + child.getNumOfKeys() + 1);
                            node.getChildren().remove(i);
                            if (node == this.root && node.getNumOfKeys() == 0) //if root become empty
                                this.root = rightSibling;
                        }
                        //Internal node -----> Case III
                    } else {
                        /* borrow from left/right sibling */
                        //left borrow -> right rotation
                        if (i != 0 && node.getChildren().get(i - 1).getNumOfKeys() > this.minNumOfKeys) {
                            IBTreeNode<K, V> leftSibling = node.getChildren().get(i - 1);
                            child.getKeys().add(0, node.getKeys().get(i - 1)); //parent to child
                            child.getValues().add(0, node.getValues().get(i - 1));
                            child.setNumOfKeys(child.getNumOfKeys() + 1);
                            node.getKeys().set(i - 1, leftSibling.getKeys().remove(leftSibling.getNumOfKeys() - 1)); //leftSibling to parent
                            node.getValues().set(i - 1, leftSibling.getValues().remove(leftSibling.getNumOfKeys() - 1));
                            leftSibling.setNumOfKeys(leftSibling.getNumOfKeys() - 1);
                            child.getChildren().add(0, leftSibling.getChildren().remove(leftSibling.getNumOfKeys() + 1)); //move leftSibling's child to current child
                        }
                        //right borrow -> left rotation
                        else if (i != nkeys && node.getChildren().get(i + 1).getNumOfKeys() > this.minNumOfKeys) {
                            IBTreeNode<K, V> rightSibling = node.getChildren().get(i + 1);
                            child.getKeys().add(node.getKeys().get(i)); //parent to child
                            child.getValues().add(node.getValues().get(i));
                            child.setNumOfKeys(child.getNumOfKeys() + 1);
                            node.getKeys().set(i, rightSibling.getKeys().remove(0)); //rightSibling to parent
                            node.getValues().set(i, rightSibling.getValues().remove(0));
                            rightSibling.setNumOfKeys(rightSibling.getNumOfKeys() - 1);
                            child.getChildren().add(rightSibling.getChildren().remove(0)); //move rightSibling's child to current child
                        }
                        /* Merging (This will shrink the tree height if the root becomes empty) */
                        //left merging
                        else if (i != 0) {
                            IBTreeNode<K, V> leftSibling = node.getChildren().get(i - 1);
                            leftSibling.getKeys().add(node.getKeys().remove(i - 1)); //append a key from parent to leftSibling
                            leftSibling.getValues().add(node.getValues().remove(i - 1));
                            node.setNumOfKeys(node.getNumOfKeys() - 1);
                            for (int j = 0; j < child.getNumOfKeys(); j++) { //append child's keys to leftSibling's keys
                                leftSibling.getKeys().add(child.getKeys().get(j));
                                leftSibling.getValues().add(child.getValues().get(j));
                            }
                            for (int k = 0; k < child.getChildren().size(); k++) { //append child's children to leftSibling's children
                                leftSibling.getChildren().add(child.getChildren().get(k));
                            }
                            leftSibling.setNumOfKeys(leftSibling.getNumOfKeys() + child.getNumOfKeys() + 1); //update numOfKeys of leftSibling
                            node.getChildren().remove(i); //child got removed
                            if (node == this.root && node.getNumOfKeys() == 0) //if root become empty
                                this.root = leftSibling;
                            //right merging
                        } else {
                            IBTreeNode<K, V> rightSibling = node.getChildren().get(i + 1);
                            rightSibling.getKeys().add(0, node.getKeys().remove(i)); //append a key from parent to rightSibling
                            rightSibling.getValues().add(0, node.getValues().remove(i));
                            node.setNumOfKeys(node.getNumOfKeys() - 1);
                            rightSibling.getChildren().add(0, child.getChildren().get(child.getNumOfKeys()));
                            for (int j = child.getNumOfKeys() - 1; j >= 0; j--) { //append child's keys & children to rightSibling's
                                rightSibling.getKeys().add(0, child.getKeys().get(j));
                                rightSibling.getValues().add(0, child.getValues().get(j));
                                rightSibling.getChildren().add(0, child.getChildren().get(j));
                            }
                            rightSibling.setNumOfKeys(rightSibling.getNumOfKeys() + child.getNumOfKeys() + 1); //update numOfKeys of rightSibling
                            node.getChildren().remove(i); //child got removed
                            if (node == this.root && node.getNumOfKeys() == 0) //if root become empty
                                this.root = rightSibling;
                        }
                    }
                    nkeys--; //Decrement the size-counter after deletion
                }
            }
        }
    }

    /**
     * <Helps _delete() method>
     * Finds the predecessor of a given node
     *
     * @param node: the right-child of the deleted key
     * @return the node that has the predecessor key-value
     */
    protected IBTreeNode<K, V> Successor(IBTreeNode<K, V> node) {
        if (node.isLeaf()) {
            return node;
        } else { //go left-most
            return Successor(node.getChildren().get(0));
        }
    }

    /**
     * <Helps _delete() method>
     * Finds the successor node of a given node
     *
     * @param node: the left-child of the deleted key
     * @return the node that has the successor key-value
     */
    protected IBTreeNode<K, V> Predecessor(IBTreeNode<K, V> node) {
        if (node.isLeaf()) {
            return node;
        } else { //go right-most
            return Predecessor(node.getChildren().get(node.getNumOfKeys()));
        }
    }

    /**
     * <Just For Testing>
     * prints the keys of the b-tree in an increasing order
     * (i.e., inorder traversal) recursively
     *
     * @param node: the root of the tree
     */
    public void inorderBTree(IBTreeNode<K, V> node) {
        if (node == this.root) System.out.print("Inorder: ");
        if (node != null) {
            for (int i = 0; i < node.getNumOfKeys(); i++) {
                inorderBTree(node.getChildren().get(i));
                System.out.print(node.getKeys().get(i) + " ");
                if (i == node.getNumOfKeys() - 1) {
                    inorderBTree(node.getChildren().get(i + 1));
                }
            }
        }
        if (node == this.root) System.out.println();
    }

    /**
     * <Just For Testing>
     * Prints the B-tree as BFS (Level Order Traversal) using a Queue
     *
     * @param node: the root of the tree
     */
    public void BFS_BTree(IBTreeNode<K, V> node) {
        Queue<IBTreeNode<K, V>> q = new LinkedList<>();
        q.add(node);
        q.add(null);
        int level = 0;
        System.out.print("Level-" + level + " -> ");
        while (!q.isEmpty()) {
            IBTreeNode<K, V> curr = q.poll();
            if (curr == null) {
                if (!q.isEmpty()) {
                    q.add(null);
                    System.out.println(); //print new line between levels
                    System.out.print("Level-" + (++level) + " -> ");
                }
            } else {
                if (curr.getChildren().get(0) != null)
                    q.addAll(curr.getChildren());
                System.out.print(curr.getKeys() + " "); //print node's keys
            }
        }
        System.out.println();
    }
}
