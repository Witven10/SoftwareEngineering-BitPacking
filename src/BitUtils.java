public class BitUtils {
    // masque k bits à 1 
    public static int maskK(int k) { 
        if(k==0) return -1; 
        else return (1<<k)-1;
     }

    // lire k bits à partir de start
    public static int getBits(int word, int start, int k) { 
        return (word>>start) & maskK(k); 
     }

    // écrire value (k bits) dans word à partir de start
    public static int setBits(int word, int start, int k, int value) {
        int maskset= maskK(k)<<start; 
        word= word & ~maskset; 
        return word | value<<start; 
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
