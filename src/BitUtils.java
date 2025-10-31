//BitUtils.java regroupe les fonctions utilitaires pour manipuler les bits, que je compte utiliser dans plusieurs classes.
//maskK: crée un masque de k bits à 1
//getBits: lit k bits à partir de start
//setBits: écrit value (k bits) dans word à partir de start
//getK: pour obtenir k à partir d'un tableau d'entiers

public class BitUtils {
    // masque k bits à 1 
    public static int maskK(int k) { 
        if(k==0) return 0;
        else if(k==32) return -1; //cas particulier: décalage de 1 de 32 positions vers la gauche donne 0, donc on retourne -1 qui est représenté par 32 bits à 1 en binaire
        else return (1<<k)-1; // décalage de 1 de k positions vers la gauche, puis -1 pour avoir k bits à 1
     }

    // lire k bits à partir de start
    public static int getBits(int word, int start, int k) { 
        return (word>>start) & maskK(k);  // Je décale le mot de start positions vers la droite, puis j'applique le masque de k bits à 1, afin de ne garder que les k bits lus
     }

    // écrire value (k bits) dans word à partir de start
    public static int setBits(int word, int start, int k, int value) {
        int maskset= maskK(k)<<start;  // masque de k bits à 1 décalé de start positions vers la gauche
        word= word & ~maskset;    //je mets à 0 les k bits à partir de start dans word
        return word | value<<start; //je fais un ou logique entre word et value décalé de start positions vers la gauche, afin d'écrire value dans word
    }

    //pour obtenir k
    public static int getK(int[] input){
        int max= 0; 
        for(int val: input){
            if(val > max){
                max=val; 
            }
        }
        if (max == 0) return 1;
        return 32 - Integer.numberOfLeadingZeros(max);

    }
}
