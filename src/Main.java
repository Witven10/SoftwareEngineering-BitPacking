public class Main {
    public static void main(String[] args) {
        // --- TEST maskK ---
        System.out.println("maskK(3) = " + BitUtils.maskK(3));   // attendu : 7 (0b111)
        System.out.println("maskK(10) = " + BitUtils.maskK(10)); // attendu : 1023 (0b1111111111)

        // --- TEST getBits ---
        int word = 0b10110110; // = 182 en décimal
        int bits = BitUtils.getBits(word, 1, 3); 
        // bits à partir du bit 1 (donc "011") => attendu : 3
        System.out.println("getBits(0b10110110, start=1, k=3) = " + bits);

        // --- TEST setBits ---
        int word2 = 0b00000000; // départ vide
        word2 = BitUtils.setBits(word2, 2, 3, 0b101);
        // on met 101 (5) à partir du bit 2 -> résultat attendu : 0b0010100 = 20
        System.out.println("setBits(0, start=2, k=3, value=5) = " + Integer.toBinaryString(word2));
    }
}
