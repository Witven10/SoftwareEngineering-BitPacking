public class BenchmarkRunner {
    /**
     * Exécute un protocole de chronométrage précis pour une fonction donnée.
     * Inclut un warm-up pour la JVM (10 itérations) et des répétitions pour la fiabilité.
     * @param compressor L'instance BitPacking à tester (Overlap, NoOverlap, Overflow).
     * @param input Le tableau d'entrée original.
     * @param methodName Le nom de la fonction à tester ("compress", "decompress", ou "get").
     * @param repetitions Le nombre de fois où la mesure sera répétée.
     * @return Le temps moyen d'exécution en nanosecondes.
     */
    public static long runBenchmark(BitPacking compressor, int[] input, String methodName, int repetitions) {
        
        //Warm-up pour que la JVM puisse optimiser le code via le JIT
        // Exécution du code 10 fois pour permettre au JIT de compiler et d'optimiser.
        for (int i = 0; i < 10; i++) {
            switch (methodName) {
                case "compress":
                    compressor.compress(input);
                    break;
                case "decompress":
                    
                    compressor.compress(input); // compression avant la décompression
                    compressor.decompress(new int[input.length]);
                    break;
                case "get":
                    compressor.compress(input); 
                    compressor.get(0);
                    break;
            }
        }
        
        //Mesure Répétée pour obtenir un temps moyen fiable
        long totalTime = 0;
        
        // Pré-calcul de la compression en dehors de la boucle de mesure pour decompress et get
        if (!methodName.equals("compress")) {
            compressor.compress(input); 
        }
        
        for (int i = 0; i < repetitions; i++) {
            long startTime = System.nanoTime();
            
            switch (methodName) {
                case "compress":
                    compressor.compress(input);
                    break;
                
                case "decompress":
                    compressor.decompress(new int[input.length]); 
                    break;
                
                case "get":
                    for (int j = 0; j < input.length; j++) {
                        compressor.get(j); 
                    }
                    break;
            }
            
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        
        //Retour du Temps Moyen
        if (methodName.equals("get")) {
             // Temps moyen pour l'accès à UN SEUL élément
             return (totalTime / repetitions) / input.length; 
        } else {
             // Temps moyen total pour la fonction compress ou decompress
             return totalTime / repetitions; 
        }
    }

}