public class BitPackingNoOverlap {
    private final int k;
    private int[] tabcompress;

    public BitPackingNoOverlap(int k) { 
        this.k = k; }

    public int[] compress(int[] input) { 
        int nb_mot = 32/k; 
        tabcompress = new int[(int)Math.ceil((double)input.length / nb_mot)];  //crée le tableau pour compresser avec le bon nombre de cases
        for(int i=0; i<input.length; i++){
            if(input[i] < 0 || input[i] >= (1<<k)) { //vérifie que la valeur peut être stockée dans k bits
                throw new IllegalArgumentException("Valeur ne peut pas être représentée sur k bits: " + input[i]);
            }

            int index= i/nb_mot; //pour trouver la case dans tabcompress de input[i]
            int start= (i % (32 / k)) * k; //pour trouver le bit à partir duquel on écrit dans tabcompress[index]
            tabcompress[index]=BitUtils.setBits(tabcompress[index], start, k, input[i]); 
        }
        return tabcompress; 

    }

    public int[] decompress(int[] output) { 
        for(int i=0; i<output.length; i++){
            output[i]= get(i);        
        }
        return output; 
        

    }

    public int get(int i) { 
        int nb_mot = 32/k;
        int index= i/nb_mot; 
        int start= (i % (32 / k)) * k;
        return BitUtils.getBits(tabcompress[index], start, k);           

    }
}
