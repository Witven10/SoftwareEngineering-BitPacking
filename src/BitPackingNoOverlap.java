public class BitPackingNoOverlap {
    private final int k;
    private int[] tabcompress;

    public BitPackingNoOverlap(int k) { 
        this.k = k; }

    public int[] compress(int[] input) { 
        int nb_mot = 32/k; 
        tabcompress = new int[(int)Math.ceil((double)input.length / nb_mot)]; 
        //int index=0; //pour écrire dans tabcompress
        //int start=0; //le bit à partir duquel on commence à écrire
        for(int i=0; i<input.length; i++){
            int index= i/nb_mot; 
            int start= (i % (32 / k)) * k;
            tabcompress[index]=BitUtils.setBits(tabcompress[index], start, k, input[i]); 
        }
        return tabcompress; 

    }

    public int[] decompress(int[] output) { 
        int nb_mot = 32/k; 
        for(int i=0; i<output.length; i++){
            int index= i/nb_mot; 
            int start= (i % (32 / k)) * k;
            output[i]= BitUtils.getBits(tabcompress[index], start, k);           
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
