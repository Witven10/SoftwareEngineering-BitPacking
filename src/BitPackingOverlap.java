public class BitPackingOverlap {
    private final int k;
    private int[] tabcompress;

    public BitPackingOverlap(int k) { 
        this.k = k; }

    public int[] compress(int[] input) { 
        tabcompress = new int[(int)Math.ceil((double)(input.length*k)/32)];  //le nombre de bits exact qu'on veut écrire est input.length*k, donc le nombre de cases nécessaires est (input.length*k)/32 arrondi à l'entier supérieur
        for(int i=0; i<input.length; i++){
            int index= i*k/32; // pour trouver la case dans tabcompress de input[i], pour le ième entier, on a déjà stocké i*k bits
            //donc le on commence à écrire l'entier input[i] à la position i*k, on est dans la case index= (i*k)/32.
            int start= (i*k) % 32; // on a déjà écrit i*k bits ( de 0 à i*k-1), donc on commence à écrire input[i] à la position i*k qui se trouve dans tabcompress[index],
            //donc le bit de départ dans tabcompress[index] est start= (i*k) % 32
            
            int writebits1= 32-start; // nombre de bits qu'on peut écrire dans tabcompress[index] à partir de start
            if(writebits1 >= k){ // si on peut écrire tous les k bits dans tabcompress[index]
                tabcompress[index]=BitUtils.setBits(tabcompress[index], start, k, input[i]);           
            }
            else{ // sinon on doit écrire les writebits1 bits dans tabcompress[index] et les k-writebits1 bits dans tabcompress[index+1]
                tabcompress[index]=BitUtils.setBits(tabcompress[index], start, writebits1, input[i]); 
                int temp = input[i]>>writebits1; // on décale input[i] de writebits1 positions vers la droite pour écrire les bits restants
                tabcompress[index+1]=BitUtils.setBits(tabcompress[index+1], 0, k-writebits1, temp); 
            } 
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
        int index= i*k/32; 
        int start= (i*k) % 32; 
        int writebits1= 32-start; // nombre de bits qu'on peut écrire dans tabcompress[index] à partir de start 
        if(writebits1 >= k){ // si on peut écrire tous les k bits dans tabcompress[index]
            return BitUtils.getBits(tabcompress[index], start, k);           
        }
        else{ // sinon on doit lire les writebits1 bits dans tabcompress[index] et les k-writebits1 bits dans tabcompress[index+1]
            int part1= BitUtils.getBits(tabcompress[index], start, writebits1);
            int part2= BitUtils.getBits(tabcompress[index+1], 0, k - writebits1);
            return part1 | (part2 << writebits1); 
        }       

    }
}
