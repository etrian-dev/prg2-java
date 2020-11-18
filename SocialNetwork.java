import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SocialNetwork {
    /*
    @overview:  SocialNetwork è un tipo di dato astratto modificabile definito una tripla:
    due funzioni parziali mutabili sia nel dominio che nel codominio ed una lista di Post
    elemento tipico:    (
                            followers: user -> {user_1, ..., user_n},
                            following: user -> {user*_1, ..., user*_n},
                            [v_1, ..., v_n]
                        )
    */

    /* variabili d'istanza (private) */
    private Map<String, Set<String>> following; // associa ad ogni utente gli utenti che segue
    private Map<String, Set<String>> followers; // associa ad ogni utente gli utenti che lo seguono
    private List<Post> posts;

    /*
    costante statica che fissa il numero minimo di like che un utente deve 
    ricevere per essere considerato un influencer
    */
    private static final int likeTreshold = 10;

    /*
    Funzione di astrazione
    AF(x) = (
                followers: x.followers.keySet() -> x.followers.get(i),
                following: x.followers.keySet() -> x.followers.get(i),
                x.posts
            )
        
    Invariante di rappresentazione
    IR(x) = x != null
            && x.posts != null
            && ∀ i. 0 <= i < x.posts.size()
                IR_Post(x.posts.get(i)) == true
            && x.following.keySet() =  {x.posts.get(j).getLikes() : 0 <= j < x.posts.size()}
            && ∀ i. i ∊ x.following.keySet()
                    && x.following.get(i) = 
                        {x.posts.get(k).getAuthor() : 
                            0 <= k < x.posts.size()
                            && x.posts.get(k).getLikes().contains(i)
                        }
            && x.followers.keySet() =  {x.posts.get(j).getAuthor() : 0 <= j < x.posts.size()}
            && ∀ i. i ∊ x.followers.keySet()
                    && x.followers.get(i) = 
                        {x.posts.get(k).getLikes() : 
                            0 <= k < x.posts.size() 
                            && x.posts.get(k).getAuthor().equals(i)
                        }
    */

    /*
    @requires:  true
    @effects:   Crea una rete sociale vuota, ovvero
                (
                    this.followers: ∅ -> ∅,
                    this.following: ∅ -> ∅,
                    []
                )
    
    */
    public SocialNetwork() {
        this.posts = new ArrayList<Post>();
        this.following = new HashMap<String, Set<String>>();
        this.followers = new HashMap<String, Set<String>>();
    }

    /*
    @requires:  ps != null
    @throws:    Se ps == null solleva NullPointerException
    @modifies:  this
    @effects:   Crea un'istanza di SocialNetwork indotta dai post in ps, ovvero
                la tripla formata dalle mappe followers, following e la lista di post:
        
                (
                    this.followers:
                    ∀ j ∊ {ps.get(i).getAuthor() : 0 <= i < ps.size()}
                    la mappa associa
                    j -> {ps.get(k).getLikes() : 0 <= k < ps.size() && ps.get(k).getAuthor().equals(j)}
                    ,
                    this.following:
                    ∀ j ∊ {ps.get(i).getLikes() : 0 <= i < ps.size()}
                    la mappa associa
                    j -> {ps.get(k).getAuthor() : 0 <= k < ps.size() && ps.get(k).getLikes().contains(j)}
                    ,
                    ps
                )
    */
    public SocialNetwork(List<Post> ps) throws NullPointerException {
        if (ps == null)
            throw new NullPointerException();
        // prima creo delle mappe vuote e la lista vuota
        this.posts = new ArrayList<Post>();
        this.following = new HashMap<String, Set<String>>();
        this.followers = new HashMap<String, Set<String>>();
        // Poi aggiorno le mappe e la lista con i dati di ps
        for (Post p : ps) {
            this.posts.add(p);
            updateFollowers(p, this.followers);
            updateFollowing(p, this.following);
        }
    }

    /** Aggiorno la mappa di chi segue l'autore del post */
    /*
    @requires:  post != null && followAuth != null
    @throws:    Se post == null o followAuth == null solleva NullPointerException
    @modifies:  this
    @effects:   Chiamo j = post.getAuthor()
                Se {followAuth.get(j)} != ∅     (cioè la chiave j appartiene alla mappa)
                    aggiorno il codominio di followAuth.get(j) a
                    {followAuth.get(j)} U post.getAuthor()
                Altrimenti aggiungo la coppia chiave -> valore
                    j -> {post.getLikes()}
    */
    private void updateFollowers(Post post, Map<String, Set<String>> followAuth) throws NullPointerException {
        if (post == null || followAuth == null)
            throw new NullPointerException();
        if (followAuth.get(post.getAuthor()) != null) {
            // merge dei set di like se esiste già la chiave autore
            followAuth.get(post.getAuthor()).addAll(post.getLikes());
        } else {
            // l'autore non era presente nella mappa, allora devo inserire la coppia chiave -> valore
            followAuth.put(post.getAuthor(), post.getLikes());
        }
    }

    /** Aggiorno la mappa degli utenti seguiti dal set di utenti che hanno messo like al post */
    /*
    @requires:  post != null && iFollow != null
    @throws:    Se post == null o iFollow == null solleva NullPointerException
    @modifies:  this
    @effects:   ∀ j. j ∊ post.getLikes()
                    Se {iFollow.get(j)} != ∅
                        aggiorno il codominio di iFollow.get(j) a
                        {iFollow.get(j)} U post.getAuthor()
                    Altrimenti aggiungo la coppia chiave -> valore
                        j -> {post.getAuhtor()}
    */
    private void updateFollowing(Post post, Map<String, Set<String>> iFollow) throws NullPointerException {
        if (post == null || iFollow == null)
            throw new NullPointerException();
        Set<String> helper = new HashSet<String>();
        for (String s : post.getLikes()) {
            if (iFollow.get(s) != null) {
                iFollow.get(s).add(post.getAuthor());
            } else {
                // ho bisogno del set helper perchè non posso passare la stringa
                // direttamente al costruttore di HashSet
                helper.add(post.getAuthor());
                iFollow.put(s, new HashSet<String>(helper));
                // ogni volta chiaramente deve essere svuotato
                helper.clear();
            }
        }
    }

    /** 
    Devo rimuovere l'autore del post rimosso dalla mappa dei seguiti se non ha altri post nella rete.
    Devo togliere l'autore del post rimosso dai seguiti degli utenti se essi hanno messo like
    a tale autore soltanto nel post rimosso.
    Sfrutto inoltre il fatto di aver già tolto post da this.posts, altrimenti dovrei controllare l'id dei post.
    
    @requires:  post != null
    @throws:    Se post == null allora solleva NullPointerException
    @modifies:  this
    @effects:   ∀ k ∊ this.following.keySet()
                    Se k ∉ {this.posts.get(i).getLikes().get(j) : 
                            ∀ j. 0 <= j < this.posts.getLikes().size()
                            ∀ i. 0 <= i < this.posts.size()
                            && this.posts.get(i).getAuthor().equals(post.getAuthor())}
                    allora tolgo post.getAuthor() dal codominio di this.following.get(k),
                    cioè rimuovo l'autore del post dalle persone seguite da k
                Se (∄ i. 
                        0 <= i < this.posts.size() 
                        && this.posts.get(i).getAuthor().equals(post.getAuthor())
                    )
                allora rimuovo la chiave post.getAuthor() dalla mappa followers
    */
    private void rmFromMaps(Post post) {
        if (post == null)
            throw new NullPointerException();
        boolean noMorePosts = true;
        Set<String> hasOtherLikes = new HashSet<String>();
        // Seleziono, tra i post rimanenti, quelli dello stesso autore del post
        for (Post element : this.posts) {
            if (element.getAuthor().equals(post.getAuthor())) {
                // ho trovato almeno un altro post con lo stesso autore
                noMorePosts = false;
                // considero chi vi ha messo like e li aggiungo ad un set
                hasOtherLikes.addAll(element.getLikes());
            }
        }
        for (String user : this.following.keySet()) {
            if (!hasOtherLikes.contains(user)) {
                this.following.get(user).remove(post.getAuthor());
            }
        }
        if (noMorePosts) {
            this.followers.remove(post.getAuthor());
        }
    }

    /** metodo per aggiungere post alla rete sociale */
    /*
    @requires:  p != null 
                && (∀ i. 
                    0 <= i < this.posts.size() 
                    && !p.getId().equals(this.posts.get(i).getId()
                    )
    @throws:    Se p == null solleva NullPointerException
                Se (∃ i.
                    0 <= i < this.posts.size() 
                    && p.getId().equals(this.posts.get(i).getId()
                    )
                    allora solleva DuplicatePostException
    @modifies:  this
    @effects:   Esegue this.posts.add(p) e poi aggiorna le mappe tramite le funzioni
                definite sopra
    */
    public void addPost(Post p) throws NullPointerException, DuplicatePostException {
        if (p == null)
            throw new NullPointerException();
        for (Post el : this.posts) {
            if (el.getId().equals(p.getId()))
                // nell'eccezione stampo anche l'id del post che ha provocato il conflitto
                throw new DuplicatePostException(p.getId());
        }
        // aggiungo alla lista
        this.posts.add(p);
        // aggiorno le mappe
        updateFollowers(p, this.followers);
        updateFollowing(p, this.following);
    }

    /** metodo per rimuovere un post dalla rete sociale */
    /*
    @requires:  p != null 
                && (∃ i. 
                    0 <= i < this.posts.size() 
                    && p.getId().equals(this.posts.get(i).getId())
    @throws:    Se p == null solleva NullPointerException
                Se (∀ i.
                    0 <= i < this.posts.size() 
                    && !p.getId().equals(this.posts.get(i).getId())
                    allora solleva NoSuchPostException
    @modifies:  this
    @effects:   Esegue this.posts.remove(p) e poi aggiorna le mappe tramite la funzione
                rmFromMaps()
    */
    public void rmPost(Post p) throws NullPointerException, NoSuchPostException {
        if (p == null)
            throw new NullPointerException();
        if (this.posts.contains((Post) p) == false) {
            throw new NoSuchPostException();
        }
        // rimuovo dalla lista
        this.posts.remove(p);
        // e aggiorno le mappe
        this.rmFromMaps(p);
    }

    /*
    Ritorna sotto forma di mappa <String, Set<String>> l'insieme delle persone seguite 
    da ogni utente derivandolo dai post passati come parametro
    
    @requires:  ps != null
    @throws:    Se ps == null solleva NullPointerException
    @effects:   Ritorna la rete sociale derivata da ps, ovvero la funzione che
                ∀ i. i ∊ {ps.get(j).getLikes() : 0 <= j < ps.size()}
                mappa
                i
                ->
                {ps.get(k).getAuthor() : 0 <= k < ps.size() && ps.get(k).getLikes().contains(i)}
    */
    public Map<String, Set<String>> guessFollowers(List<Post> ps) {
        if (ps == null)
            throw new NullPointerException();
        // parto da una mappa vuota e la aggiorno con ogni post della lista
        Map<String, Set<String>> m = new HashMap<String, Set<String>>();
        for (Post p : ps) {
            updateFollowing(p, m);
        }
        return m;
    }

    // analoga alla precedente, cambia solo la mappa che viene aggiornata
    public Map<String, Set<String>> guessFollowing(List<Post> ps) {
        if (ps == null)
            throw new NullPointerException();
        Map<String, Set<String>> m = new HashMap<String, Set<String>>();
        for (Post p : ps) {
            updateFollowers(p, m);
        }
        return m;
    }

    /*  Ritorna la lista (senza duplicati) degli utenti in this che sono seguiti da più
        persone di quante ne seguano. (posso limitarmi a considerare le chiavi di 
        this.followers in quanto se nessuno ha messo like ad un utente, allora non può 
        essere un influencer).
    
    @requires:  true
    @effects:   Ritorna [i : 
                            i ∊ this.followers.keySet()
                            && #{this.followers.get(i)} > #{this.following.get(i)}
                        ]
    */
    public List<String> influencers() {
        List<String> influencers = new ArrayList<String>();
        for (String user : this.followers.keySet()) {
            if (!influencers.contains(user)) {
                if (this.following.get(user) == null) {
                    influencers.add(user);
                }
                else if (this.followers.get(user).size() > this.following.get(user).size()) {
                    influencers.add(user);
                }
            }
        }
        return influencers;
    }

    /*  Ritorna la lista (senza duplicati) degli utenti nella mappa passata come parametro
        che sono seguiti da più di likeTreshold utenti.
    
    @requires:  followers != null
    @throws:    Se followers == null solleva NullPointerException
    @effects:   Ritorna [i : i ∊ followers.keySet() && #{followers.get(i)} > likeTreshold]
    */
    public static List<String> influencers(Map<String, Set<String>> followers) {
        if (followers == null)
            throw new NullPointerException();
        List<String> influencers = new ArrayList<String>();
        for (String user : followers.keySet()) {
            if (!influencers.contains(user) && followers.get(user).size() > likeTreshold) {
                influencers.add(user);
            }
        }
        return influencers;
    }

    /*
    Chiamo la funzione con parametro per determinare la lista di utenti
    che sono autori di almeno un post in this
    
    @requires:  true
    @effects:   ritorna
                {this.posts.get(i).getAuthor() : 0 <= i < this.posts.size()}
    */
    public Set<String> getMentionedUsers() {
        return getMentionedUsers(this.posts);
    }

    /*
    @requires:  ps != null
    @throws:    Se ps == null solleva NullPointerException
    @effects:   Ritorna {ps.get(i).getAuthor() : 0 <= i < ps.size()}
    */
    public Set<String> getMentionedUsers(List<Post> ps) throws NullPointerException {
        if (ps == null)
            throw new NullPointerException();
        Set<String> mentioned = new HashSet<String>();
        for (Post p : ps) {
            // Dato che è un Set non ho problemi di duplicati
            mentioned.add(p.getAuthor());
        }
        return mentioned;
    }

    /*
    Usa la funzione con parametro per ritornare la lista di post scritti da username
    che sono presenti in this
    
    @requires:  username != null
    @throws:    Se username == null solleva NullPointerException
    @effects:   ritorna
                [this.posts.get(i) :
                     0 <= i < this.posts.length()
                     && this.posts.get(i).getAuthor().equals(username)
                ]
    */
    public List<Post> writtenBy(String username) throws NullPointerException {
        if (username == null)
            throw new NullPointerException();
        return writtenBy(this.posts, username);
    }

    /*
    @requires:  username != null && ps != null
    @throws:    Se username == null o ps == null solleva NullPointerException
    @effects:   Ritorna [ps.get(i) : 
                            0 <= i < ps.length()
                            && ps.get(i).getAuthor().equals(username)
                        ]
    */
    public List<Post> writtenBy(List<Post> ps, String username) throws NullPointerException {
        if (username == null || ps == null)
            throw new NullPointerException();
        List<Post> postedBy = new ArrayList<Post>();
        for (Post p : ps) {
            if (p.getAuthor().equals(username)) {
                postedBy.add(p);
            }
        }
        return postedBy;
    }

    /*
    @requires:  words != null
                && ∀ i. 0 <= i < words.size()
                    words.get(i) != null
    @throws:    Se words == null
                oppure se ∃ i. 0 <= i < words.size() && words.get(i) == null
                solleva NullPointerException
    @effects:   Ritorna la lista (senza duplicati) di post ∊ this.posts il cui testo
                contiene almeno una parola tra quelle della lista words, ovvero
                
                {this.posts.get(k) : 
                    0 <= k < this.posts.size() 
                    && this.posts.get(k) ∊ 
                        {this.posts.get(i) :
                            ∃ (i, j).
                                0 <= i < this.posts.size() 
                                && 0 <= j < words.size()
                                && this.posts.get(i).getText().contains(words.get(j))
                        }
                }
                (il fatto di aver considerato un insieme è solo per non ammettere 
                formalmente i duplicati: in realtà ritorna una lista come da specifica)
            
    */
    public List<Post> containing(List<String> words) throws NullPointerException {
        if (words == null)
            throw new NullPointerException();
        List<Post> hasSome = new ArrayList<Post>();
        for (String s : words) {
            if (s == null)
                throw new NullPointerException();
            for (Post p : this.posts) {
                if (!hasSome.contains(p) && p.getText().contains(s)) {
                    hasSome.add(p);
                }
            }
        }
        return hasSome;
    }

    /* ritorna una copia della lista di post
    @requires:  true
    @effects:   [this.posts.get(i) : 0 <= i < this.posts.size()]
    */
    public List<Post> getPosts() {
        return new ArrayList<Post>(this.posts);
    }

    /*  sovrascrivo il metodo equals per confrontare istanze di SocialNetwork
        Se other == null ritorna false
    
    @requires:  true
    @effects:   Ritorna il risultato (boolean) della valuazione di
    
                (other == null)
                && (∀ (i, j). i ∊ this.following.keySet() && i ∊ other.following.keySet()
                        && j ∊ this.followers.keySet() && j ∊ other.followers.keySet()
                    )
                && (∀ i. i ∊ this.following.keySet()
                    && this.following.get(i).equals(other.following.get(i))
                    && this.followers.get(i).equals(other.followers.get(i))
                    )
                && this.posts.equals(other.getPosts())
    */
    public boolean equals(SocialNetwork other) {
        // ovviamente deve essere vero che this != null
        if (other == null)
            return false;
        // mi creo copie delle mappe e dei post tramite gli appositi metodi
        List<Post> otherPosts = other.getPosts();
        Map<String, Set<String>> otherFollowing = other.guessFollowers(otherPosts);
        Map<String, Set<String>> otherFollowers = other.guessFollowing(otherPosts);

        // devono essere presenti gli stessi post
        if (!this.posts.equals(otherPosts))
            return false;

        // le mappe devono corrispondere sia nel dominio che nel codominio
        for (String liker : this.following.keySet()) {
            if (!otherFollowing.containsKey(liker) || !this.following.get(liker).equals(otherFollowing.get(liker))) {
                return false;
            }
        }
        for (String liked : this.followers.keySet()) {
            if (!otherFollowers.containsKey(liked) || !this.followers.get(liked).equals(otherFollowers.get(liked))) {
                return false;
            }
        }
        // tutto ok => uguali
        return true;
    }

    /*  
    Sovrascrivo il metodo toString
    
    @requires:  true
    @effects:   Ritorna una String contenente la rappresentazione di this 
                    in accordo con la AF
    */
    public String toString() {
        String s = new String();

        s += "(\n[\n";
        for (Post p : this.posts) {
            s += "\t" + p.toString();
        }
        s += "],\n";
        for (String user : this.followers.keySet()) {
            if (!this.followers.get(user).isEmpty())
                s += "followers(" + user + ") = " + this.followers.get(user).toString() + "\n";
        }
        s += ",\n";
        for (String user : this.following.keySet()) {
            if (!this.following.get(user).isEmpty())
                s += "following(" + user + ") = " + this.following.get(user).toString() + "\n";
        }
        s += ")\n";

        return s;
    }
};
