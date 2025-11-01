//Interface commun à toutes les classes de BitPacking
public interface BitPacking {
    int[] compress(int[] input);
    int[] decompress(int[] output);
    int get(int i);
}
