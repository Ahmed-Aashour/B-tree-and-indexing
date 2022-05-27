import org.dslabs.BTree;
import org.dslabs.BTreeNode;
import org.dslabs.IBTreeNode;

import java.util.List;

public class BTreeForTesting<K extends Comparable<K>, V> extends BTree<K, V> {
    public BTreeForTesting(int minDeg) {
        super(minDeg);
    }

    public void _delete(IBTreeNode<K, V> node, K key){
        //if key found :)
        if (node != null && node.getKeys().contains(key)){
            //Internal node deletion ----> Case II
            if(!node.isLeaf()){
                System.out.print("Case II: ");
                int index = node.getKeys().indexOf(key);
                IBTreeNode<K, V> leftChild  = node.getChildren().get(index);
                IBTreeNode<K, V> rightChild = node.getChildren().get(index+1);
                //Inorder predecessor - left child
                if(leftChild.getNumOfKeys() > this.minNumOfKeys && leftChild.isLeaf() || !leftChild.isLeaf()){
                    System.out.println("Predecessor of internal node");
                    IBTreeNode<K, V> predecessorNode = Predecessor(leftChild);
                    int last = predecessorNode.getNumOfKeys()-1; //Predecessor index
                    node.getKeys().set(index, predecessorNode.getKeys().get(last)); //set the predecessor
                    node.getValues().set(index, predecessorNode.getValues().get(last));
                    _delete(leftChild, predecessorNode.getKeys().get(last)); //Recursively _delete the Predecessor
                }
                //Inorder successor - right child
                else if(rightChild.getNumOfKeys() > this.minNumOfKeys && rightChild.isLeaf() || !rightChild.isLeaf()){
                    System.out.println("Successor of internal node");
                    IBTreeNode<K, V> successorNode = Successor(rightChild);
                    node.getKeys().set(index, successorNode.getKeys().get(0)); //set the successor
                    node.getValues().set(index, successorNode.getValues().get(0));
                    _delete(rightChild, successorNode.getKeys().get(0)); //Recursively _delete the Successor
                }
                //Merging left & right children of the internal node
                else {
                    System.out.println("Merging left & right children - both predecessor & successor have minNumOfKeys");
                    node.getValues().remove(index); //remove value & key
                    node.getKeys().remove(index);
                    node.setNumOfKeys(node.getNumOfKeys() - 1);
                    for(int i = 0; i < rightChild.getNumOfKeys(); i++) { //append to left child
                        leftChild.getKeys().add(rightChild.getKeys().get(i));
                        leftChild.getValues().add(rightChild.getValues().get(i));
                        if(!leftChild.isLeaf()){ //not leaf then append children
                            leftChild.getChildren().add(rightChild.getChildren().get(i));
                            if(i == rightChild.getNumOfKeys() - 1)
                                leftChild.getChildren().add(rightChild.getChildren().get(i+1));
                        }
                    }
                    leftChild.setNumOfKeys(leftChild.getNumOfKeys() + rightChild.getNumOfKeys());
                    node.getChildren().remove(rightChild); //remove right child
                    if(node == this.root && node.getNumOfKeys() == 0){
                        this.root = leftChild;
                    }
                }
            }
            //leaf node deletion ----> Case I
            else{
                System.out.println("Case I: in Leaf node");
                node.getValues().remove(node.getKeys().indexOf(key)); //remove value & key
                node.getKeys().remove(key);
                node.setNumOfKeys(node.getNumOfKeys() - 1);
            }
        }
        //if key not found :(
        else if(node != null){
            List<K> keys = node.getKeys();
            int nkeys = node.getNumOfKeys();
            for (int i = 0; i <= nkeys; i++){
                IBTreeNode<K, V> child = new BTreeNode<>(null, null, null, false, this.maxNumOfKeys);
                if(i < nkeys && key.compareTo(keys.get(i)) < 0 || i == nkeys){ //Search for the key recursively to be deleted
                    child = node.getChildren().get(i);
                    _delete(child, key);
                }
                //if deletion happens && it violates the B-tree property
                if(child != null && child.getNumOfKeys() < minNumOfKeys) {
                    //Leaf node deletion -----> Case I
                    if (child.isLeaf()) {
                        System.out.print("Case I: ");
                        /* borrow from left/right sibling */
                        //left borrow -> right rotation
                        if (i != 0 && node.getChildren().get(i - 1).getNumOfKeys() > this.minNumOfKeys) {
                            System.out.println("Left borrow");
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
                            System.out.println("Right borrow");
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
                            System.out.println("Left merging");
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
                            System.out.println("Right merging");
                            IBTreeNode<K, V> rightSibling = node.getChildren().get(i + 1);
                            rightSibling.getKeys().add(0, node.getKeys().remove(i));
                            rightSibling.getValues().add(0, node.getValues().remove(i));
                            node.setNumOfKeys(node.getNumOfKeys() - 1);
                            for (int j = child.getNumOfKeys()-1; j >= 0 ; j--) {
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
                        System.out.print("Case II: ");
                        /* borrow from left/right sibling */
                        //left borrow -> right rotation
                        if (i != 0 && node.getChildren().get(i - 1).getNumOfKeys() > this.minNumOfKeys) {
                            System.out.println("Left borrow");
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
                            System.out.println("Right borrow");
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
                            System.out.println("Left merging");
                            IBTreeNode<K, V> leftSibling = node.getChildren().get(i - 1);
                            leftSibling.getKeys().add(node.getKeys().remove(i - 1)); //append a key from parent to leftSibling
                            leftSibling.getValues().add(node.getValues().remove(i - 1));
                            node.setNumOfKeys(node.getNumOfKeys() - 1);
                            for (int j = 0; j < child.getNumOfKeys(); j++) { //append child's keys to leftSibling's keys
                                leftSibling.getKeys().add(child.getKeys().get(j));
                                leftSibling.getValues().add(child.getValues().get(j));
                            }
                            for (int k = 0; k < child.getChildren().size(); k++){ //append child's children to leftSibling's children
                                leftSibling.getChildren().add(child.getChildren().get(k));
                            }
                            leftSibling.setNumOfKeys(leftSibling.getNumOfKeys() + child.getNumOfKeys() + 1); //update numOfKeys of leftSibling
                            node.getChildren().remove(i); //child got removed
                            if (node == this.root && node.getNumOfKeys() == 0) //if root become empty
                                this.root = leftSibling;
                            //right merging
                        } else {
                            System.out.println("Right merging");
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
                    //if root has no children then it's a leaf node
                    if (node.getChildren().size() == 0) {
                        this.root.setLeaf(true);
                    }
                    nkeys--; //Decrement the size-counter after deletion
                }
            }
        }
    }
}
