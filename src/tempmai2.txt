public class Main {
    public static void main(String[] args) {

        // ---------------------------------------- TEST BitPacking -------------------------------------------------------
        //int[] input4= {32768, 32768, 36664, 40000, 1000, 2, 9, 1000, 3, 8, 20, 30, 44, 88, 101, 300, 700, 1500};
        int[] input4 = {
            32768, 32768, 36664, 40000, 1000, 2, 9, 1000, 3, 8, 20, 30, 44, 88, 101, 300, 700, 1500,
            1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
            31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100
        };
        
        //testCompressionMethod(compresseur, input4);
        for (String mode : new String[]{"nooverlap", "overlap", "overflow"}) {
            System.out.println("\n\n//////////////////////////////////// TEST POUR " + mode + " ////////////////////////////////////");
            System.out.println("\nTaille de l'entrée: " + input4.length + " entiers.");
            int k= BitUtils.getK(input4);
            BitPacking compressor = BitPackingFactory.create(mode, k);
            Test.testCompressionMethod(compressor, input4);
        }


    }

}