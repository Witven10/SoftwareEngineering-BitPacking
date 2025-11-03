import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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
            //pour debug : System.out.print(output[i] + " ");
            if(output[i] != input[i]) {
                System.out.println("\nErreur à l'indice " + i + ": valeur décompressée " + output[i] + " ne correspond pas à la valeur d'entrée " + input[i]);
                allCorrect = false;
            }
            if(compresseur.get(i) != input[i]) {
                allCorrect = false; 
                System.out.println("\nErreur dans get() à l'indice " + i + ": valeur retournée " + compresseur.get(i) + " ne correspond pas à la valeur d'entrée " + input[i]);
            }
        }

        if(allCorrect) {
            System.out.println("Tout fonctionne correctement pour toutes les valeurs.");
        } else {
            System.out.println("Il y a des erreurs.");

        }
        //System.out.println("=============Fin du test pour ce mode=============");
    }



    /**
     * Génère un tableau d'entiers aléatoires avec une distribution pondérée.
     * Cette fonction est utile pour simuler des données où la majorité des valeurs
     * sont proches de la borne inférieure (test optimal pour le BitPackingOverflow).
     *
     * @param size La taille du tableau (max 2^27 - 1).
     * @param minBound La borne inférieure des valeurs (inclusive).
     * @param maxBound La borne supérieure des valeurs (inclusive).
     * @param biasToLow La pondération (0.0 à 1.0). 1.0 favorise les petites valeurs.
     * @return Le tableau d'entiers généré.
     */
    public static int[] generateTestArray(int size, int minBound, int maxBound, double biasToLow) {
        if (size > (1 << 27) - 1) {
            throw new IllegalArgumentException("La taille dépasse la limite de 2^27 - 1.");
        }
        if (minBound >= maxBound || biasToLow < 0.0 || biasToLow > 1.0) {
            throw new IllegalArgumentException("Paramètres de borne ou de biais invalides.");
        }

        int[] array = new int[size];
        Random random = new Random();
        int range = maxBound - minBound + 1;
        
        for (int i = 0; i < size; i++) {
            double r = random.nextDouble(); // Nombre aléatoire uniforme entre 0.0 et 1.0
            double biasedR;
            
            if (biasToLow > 0.5) {
                // Biais vers les petites valeurs (LSB)
                // La puissance augmente rapidement lorsque biasToLow se rapproche de 1.0
                // Ex: Si biasToLow = 0.9, power est 5. Si biasToLow = 0.95, power est 10.
                double power = 1.0 / (1.0 - biasToLow); 
                biasedR = Math.pow(r, power); 
            } else if (biasToLow < 0.5) {
                // Biais vers les grandes valeurs (MSB)
                double power = 1.0 / biasToLow;
                biasedR = 1.0 - Math.pow(1.0 - r, power);
            } else {
                // Distribution uniforme (biasToLow = 0.5)
                biasedR = r;
            }

            // Transformer le nombre biaisé en une valeur dans la plage [minBound, maxBound]
            array[i] = (int) (minBound + biasedR * range);
        }

        return array;
    }

    /**
     * Écrit le contenu d'un tableau d'entiers dans un fichier, un élément par ligne.
     * @param array Le tableau à écrire.
     * @param filename Le nom du fichier de sortie.
     */
    public static void writeArrayToFile(int[] array, String filename) {
        // Utilisation de try-with-resources pour s'assurer que les ressources sont fermées
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int value : array) {
                writer.write(String.valueOf(value));
                writer.newLine(); // Passe à la ligne suivante pour le prochain élément
            }
            System.out.println("Fichier de données généré avec succès : " + filename);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier " + filename + ": " + e.getMessage());
        }
    }
}


