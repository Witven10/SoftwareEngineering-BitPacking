public class Main {
    public static void main(String[] args) {

        int MAX_VALUE = 40000; 
        int ARRAY_SIZE = 10000; 

        // Génération d'un tableau de 10000 éléments avec un fort biais vers les petites valeurs
        int[] input_bench = Test.generateTestArray(ARRAY_SIZE, 0, MAX_VALUE, 0.95); 
        // Ici, 95% du temps, les nombres seront bas.
        Test.writeArrayToFile(input_bench, "bench_input_data.txt");

        // Base sample values (from selection) then expanded deterministically to reach 1000 elements
        /*int[] input_benc = {
            32768, 32768, 0, 0, 0, 0, 0, 600000, 36664, 8632, 40000, 1000, 2, 9, 1000, 3, 8, 20, 30, 44, 88, 101, 300, 700, 1500,
            1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
            31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100,
            110, 115, 120, 125, 130, 140, 150, 160, 170, 180, 190, 200, 250, 300, 350, 400, 450, 500, 600, 700,
            800, 900, 1000, 1200, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 6000, 7000, 8000, 9000, 10000, 12000, 15000, 20000,
            25000, 30000, 32767, 32768, 32769, 33000, 34000, 35000, 36000, 36664, 37000, 38000, 39000, 40000, 39999,
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47,
            53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 131, 151, 181, 191, 211, 251, 271, 307, 401,
            503, 601, 701, 809, 1001, 2001, 3001, 4001, 5001, 6001, 7001, 8001, 9001, 10001, 12345, 22222, 33333, 40000, 65535, 0
        };
*/
        int REPETITIONS = 50; // Nombre de répétitions pour le benchmark
        int k_max = BitUtils.getK(input_bench); 

        System.out.println("==================================================================================");
        System.out.println("//////////////////DÉMARRAGE DES TESTS & BENCHMARKS //////////////////");
        System.out.println("Taille de l'entrée: " + input_bench.length + " entiers.");
        System.out.println("K max requis: " + k_max + " bits.");
        System.out.println("Répétitions du benchmark: " + REPETITIONS);
        System.out.println("==================================================================================");


        //BOUCLE DE TEST ET BENCHMARK POUR CHAQUE MODE DE COMPRESSION
        for (String mode : new String[]{"nooverlap", "overlap", "overflow"}) {
            
            System.out.println("\n\n//////////////////////////////////// TEST POUR " + mode.toUpperCase() + " ////////////////////////////////////");
            
            BitPacking compressor = BitPackingFactory.create(mode, k_max);
            
            //TEST DE VALIDATION
            System.out.println("\n////////////VÉRIFICATION DE LA VALIDATION  ////////////");
            Test.testCompressionMethod(compressor, input_bench);


            //BENCHMARK (Performance)
            System.out.println("\n//////////////BENCHMARK DE PERFORMANCE //////////////");
            
            // T_C : Chronométrage de la compression
            long t_c = BenchmarkRunner.runBenchmark(compressor, input_bench, "compress", REPETITIONS);
            
            // T_D : Chronométrage de la décompression
            long t_d = BenchmarkRunner.runBenchmark(compressor, input_bench, "decompress", REPETITIONS);
            
            // T_G : Chronométrage de l'accès direct
            long t_g = BenchmarkRunner.runBenchmark(compressor, input_bench, "get", REPETITIONS);

            //CALCUL DE LA TAILLE DE LA COMPRESSION
            int[] tabCompress = compressor.compress(input_bench); // Re-compression pour obtenir la taille
            long size_brute_bits = (long) input_bench.length * 32;
            long size_comp_bits = (long) tabCompress.length * 32;
            double ratio = (double) size_comp_bits / size_brute_bits;


            //AFFICHAGE DES RÉSULTATS
            System.out.println("\nTEMPS MOYENS (ns)");
            System.out.printf("T_C : Temps pour la Compression : %d ns\n", t_c);
            System.out.printf("T_D : Temps pour la Décompression : %d ns\n", t_d);
            System.out.printf("T_G : Temps pour l'Accès Direct/élément : %d ns\n", t_g);
            
            System.out.println("\nTAILLE ET EFFICACITÉ DE LA COMPRESSION");
            System.out.printf("Taille Brute: %d bits\n", size_brute_bits);
            System.out.printf("Taille Compressée: %d bits\n", size_comp_bits);
            System.out.printf("Ratio de Compression (Comppression/Brute): %.3f\n", ratio);
            System.out.printf("Gain d'Espace: %.1f%%\n", (1.0 - ratio) * 100);

            //CALCUL DE RENTABILITÉ DE LA COMPRESSION
            long delta_T_comp = t_c + t_d;
            System.out.printf("\nANALYSE DE RENTABILITÉ DE LA COMPRESSION\n");
            System.out.printf("Coût de Calcul (T_C + T_D) : %d ns\n", delta_T_comp);
            System.out.println("C'est le gain de temps de transmission minimum nécessaire pour que la compression soit rentable.");

        }
    }
}