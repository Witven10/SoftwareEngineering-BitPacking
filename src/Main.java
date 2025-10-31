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
        System.out.println(" ");
        System.out.println("========== TEST NO OVERLAP========");
        int[] input = {5, 7, 2, 9, 17, 3, 8, 20, 30, 44, 88, 101, 300, 700, 1500};

        int k1= BitUtils.getK(input); 
        BitPackingNoOverlap P = new BitPackingNoOverlap(k1);
        System.err.println("k = "+k1 );

        // Compression
        int[] tabcompress = P.compress(input);
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
        System.out.println("=== GET===");
        System.out.println("Valeur à l'indice 3 = " + P.get(3));
        System.out.println("Valeur à l'indice 0 = " + P.get(0));


 
    
                //--------------------------------TEST  BitPackingOverlap-----------------------------------------------
        System.out.println(" ");
        System.out.println("========== TEST OVERLAP========");
        int[] input2 = {5, 7, 2, 9, 17, 3, 8, 20, 30, 44, 88, 101, 300, 700, 1500};

        int k2= BitUtils.getK(input2); 
        BitPackingOverlap P2 = new BitPackingOverlap(k2);
        System.err.println("k = "+k2);

        // Compression
        int[] tabcompress2 = P2.compress(input2);
        System.out.println("=== COMPRESSION ===");
        for (int compressWord2 : tabcompress2) {
            System.out.println(Integer.toBinaryString(compressWord2));
        }

        // Décompression
        int[] output2 = new int[input2.length];
        output2 = P2.decompress(output2);

        System.out.println("=== DECOMPRESSION ===");
        for (int val : output2) {
            System.out.print(val + " ");
        }
        System.out.println();

        // Test get
        System.out.println("=== GET===");
        System.out.println("Valeur à l'indice 3 = " + P2.get(3));
        System.out.println("Valeur à l'indice 0 = " + P2.get(0));
        

               //--------------------------------TEST  BitPackingOverflow-----------------------------------------------
        System.out.println(" ");
        System.out.println("========== TEST OVERFLOW========");
        int[] input3= {1000, 1000, 2, 9, 1000, 3, 8, 20, 30, 44, 88, 101, 300, 700, 1500};

        int k3= BitUtils.getK(input3); 
        BitPackingOverflow P3 = new BitPackingOverflow(k3);
        System.err.println("k = "+k3);

        // Compression
        int[] tabcompress3 = P3.compress(input3);
        System.out.println("=== COMPRESSION ===");
        for (int compressWord3 : tabcompress3) {
            System.out.println(Integer.toBinaryString(compressWord3));
        }

        // Décompression
        int[] output3 = new int[input3.length];
        output3 = P3.decompress(output3);

        System.out.println("=== DECOMPRESSION ===");
        for (int val : output3) {
            System.out.print(val + " ");
        }
        System.out.println();

        // Test get
        System.out.println("=== GET===");
        System.out.println("Valeur à l'indice 3 = " + P3.get(3));
        System.out.println("Valeur à l'indice 0 = " + P3.get(0));        
    
    }
}
