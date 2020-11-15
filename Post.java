import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Post {
    /*
    @overview:  Post è un tipo di dato astratto modificabile rappresentato da una quintupla (cinque campi ordinati) di valori, di cui solo l'ultimo
                è modificabile (un insieme modificabile):
                Elemento tipico: (id, auth, text, timestamp, {s_1, ..., s_n})
    */
    /* variabili d'istanza (private) */
    private Integer id;
    private String author;
    private String text;
    private Date timestamp;
    private Set<String> likes;

    /* variabile statica (private) per generare id */
    private static Random rng = new Random();

    /*
    Funzione di astrazione
    AF(x) = (x.id, x.author, x.text, x.timestamp, x.likes)
            
    Invariante di rappresentazione
    IR(x) = x != null 
            && x.id != null
            && x.author != null
            && x.text != null
            && x.timestamp != null 
            && x.likes != null
            && ∀ i. 0 <= i < x.likes.size()
                x.likes.get(i) != null
                && x.likes.get(i) != x.author
            && x.text.length() <= 140
            && ∀ i. 0 <= i < x.likes.size() - 1
                ∀ j. i < j < x.likes.size()
                    x.likes.get(i) != x.likes.get(j)
    */

    /*
    @requires:  author != null
                && text != null
                && date != null 
                && text.length() <= 140
    @throws:    TextOverflowException, NullPointerException
    @modifies:  this
    @effects:   Se almeno uno dei parametri è uguale a null, allora solleva         
                NullPointerException.
                Altrimenti se text.length() > 140 solleva TextOverflowException.
                Altrimenti crea un'istanza di Post con un set di like vuoto e gli altri parametri.
    */
    public Post(String author, String text, Date timestamp) throws TextOverflowException, NullPointerException {
        if (author == null || text == null || timestamp == null) {
            throw new NullPointerException();
        }
        // genero un id con valori in [0, Integer.MAX_VALUE - 1]
        this.id = rng.nextInt(Integer.MAX_VALUE - 1);
        /* voglio usare l'id nell'eccezione, allora devo generarlo prima */
        if (text.length() > 140) {
            throw new TextOverflowException(this.id);
        }
        this.author = author;
        this.text = text;
        this.timestamp = timestamp;
        this.likes = new HashSet<String>(); //inizializzo ad un set di likes vuoto
    }

    /* identico al precedente, ma passo un id esplicitamente: serve solo a creare conflitti tra id nel testing */
    public Post(Integer id, String author, String text, Date timestamp)
    throws TextOverflowException, NullPointerException {
        if (id == null || author == null || text == null || timestamp == null) {
            throw new NullPointerException();
        }
        if(text.length() > 140) {
            throw new TextOverflowException(id);
        }
        this.id = id;
        this.author = author;
        this.text = text;
        this.timestamp = timestamp;
        this.likes = new HashSet<String>(); //inizializzo ad un set di likes vuoto
    }
    
    /* 
        metodi osservatori
        La clausola modifies è omessa in quanto non modificano this
    */

    /*
    @requires:  true
    @effects:   ritorna una copia (shallow) di this.id
    */
    public Integer getId() {
        return (Integer) this.id.intValue();
    }

    /*
    @requires:  true
    @effects:   ritorna una copia (shallow) di this.author
    */
    public String getAuthor() {
        return new String(this.author);
    }

    /*
    @requires:  true
    @effects:   ritorna una copia (shallow) di this.text
    */
    public String getText() {
        return new String(this.text);
    }

    /*
    @requires:  true
    @effects:   ritorna una copia (shallow) di this.likes
    */
    public Set<String> getLikes() {
        return new HashSet<String>(this.likes);
    }

    /* metodi modificatori */
    /*
    @requires:  follower != null && this.author.equals(follower) == true
    @throws:    NullPointerException, SelfLikeException
    @modifies:  this
    @effects:   Se follower == null, allora solleva NullPointerException().
                Altrimenti se this.author.equals(follower) == true
                solleva SelfLikeException
                Altrimenti se 
                (this.author.equals(follower) == true) || (this.likes.contains(follower) == true)
                allora non fa niente.
                Altrimenti esegue this.likes.add(follower)
    */
    public void addLike(String follower) throws NullPointerException, SelfLikeException {
        if(follower == null) throw new NullPointerException();
        if (this.author.equals(follower))
            throw new SelfLikeException();
        
        if((this.author.equals(follower) == false) && (this.likes.contains(follower) == false)) {
            this.likes.add(follower);
        }
    }
    
    /*
    @requires:  true
    @effects:   ritorna, su una String, la rappresentazione di this nel formato espresso da AF
    */
    public String toString() {
        String cut_text = "";
        if (this.text.length() > 20) {
            cut_text = this.text.substring(0, 19) + "...";
        } else {
            cut_text = this.text;
        }
        String s = "(" + this.id + ", " + this.author + ", \"" + cut_text + "\", " + this.timestamp.toString() + ", "
                + this.likes + ")\n";
        return s;
    }

    /*
    @requires:  true
    @effects:   se other == null ritorna false, altrimenti ritorna true
                se e solo se this.getId().equals(other.getId())
    */
    public boolean equals(Post other) {
        if (other == null)
            return false;
        return this.id == other.getId();
    }
};
