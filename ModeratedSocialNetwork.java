import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

class ModeratedSocialNetwork extends SocialNetwork {
    /*
    @overview:  ModeratedSocialNetwork è un SocialNetwork e possiede
                un insieme di post segnalati, quindi può essere vista come una coppia
                con elemento tipico: 
                (SocialNetwork, reported) dove reported = {id_1, ..., id_n}
    */

    // il Set di id dei post offensivi
    private Set<Integer> reported;
    // creo una hashtable statica da usare come dizionario per le parole offensive
    private static Hashtable<Integer, String> badwords = new Hashtable<Integer, String>();

    // blocco static per inizializzare il dizionario
    static {
        Scanner s = null;
        try {
            File f = new File("badwords.txt");
            s = new Scanner(f);
        } catch (Exception ex) {
            System.out.println("Caught:" + ex);
        }
        String word = null;
        while (s.hasNext()) {
            word = s.nextLine();
            badwords.put(word.hashCode(), word);
        }
        s.close();
    }

    /*
    Costruttore rete sociale moderata vuota
    
    @requires:  true
    @modifies:  this
    @effects:   Crea la rete sociale moderata (SocialNetwork(), {})
    */
    public ModeratedSocialNetwork() {
        super();
        this.reported = new HashSet<Integer>();
    }

    /*
    Costruttore rete sociale inizializzata dalla lista di post pList
    
    @requires:  true
    (in quanto le precondizioni sono controllate comunque dal costruttore di SocialNetwork, potrebbe quindi sollevare NullPointerException)
    @modifies:  this
    @throws:    NullPointerException (unchecked)
    @effects:   Crea la rete sociale moderata (SocialNetwork(ps), {})
    */
    public ModeratedSocialNetwork(List<Post> pList) throws NullPointerException {
        super(pList); // solleva NullPointerException se pList == null
        this.reported = new HashSet<Integer>();
        for (Post p : pList) {
            for (String w : p.getText().split(" ")) {
                if (badwords.get(w.hashCode()) != null) {
                    reported.add(p.getId());
                }
            }
        }
    }

    /**
    Sovrascrivo addPost()
    @requires:  true
    @throws:    DuplicatePostException, NullPointerException 
                se lo fa il corrispondente metodo della superclasse
    @modifies:  this
    @effects:   Esegue super.addPost(p) e poi se 
                (∃ i. i ∊ badwords.values() && p.getText() contiene la parola i)
                esegue reported = reported U {p.getId()}
    */
    public void addPost(Post p) throws DuplicatePostException, NullPointerException {
        super.addPost(p); // prima aggiungo il post alla rete
        for (String w : p.getText().split(" ")) {
            if (badwords.get(w.hashCode()) != null) {
                reported.add(p.getId());
            }
        }
    }

    /**
    Sovrascrivo addPost()
    @requires:  true
    @throws:    NoSuchPostException, NullPointerException 
                se lo fa il corrispondente metodo della superclasse
    @modifies:  this
    @effects:   Esegue super.rmPost(p) e poi se 
                reported.contains(pid)
                esegue reported = reported - {p.getId()}
    */
    public void rmPost(Integer pid) throws NoSuchPostException, NullPointerException {
        super.rmPost(pid); // prima rimuovo il post dalla rete
        if (this.reported.contains(pid)) {
            // lo rimuovo dai segnalati
            this.reported.remove(pid);
        }
    }

    /**
    Ritorna il Set di post segnalati (copia shallow)
    
    @requires:  true
    @effects:   {reported.get(i) : 0 <= i < reported.size()}
     */
    public Set<Integer> getOffensive() {
        return new HashSet<Integer>(this.reported);
    }

    /**
    Ritorna il Set di post parole offensive (i valori del dizionario)
    
    @requires:  true
    @effects:   {badwords.values()}
     */
    public static Set<String> getBadwords() {
        return new HashSet<String>(badwords.values());
    }
};