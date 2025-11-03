public class BitPackingOverflow implements BitPacking {
    private final int k;
    private int[] tabcompress;

    public BitPackingOverflow(int k ) {  
        this.k = k;
 }
    @Override
    public int[] compress(int[] input) { 
        
        int k_prime=k; 
        int n= input.length;
        int n_overflow=0;
        int minBits = Integer.MAX_VALUE; // Initialiser avec une valeur maximale
        for( int kfind=1; kfind<=k; kfind++){ 
            int tmp_overflow=0;
            for(int i=0; i<n; i++){
                if (input[i] >= (1<<kfind)) { //si l'entier ne peut pas être stocké dans k_prime bits
                    tmp_overflow +=1; 
                }
            }
            int total_bits= 32 + n * (kfind+1) + tmp_overflow* k; //nombre total de bits à stocker
            if(total_bits < minBits) {
                minBits = total_bits;   
                k_prime = kfind;
                n_overflow= tmp_overflow;
            }
        }
        
        // 1. Calculer les bits requis pour l'index d'overflow (k_index)
        //car si kprime doit pouvoir stocker toutes les positions des entiers en overflow
        int k_index = 0;
        if (n_overflow > 0) {
            k_index = 32 - Integer.numberOfLeadingZeros(n_overflow - 1);
        }
        if (k_index > k_prime) {
            // Le k' optimal choisi pour la valeur est trop petit pour l'index d'overflow.
            // On force k_prime à être la taille requise pour l'index d'overflow.
            k_prime = k_index; 
            
            // kprime change, n_overflow doit être recalculé
            n_overflow = 0;
            for (int val : input) {
                if (val >= (1 << k_prime)) {
                    n_overflow++;
                }
            }
        }

        //J'ai choisi de stocker ( input.lenght,  k_prime;) en début de tableau compressé, pour pouvoir les récupérer lors de la décompression
        //avec k_prime 5 bits maximum car on va écrire cahque nombre avec k_prime+1 bits (0 ou 1 + k' bits pour le nombre)
        //J'ai choisi de limiter input.lenght sur les 27 bits restants 
        //pour simplifier le code j'ai décider d'écrire ces données que sur un int (32 bits)
        // on peut donc stocker des tableaux de taille maximum 134217727 (2^27-1) 
        if(input.length > 134217727) {
            throw new IllegalArgumentException("Taille du tableau d'entrée dépasse la limite de 134217727 (2^27-1) : " + input.length);
        }

        int total_bits= 32 + input.length * (k_prime+1) + n_overflow * k; //nombre total de bits à stocker
        tabcompress = new int[(int)Math.ceil((double)(total_bits)/32)];
        


        //on écrit d'abord les la taille et k' au début de tabcompress
        
        tabcompress[0]= BitUtils.setBits(tabcompress[0], 0, 27, input.length); //on écrit input.length sur 12 bits
        tabcompress[0]=BitUtils.setBits(tabcompress[0], 27, 5, k_prime); //on écrit k' sur 5 bits 
        int k_plus1= k_prime + 1;    
        int pos_overflow=0; //position de l'entier en overflow dans la liste des entiers en overflow 
        for(int i=0; i<input.length; i++){
            // pour trouver la case dans tabcompress de input[i](vraie valeaur ou position), pour le ième entier, on a déjà stocké 32+i*k_plus1 bits
            //donc on commence à écrire l'entier input[i] à la position 32+i*k_plus1, on est dans la case index= (32+i*k_plus1)/32.
            int index= (32+i*k_plus1)/32; 
            int start= (32+i*k_plus1) % 32; // on a déjà écrit 32+i*k_plus1 bits ( de 0 à i*k_plus1-1), donc on commence à écrire input[i] à la position 32+i*k_plus1 qui se trouve dans tabcompress[index],
            //donc le bit de départ dans tabcompress[index] est start= (32+i*k_plus1) % 32
            
            //si on peut écrire l'entier dans k_prime bits   
            if(input[i] < (1<<k_prime)){ 
                int writebits1= 32-start; // nombre de bits qu'on peut écrire dans tabcompress[index] à partir de start
                if(writebits1 >= k_plus1){ // si on peut écrire tous les k_plus1 bits dans tabcompress[index]
                    int val_a_ecrire = input[i] & BitUtils.maskK(k_prime); //on prend les k' bits de poids faible de l'entier à écrire
                    int valeur_totale =  (val_a_ecrire<<1) | 0; // on ajoute le bit de contrôle à 0 au début, on le met sur le bit de poids faible pour y accéder plus simplement
                    // j'écris la valeur totale (bit de contrôle + k' bits de l'entier) dans tabcompress
                    tabcompress[index] = BitUtils.setBits(tabcompress[index], start, k_plus1, valeur_totale);       
                }
                else{ // sinon on doit écrire les writebits1 bits dans tabcompress[index] et les k_plus1-writebits1 bits dans tabcompress[index+1]
                    int val_a_ecrire = input[i] & BitUtils.maskK(k_prime); //on prend les k' bits de poids faible de l'entier à écrire
                    int valeur_totale =  (val_a_ecrire<<1) | 0; // on ajoute le bit de contrôle à 0 au début, on le met sur le bit de poids faible pour y accéder plus simplement
                    tabcompress[index] = BitUtils.setBits(tabcompress[index], start, writebits1, valeur_totale); 
                    if (index + 1 < tabcompress.length) {
                        int temp = valeur_totale>>writebits1; // on décale input[i] de writebits1 positions vers la droite pour écrire les bits restants
                        tabcompress[index+1]=BitUtils.setBits(tabcompress[index+1], 0, k_plus1-writebits1, temp); 
                    }
                } 
            }
            else{  
                int writebits1= 32-start; // nombre de bits qu'on peut écrire dans tabcompress[index] à partir de start
                if(writebits1 >= k_plus1){ // si on peut écrire tous les k_plus1 bits dans tabcompress[index]
                    int val_a_ecrire = pos_overflow & BitUtils.maskK(k_prime); //on prend les k' bits de poids faible de la position en overflow à écrire
                    int valeur_totale =  (val_a_ecrire<<1) | 1; // on ajoute le bit de contrôle à 0 au début, on le met sur le bit de poids faible pour y accéder plus simplement
                    tabcompress[index] = BitUtils.setBits(tabcompress[index], start, k_plus1, valeur_totale);     
                    
                }
                else{ // sinon on doit écrire les writebits1 bits dans tabcompress[index] et les k_plus1-writebits1 bits dans tabcompress[index+1]
                    int val_a_ecrire = pos_overflow & BitUtils.maskK(k_prime); //on prend les k' bits de poids faible de l'entier à écrire
                    int valeur_totale =  (val_a_ecrire<<1) | 1; // on ajoute le bit de contrôle à 0 au début, on le met sur le bit de poids faible pour y accéder plus simplement
                    tabcompress[index] = BitUtils.setBits(tabcompress[index], start, writebits1, valeur_totale); 
                    if (index + 1 < tabcompress.length) {
                        int temp = valeur_totale>>writebits1; // on décale input[i] de writebits1 positions vers la droite pour écrire les bits restants
                        tabcompress[index+1]=BitUtils.setBits(tabcompress[index+1], 0, k_plus1-writebits1, temp); 
                    }
                }
                //on écrit l'entier en overflow à la position correspondante
                int overflow_index= (32 + input.length * k_plus1 + pos_overflow  * k)/32; //on a déja écrit 32 + input.length * k_plus1 bits avant les entiers en overflow
                //donc on commence à écrire l'entier en overflow à la position 32 + input.length * k_plus1 + pos_overflow * k
                int overflow_start= (32 + input.length * k_plus1 + pos_overflow * k) %32; //c'est la même logique que pour trouver index et start

                int writebits1_o= 32-overflow_start; // nombre de bits qu'on peut écrire dans tabcompress[index] à partir de overflow_start
                if(writebits1_o >= k){ // si on peut écrire tous les k bits dans tabcompress[overflow_index]
                    tabcompress[overflow_index]=BitUtils.setBits(tabcompress[overflow_index], overflow_start, k, input[i]);           
                }
                else{ // sinon on doit écrire les writebits1_o bits dans tabcompress[overflow_index] et les k-writebits1_o bits dans tabcompress[overflow_index+1]
                    tabcompress[overflow_index]=BitUtils.setBits(tabcompress[overflow_index], overflow_start, writebits1_o, input[i]); 
                    if (overflow_index + 1 < tabcompress.length) {
                        int temp = input[i]>>writebits1_o; // on décale input[i] de writebits1 positions vers la droite pour écrire les bits restants
                        tabcompress[overflow_index+1]=BitUtils.setBits(tabcompress[overflow_index+1], 0, k-writebits1_o, temp); 
                    }
                } 
                pos_overflow +=1; 
                }
        }
        return tabcompress; 

    }

    @Override
    public int[] decompress(int[] output) { 
        for(int i=0; i<output.length; i++){
            output[i]= get(i);        
        }
        return output; 
        

    }

    @Override
    public int get(int i) { 

        int taille= BitUtils.getBits(tabcompress[0], 0, 27);
        int k_prime= BitUtils.getBits(tabcompress[0], 27, 5);
        int k_plus1= k_prime + 1;

        int index= (32+i*k_plus1)/32; 
        int start= (32+i*k_plus1) % 32;
        int writebits1= 32-start; // nombre de bits qu'on peut écrire dans tabcompress[index] à partir de start
        int retour = 0;  
        int control_bit= 0; 
        int valeur_totale=0;
        if(writebits1 >= k_plus1){ // si on peut écrire tous les k bits dans tabcompress[index]
            valeur_totale= BitUtils.getBits(tabcompress[index], start, k_plus1);           
        }
        else{ // sinon on doit lire les writebits1 bits dans tabcompress[index] et les k_prime-writebits1 bits dans tabcompress[index+1]
            int part1= BitUtils.getBits(tabcompress[index], start, writebits1);
            valeur_totale = part1; //au cas où on n'arrive pas à lire la totalité des bits, on initialise valeur_totale avec la partie lue
            if (index + 1 < tabcompress.length){  
                int part2= BitUtils.getBits(tabcompress[index+1], 0, k_plus1 - writebits1);
                valeur_totale=  part1 | (part2 << writebits1); 
            }
        }
        control_bit= valeur_totale & 1; //le bit de contrôle est le bit de poids faible
        retour= valeur_totale >> 1; //on décale de 1 position vers la droite pour obtenir la valeur sans le bit de contrôle
        if(control_bit ==0){ //l'entier est stocké directement
            return retour;
        }
        else{ //l'entier est en overflow
            int pos_overflow= retour;
            int overflow_index= (32 + taille * k_plus1 + pos_overflow  * k)/32; 
            int overflow_start= (32 + taille * k_plus1 + pos_overflow * k) %32; //c'est la même logique que pour trouver les trouver dans les fonctions précédenates

            int writebits1_o= 32-overflow_start;
            int retour_overflow=0; 
            if(writebits1_o >= k){ // si on peut écrire tous les k bits dans tabcompress[overflow_index]
                retour_overflow= BitUtils.getBits(tabcompress[overflow_index], overflow_start, k);           
            }
            else{ // sinon on doit lire les writebits1_o bits dans tabcompress[overflow_index] et les k-writebits1_o bits dans tabcompress[overflow_index+1]
                int part1= BitUtils.getBits(tabcompress[overflow_index], overflow_start, writebits1_o);
                retour_overflow = part1; //si on est ici, c'est que l'entier en overflow est mal formé, on retourne au moins la partie lue

                if (overflow_index + 1 < tabcompress.length) {
                    int part2= BitUtils.getBits(tabcompress[overflow_index+1], 0, k - writebits1_o);
                    retour_overflow=  part1 | (part2 << writebits1_o); 
                }
            } 
            return retour_overflow;
        }

    }
}
