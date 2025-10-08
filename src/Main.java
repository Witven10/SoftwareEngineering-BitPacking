public class Main {
    public static void main(String[] args) {
        // ----------------------------------------- TEST maskK ------------------------------------------------------
        System.out.println("maskK(3) = " + Integer.toBinaryString(BitUtils.maskK(3)));   // attendu :(0b111)
        System.out.println("maskK(10) = " + Integer.toBinaryString(BitUtils.maskK(10))); // attendu : (0b1111111111)

        // ----------------------------------------- TEST getBits ----------------------------------------
        int word = 0b10110110; 
        int bits = BitUtils.getBits(word, 1, 3); 
        System.out.println("getBits(0b10110110, start=1, k=3) = " + Integer.toBinaryString(bits));

        // ---------------------------------------- TEST setBits -------------------------------------------------------
        int word2 = 0b00000000;
        word2 = BitUtils.setBits(word2, 2, 3, 0b101);
        System.out.println("setBits(0, start=2, k=3, value=5) = " + Integer.toBinaryString(word2));

        

        //--------------------------------TEST  BitPackingNoOverlap-----------------------------------------------

        int[] input = {5, 7, 2, 9, 4, 3, 8, 20, 30, 44, 88, 100};

        int k1= BitUtils.getK(input); 
        BitPackingNoOverlap P = new BitPackingNoOverlap(k1);
        System.err.println("k = "+k1 );

        // Compression
        int[] tabcompress = P.compress(input);
        System.out.println(" ");
        System.out.println("=== COMPRESSION ===");
        for (int compressWord : tabcompress) {
            System.out.println(Integer.toBinaryString(compressWord));
        }

        // Décompression
        int[] output = new int[input.length];
        output = P.decompress(output);

        System.out.println("=== DECOMPRESSION ===");
        for (int val : output) {
            System.out.print(val + " ");
        }
        System.out.println();

        // Test get
        System.out.println("=== ACCÈS DIRECT ===");
        System.out.println("Valeur à l'indice 3 = " + P.get(3));
        System.out.println("Valeur à l'indice 0 = " + P.get(0));
    
    }
}
