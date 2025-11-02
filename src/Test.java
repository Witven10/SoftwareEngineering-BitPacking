public class Test{

    public static void testCompressionMethod(BitPacking compresseur, int[] input) {
        System.out.println("\n=== COMPRESSION ===");
        int[] compressed = compresseur.compress(input);
        //à décommanter pour debugger 
        /*System.out.println("");
        for (int compressWord3 : compressed) { // afficher les mots compressés en binaire, pour vérifier visuellement en cas d'erreur
            System.out.println(Integer.toBinaryString(compressWord3));
        } */
        System.out.println("Compression terminée. Taille compressée: " + compressed.length + " entiers.");
        int[] output = new int[input.length];
        output = compresseur.decompress(output); 

        System.out.println("\n=== DECOMPRESSION ===AND=== GET===");
        if(output.length != input.length) {
            System.out.println("Erreur: la taille du tableau décompressé ne correspond pas à la taille du tableau d'entrée.");
            return;
        } 
        boolean allCorrect = true;
        for (int i = 0; i < input.length; i++) {
            System.out.print(output[i] + " ");
            if(output[i] != input[i]) {
                System.out.println("\nErreur à l'indice " + i + ": valeur décompressée " + output[i] + " ne correspond pas à la valeur d'entrée " + input[i]);
                allCorrect = false;
            }
            if(compresseur.get(i) != input[i]) {
                allCorrect = false; 
                System.out.println("\nErreur dans get() à l'indice " + i + ": valeur retournée " + compresseur.get(i) + " ne correspond pas à la valeur d'entrée " + input[i]);
            }
        }

        System.out.println("\n");
        if(allCorrect) {
            System.out.println("Tout fonctionne correctement pour toutes les valeurs.");
        } else {
            System.out.println("Il y a des erreurs.");

        }
        //System.out.println("=============Fin du test pour ce mode=============");
    }
}


