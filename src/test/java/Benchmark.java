import org.dslabs.BTree;
import org.dslabs.IBTree;

import java.util.Random;

public class Benchmark {
    public static void main(String[] args) {
        int samples = 256;
        int n = 8192;
        long[] putTimeSamples = new long[samples];
        long[] getTimeSamples = new long[samples];
        long[] removeTimeSamples = new long[samples];
        for (int i = 0; i < samples; i++) {
            IBTree<Integer, Integer> tree = new BTree<>(3);
            Random keyGenerator = new Random();
            Integer[] dataset = new Integer[n];
            for (int k = 0; k < n; k++) {
                dataset[k] = keyGenerator.nextInt(999999999);
            }
            long start = System.nanoTime();
            for (int k = 0; k < n; k++) {
                tree.insert(dataset[k], dataset[k]);
            }
            long end = System.nanoTime();
            putTimeSamples[i] = (end - start);
            for (int k = 0; k < n; k++) {
                start = System.nanoTime();
                tree.search(dataset[k]);
                end = System.nanoTime();
                getTimeSamples[i] += (end - start);
            }
            for (int k = 0; k < n; k++) {
                start = System.nanoTime();
                tree.delete(dataset[k]);
                end = System.nanoTime();
                removeTimeSamples[i] += (end - start);
            }
        }
        long meanPutTime = 0;
        long meanGetTime = 0;
        long meanRemoveTime = 0;
        for (int i = 0; i < samples; i++) {
            meanPutTime += putTimeSamples[i];
            meanGetTime += getTimeSamples[i];
            meanRemoveTime += removeTimeSamples[i];
        }
        meanPutTime /= samples;
        meanGetTime /= samples;
        meanRemoveTime /= samples;
        System.out.println("Mean Insert Time: " + meanPutTime / 1000000.0 + "ms");
        System.out.println("Mean Search Time: " + meanGetTime / 1000000.0 + "ms");
        System.out.println("Mean Remove Time: " + meanRemoveTime / 1000000.0 + "ms");
    }
}
