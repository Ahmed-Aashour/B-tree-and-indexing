import org.dslabs.BTree;

import java.util.Arrays;
import java.util.Random;

class Tests {

    /**
     * Tests the insert() & delete() methods in B-tree implementation
     * & shows the results in a good format using inorderBTree() & BFS_BTree() methods
     * @param testNum: test case number
     * @param t: the minimum degree of the B-tree
     * @param keys: the keys to be inserted & deleted
     */
    public static void test_case(int testNum, int t, Integer[] keys){
        System.out.println("\n\n<<< Test case " + testNum + " >>>");
        BTree<Integer, String> btree = new BTreeForTesting<>(t);
        System.out.println("Data set: " + Arrays.toString(keys));
        System.out.println("Data set length: " + keys.length);
        System.out.println("MinDegree: " + t);
        for (Integer key : keys) { /* Insertions */
            btree.insert(key, key.toString());
        }
        System.out.println(">>>>>>>>> After insertion process");
        btree.inorderBTree(btree.getRoot()); //show the inorder-traversal after insertion
        btree.BFS_BTree(btree.getRoot()); //show the tree representation

        for (Integer key : keys) { /* Deletions */
            System.out.println(">>>>>>>>> Deleting (" + key + ") ");
            btree.delete(key);
            btree.inorderBTree(btree.getRoot()); //show the inorder-traversal after each deletion
            btree.BFS_BTree(btree.getRoot());
        }
    }

    public static void main (String[] args) {

        Integer[] keys  = {1, 5, 16, 18, 4, 3, 19, 2, 10, 20, 17, 7, 8, 9, 6, 15, 13, 12, 14, 11};
        test_case(1, 2, keys); //Test case 1
        test_case(2, 4, keys); //Test case 2
        test_case(3, 6, keys); //Test case 3

        //Generating random keys
        Random rand = new Random();
        int numOfKeys = 25;
        Integer[] keys2 = new Integer[numOfKeys];
        for (int i = 0; i < keys2.length; i++){
            keys2[i] = rand.nextInt(101);
        }

        test_case(4, 6, keys2); //Test case 4
    }
}