import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Post {
    /*
    @overview:  Post è un tipo di dato astratto modificabile rappresentato da una 
                quintupla, di cui l'ultimo elemento è un insieme modificabile, quindi 
                globalmente è un tipo di dato modificabile.
                Elemento tipico: (id, author, text, timestamp, likes)
                dove likes = {like_1, ..., like_n}
    */

    /* variabili d'istanza (private) */
    private Integer id;
    private String author;
    private String text;
    private Date timestamp; // data e ora di pubblicazione del post
    private Set<String> likes;

    /* variabile statica (privata) per generare id */
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
            && ∀ i.
                0 <= i < x.likes.size()
                && x.likes.get(i) != null
                && !x.likes.get(i).equals(x.author)
            && x.text.length() <= 140
            && ∀ i. 
                0 <= i < x.likes.size()
                && ∀ j. 
                    0 <= j < x.likes.size()
                    && (
                        (i != j && !x.likes.get(i).equals(x.likes.get(j)))
                        ||
                        (i == j && x.likes.get(i).equals(x.likes.get(j)))
                    )
    */

    /*
    @requires:  author != null
                && text != null
                && timestamp != null 
                && text.length() <= 140
    @throws:    Se almeno uno dei parametri è null allora solleva NullPointerException.
                Se text.length() > 140 allora solleva TextOverflowException.
    @modifies:  this
    @effects:   Crea un'istanza di Post con id >= 0 generato casualmente
                e nessun like (Set vuoto), ovvero:
        
                (rng.nextInt(), author, text, timestamp, {})
    */
    public Post(String author, String text, Date timestamp) throws TextOverflowException, NullPointerException {
        if (author == null || text == null || timestamp == null) {
            throw new NullPointerException();
        }
        // genero un id con valori in [0, Integer.MAX_VALUE - 1]
        this.id = rng.nextInt(Integer.MAX_VALUE - 1);
        if (text.length() > 140) {
            throw new TextOverflowException(this.id);
        }
        this.author = author;
        this.text = text;
        this.timestamp = timestamp;
        this.likes = new HashSet<String>();
    }

    /*
    identico al precedente, ma invece di generare un id lo passo come parametro:
    serve a creare intenzionalmente conflitti tra id e non dovrebbe essere usato
    in un'implementazione reale
    
    @requires:  id != null
                && author != null
                && text != null
                && timestamp != null 
                && text.length() <= 140
    @throws:    Se almeno uno dei parametri è null allora solleva NullPointerException.
                Se text.length() > 140 allora solleva TextOverflowException.
    @modifies:  this
    @effects:   Crea un'istanza di Post con id >= 0 generato casualmente
                e nessun like (Set vuoto), ovvero:
        
                (rng.nextInt(), author, text, timestamp, {})
    */
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
        this.likes = new HashSet<String>();
    }

    /*
    @requires:  true
    @effects:   ritorna una copia (shallow) dell'id
    */
    public Integer getId() {
        return (Integer) this.id.intValue();
    }

    /*
    @requires:  true
    @effects:   ritorna una copia (shallow) di author
    */
    public String getAuthor() {
        return new String(this.author);
    }

    /*
    @requires:  true
    @effects:   ritorna una copia (shallow) di text
    */
    public String getText() {
        return new String(this.text);
    }

    /*
    @requires:  true
    @effects:   ritorna una copia (shallow) del set likes di this
                Se anche fosse modificato il set ritornato
                non avrei effetti su quello contenuto in this
                (le stringhe non sono mutabili)
    */
    public Set<String> getLikes() {
        return new HashSet<String>(this.likes);
    }

    /* metodi modificatori */
    /*
    @requires:  follower != null && !author.equals(follower)
    @throws:    Se follower == null, allora solleva NullPointerException.
                Se author.equals(follower) solleva SelfLikeException
                (l'autore del post non può seguire se stesso)
    @modifies:  this
    @effects:   Esegue likes = likes U {follower}
                (come da definizione di insieme non ho duplicati)
    */
    public void addLike(String follower) throws NullPointerException, SelfLikeException {
        if (follower == null)
            throw new NullPointerException();
        // l'autore non può comparire nel Set dei like
        if (this.author.equals(follower))
            throw new SelfLikeException();
        this.likes.add(follower); // aggiungo l'utente al Set di like
    }
    
    /*
    @requires:  true
    @effects:   ritorna la quintupla (id, author, text, timestamp, likes)
                sotto forma di una String
    */
    public String toString() {
        // Se il testo ha lunghezza > 20 allora inserisco solo la sottostringa formata
        // dai primi venti caratteri per evitaredi produrre un output meno leggibile
        String cut_text;
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
    @effects:   Se other == null ritorna false
                Se id.equals(other.getId()) ritorna true,
                altrimenti ritorna false
    */
    public boolean equals(Post other) {
        if (other == null)
            return false;
        return this.id.equals(other.getId());
    }
};
