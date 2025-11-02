public class Main {
    public static void main(String[] args) {

        // --- 1. PRÉPARATION DES DONNÉES DE BENCHMARK ---
        // Utilisation d'un grand tableau pour une mesure plus fiable
        int[] input_bench = {
            32768, 32768, 36664, 40000, 1000, 2, 9, 1000, 3, 8, 20, 30, 44, 88, 101, 300, 700, 1500,
            1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
            31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100
        };
        
        int REPETITIONS = 50; // Nombre de répétitions pour le benchmark
        int k_max = BitUtils.getK(input_bench); // K max requis (16 bits pour 40000)

        System.out.println("==================================================================================");
        System.out.println("--- DÉMARRAGE DES TESTS & BENCHMARKS ---");
        System.out.println("Taille de l'entrée: " + input_bench.length + " entiers.");
        System.out.println("K max requis: " + k_max + " bits.");
        System.out.println("Répétitions du benchmark: " + REPETITIONS);
        System.out.println("==================================================================================");


        // --- 2. BOUCLE DE TEST ET BENCHMARK POUR CHAQUE MODE ---
        for (String mode : new String[]{"nooverlap", "overlap", "overflow"}) {
            
            System.out.println("\n\n//////////////////////////////////// TEST POUR " + mode.toUpperCase() + " ////////////////////////////////////");
            
            // Création de l'instance via la Factory
            BitPacking compressor = BitPackingFactory.create(mode, k_max);
            
            // 2.1. TEST DE VALIDATION (Exactitude)
            System.out.println("\n--- VÉRIFICATION DE LA VALIDATION (Output) ---");
            Test.testCompressionMethod(compressor, input_bench);


            // 2.2. BENCHMARK (Performance)
            System.out.println("\n--- BENCHMARK DE PERFORMANCE ---");
            
            // T_C : Chronométrage de la compression
            long t_c = BenchmarkRunner.runBenchmark(compressor, input_bench, "compress", REPETITIONS);
            
            // T_D : Chronométrage de la décompression
            long t_d = BenchmarkRunner.runBenchmark(compressor, input_bench, "decompress", REPETITIONS);
            
            // T_G : Chronométrage de l'accès direct
            long t_g = BenchmarkRunner.runBenchmark(compressor, input_bench, "get", REPETITIONS);

            // T_SIZE : Benchmark de l'empreinte mémoire (Memory Footprint)
            // Note: Le calcul doit être effectué une seule fois après la compression du warm-up.
            int[] tabCompress = compressor.compress(input_bench); // Re-compression pour obtenir la taille
            long size_brute_bits = (long) input_bench.length * 32;
            long size_comp_bits = (long) tabCompress.length * 32;
            double ratio = (double) size_comp_bits / size_brute_bits;


            // 2.3. AFFICHAGE DES RÉSULTATS
            System.out.println("\n[TEMPS MOYENS (ns)]");
            System.out.printf("T_C (Compression) : %d ns\n", t_c);
            System.out.printf("T_D (Décompression) : %d ns\n", t_d);
            System.out.printf("T_G (Accès Direct/élément) : %d ns\n", t_g);
            
            System.out.println("\n[TAILLE ET EFFICACITÉ]");
            System.out.printf("Taille Brute: %d bits\n", size_brute_bits);
            System.out.printf("Taille Compressée: %d bits\n", size_comp_bits);
            System.out.printf("Ratio de Compression (Comp/Brute): %.3f\n", ratio);
            System.out.printf("Gain d'Espace: %.1f%%\n", (1.0 - ratio) * 100);

            // 2.4. CALCUL DE RENTABILITÉ (pour le rapport)
            long delta_T_comp = t_c + t_d;
            System.out.printf("\n[ANALYSE DE RENTABILITÉ]");
            System.out.printf("Coût de Calcul (T_C + T_D) : %d ns\n", delta_T_comp);
            System.out.println("C'est le gain de temps de transmission minimum nécessaire pour que la compression soit rentable.");

        }
    }
}