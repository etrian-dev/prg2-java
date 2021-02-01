import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SocialNetwork {
    /*
    @overview:  SocialNetwork è un tipo di dato astratto modificabile definito una tripla:
                due funzioni parziali mutabili sia nel dominio che nel codominio ed
                una lista (di Post)
    elemento tipico:    (
                            followers: {users} → {{follower_1}, ... , {follower_n}},
                            following: {users} → {{following_1}, ... , {following_n}},
                            posts
                        )
                        dove posts = [post_1, ..., post_n]
    */

    /* variabili d'istanza (private) */
    // associa ad ogni utente il Set degli utenti che segue
    private Map<String, Set<String>> following;
    // associa ad ogni utente il Set degli utenti che lo seguono
    private Map<String, Set<String>> followers;
    // lista dei post presenti nella rete
    private List<Post> posts;

    /*
    Funzione di astrazione
    AF(x) = (
                followers:
                x.followers.keySet() → {x.followers.get(i) : i ∊ x.followers.keySet()}
                ,
                following:
                x.following.keySet() → {x.following.get(i) : i ∊ following.keySet()}
                ,
                x.posts
            )
    Invariante di rappresentazione
    IR(x) = x != null
            && x.posts != null
            && x.following != null
            && x.followers != null
            && ∀ i.
                0 <= i < x.posts.size()
                && IR_Post(x.posts.get(i)) == true      (ogni post nel social deve rispettare l'IR di Post)
            && x.following.keySet() =  {x.posts.get(j).getLikes() :
                                            0 <= j < x.posts.size()
                                        }
            && ∀ i. i ∊ x.following.keySet()
                    && x.following.get(i) =
                        {x.posts.get(k).getAuthor() :
                            0 <= k < x.posts.size()
                            && x.posts.get(k).getLikes().contains(i)
                        }
            && x.followers.keySet() =  {x.posts.get(j).getAuthor() :
                                            0 <= j < x.posts.size()
                                            && x.posts.get(j).getLikes() != ∅
                                        }
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
                    followers: ∅ → ∅,
                    following: ∅ → ∅,
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
                la tripla formata dalla lista di post ps e le mappe opportunamente
                generate:

                (
                    followers:
                    ∀ j ∊ {ps.get(i).getAuthor() : 0 <= i < ps.size()}
                    inserisco
                    j → {ps.get(k).getLikes() : 0 <= k < ps.size() && ps.get(k).getAuthor().equals(j)}
                    ,
                    following:
                    ∀ j ∊ {ps.get(i).getLikes() : 0 <= i < ps.size()}
                    inserisco
                    j → {ps.get(k).getAuthor() : 0 <= k < ps.size() && ps.get(k).getLikes().contains(j)}
                    ,
                    ps
                )
    */
    public SocialNetwork(List<Post> ps) throws NullPointerException {
        if (ps == null)
            throw new NullPointerException();
        this.posts = new ArrayList<Post>(ps);
        // prima creo delle mappe vuote
        this.following = new HashMap<String, Set<String>>();
        this.followers = new HashMap<String, Set<String>>();
        // Poi aggiorno le mappe sulla base dei post in ps
        for (Post p : ps) {
            updateFollowers(p, this.followers);
            updateFollowing(p, this.following);
        }
    }

    /*
    Aggiorno la mappa di chi segue l'autore del post.
    Modifica la mappa followAuth, che non è necessariamente this

    @requires:  post != null && followAuth != null
    @throws:    Se post == null o followAuth == null solleva NullPointerException
    @modifies:  followAuth
    @effects:   Chiamo j = post.getAuthor()
                Se {followAuth.get(j)} != ∅     (cioè la chiave j appartiene alla mappa)
                    allora followAuth.get(j) → {followAuth.get(j)} U post.getLikes()
                Altrimenti aggiungo l'associazione chiave → valore
                    j → {post.getLikes()}
    */
    private void updateFollowers(Post post, Map<String, Set<String>> followAuth) throws NullPointerException {
        if (post == null || followAuth == null)
            throw new NullPointerException();
        if (followAuth.get(post.getAuthor()) != null) {
            // merge dei set di like se esiste già la chiave autore
            followAuth.get(post.getAuthor()).addAll(post.getLikes());
        } else {
            // l'autore non era presente nella mappa, allora devo inserire la coppia chiave → valore
            Set<String> likes = new HashSet<String>();
            for (String like : post.getLikes()) {
                likes.add(new String(like));
            }
            followAuth.put(post.getAuthor(), likes);
        }
    }

    /*
    Aggiorno la mappa degli utenti seguiti da quegli utenti che hanno messo
    like ad almeno un post che fa parte nella rete.
    Modifica la mappa iFollow, che non è necessariamente this

    @requires:  post != null && iFollow != null
    @throws:    Se post == null o iFollow == null solleva NullPointerException
    @modifies:  iFollow
    @effects:   ∀ j. j ∊ post.getLikes()
                    Se {iFollow.get(j)} != ∅
                        iFollow.get(j) → {iFollow.get(j)} U post.getAuthor()
                    Altrimenti aggiungo l'associazione chiave → valore
                        j → {post.getAuhtor()}
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
    Devo togliere l'autore del post rimosso dai seguiti degli utenti se essi hanno messo like a tale autore soltanto nel post rimosso e togliere di conseguenza l'utente
    dai followers dell'autore
    Sfrutto inoltre il fatto di aver già tolto p da this.posts, altrimenti dovrei controllare l'id dei post.

    @requires:  p != null
    @throws:    Se p == null allora solleva NullPointerException
    @modifies:  this
    @effects:   ∀ k. k ∊ following.keySet()
                    Se k ∉ {posts.get(i).getLikes().get(j) :
                                ∀ (i, j).
                                0 <= j < posts.getLikes().size()
                                && 0 <= i < posts.size()
                                && posts.get(i).getAuthor().equals(p.getAuthor())
                            }
                    allora k → following.get(k) - {p.getAuthor()}
                    e k → followers.get(p.getAuthor()) - {k}
    */
    private void rmFromMaps(Post p) throws NullPointerException {
        if (p == null)
            throw new NullPointerException();
        Set<String> hasOtherLikes = new HashSet<String>();
        // Seleziono, tra i post rimanenti, quelli dello stesso autore di p
        for (Post element : this.posts) {
            if (element.getAuthor().equals(p.getAuthor())) {
                // ∃ post ∊ this.posts : post.getAuthor().equals(p.getAuthor())
                // unisco gli utenti che hanno messo like al set di utenti da escludere
                // dalla rimozione della relazione di following
                hasOtherLikes.addAll(element.getLikes());
            }
        }
        for (String user : this.following.keySet()) {
            /*  la seconda condizione in and è presente (non indispensabile) perché,
                se user non seguiva l'autore prima della rimozione del post,
                so già che non lo segue neanche dopo. Perciò posso evitare di eseguire
                un'azione che non apporterà nessuna modifica
            */
            if (!hasOtherLikes.contains(user) && this.following.get(user).contains(p.getAuthor())) {
                this.following.get(user).remove(p.getAuthor());
                this.followers.get(p.getAuthor()).remove(user);
            }
        }
    }

    /** metodo per aggiungere post alla rete sociale */
    /*
    @requires:  p != null
                && (∀ i.
                    0 <= i < posts.size()
                    && !p.getId().equals(posts.get(i).getId())
                    )
    @throws:    Se p == null solleva NullPointerException
                Se (∃ i.
                    0 <= i < posts.size()
                    && p.getId().equals(posts.get(i).getId())
                    )
                allora solleva DuplicatePostException
    @modifies:  this
    @effects:   Esegue posts = posts U [p] e poi aggiorna le mappe chiamando le
                funzioni definite sopra con parametro rispettivamente followers e
                following relativi a  questa istanza
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
    @requires:  pid != null
                && (∃ i.
                    0 <= i < posts.size()
                    && posts.get(i).getId().equals(pid))
    @throws:    Se pid == null solleva NullPointerException
                Se (∀ i.
                    0 <= i < posts.size()
                    && !posts.get(i).getId().equals(pid))
                solleva NoSuchPostException
    @modifies:  this
    @effects:   Se (∃ p.
                    p ∊ this.posts
                    && p.getId().equals(pid))
                esegue posts = posts / {p}
                e poi aggiorna le mappe tramite la funzione rmFromMaps(p)
    */
    public void rmPost(Integer pid) throws NullPointerException, NoSuchPostException {
        if (pid == null)
            throw new NullPointerException();
        boolean nopost = true;
        Post toRemove = null;
        for (Post p : this.posts) {
            if (p.getId().equals(pid)) {
                nopost = false;
                toRemove = p;
            }
        }
        if (nopost)
            throw new NoSuchPostException();
        // rimuovo dalla lista
        this.posts.remove(toRemove);
        // e aggiorno le mappe
        this.rmFromMaps(toRemove);
    }

    /*
    Modifica il post con id passato come parametro aggiungendo il like, 
    anch'esso passato come parametro, modificando di conseguenza le mappe
    
    @requires:  pid != null && liker != null
    @throws:    Se pid == null o liker == null solleva NullPointerException
                Se 
                (∀i. 
                    0 <= i < this.posts.size() 
                    && !(this.posts.get(i).getId().equals(pid))
                )
                solleva NoSuchPostException
                Se 
                (∃i. 
                    0 <= i < this.posts.size() 
                    && this.posts.get(k).getId().equals(pid) 
                    && this.posts.get(k).getAuthor().equals(liker)
                )
                allora solleva SelfLikeException
    @effects:   Se 
                (∃i. 
                    0 <= i < this.posts.size() 
                    && this.posts.get(k).getId().equals(pid) 
                    && !(this.posts.get(k).getAuthor().equals(liker))
                )
                allora esegue this.posts.get(k).addLike() ed aggiorna 
                this.followers e this.following di conseguenza
    */
    public void likePost(Integer pid, String liker)
            throws SelfLikeException, NoSuchPostException, NullPointerException {
        if (pid == null || liker == null) {
            throw new NullPointerException();
        }
        // cerca post nella lista
        boolean found = false;
        for (Post ps : this.posts) {
            // cerca il post nella lista
            if (ps.getId().equals(pid)) {
                try {
                    // tenta di aggiungere il like (potrebbe sollevare eccezioni)
                    ps.addLike(liker);
                    // aggiorno le mappe
                    updateFollowers(ps, this.followers);
                    updateFollowing(ps, this.following);
                } catch (SelfLikeException reject_like) {
                    // gestisco la SelfLikeException dettagliandola e rilanciandola
                    throw new SelfLikeException(liker + " Ha provato a mettere like al proprio post", reject_like);
                } finally {
                    // in ogni caso se trovo il post setto la flag
                    found = true;
                }
            }
        }
        // post non trovato: non posso modificarlo
        if (!found) {
            throw new NoSuchPostException();
        }
    }

    // semantica analoga alla precendente, ma prende un post come argomento
    public void likePost(Post p, String liker) throws SelfLikeException, NoSuchPostException, NullPointerException {
        if (p == null || liker == null) {
            throw new NullPointerException();
        }
        // chiama semplicemente la versione che usa l'id del post
        this.likePost(p.getId(), liker);
    }

    /*
    Ritorna sotto forma di mappa <String, Set<String>> la funzione che associa ad
    ogni utente, nella rete indotta da ps, il Set di utenti che segue

    @requires:  ps != null
                && (∀ i. 0 <= i < ps.size() && ps.get(i) != null)
    @throws:    Se ps == null oppure
                (∃ i. 0 <= i < ps.size() && ps.get(i) == null)
                allora solleva NullPointerException
    @effects:   Ritorna la rete sociale derivata da ps, ovvero la
                funzione che
                ∀ i. i ∊ {ps.get(j).getLikes() : 0 <= j < ps.size()}
                mappa
                i
                →
                {ps.get(k).getAuthor() : 0 <= k < ps.size() && ps.get(k).getLikes().contains(i)}
    */
    public Map<String, Set<String>> guessFollowers(List<Post> ps) throws NullPointerException {
        if (ps == null)
            throw new NullPointerException();
        // parto da una mappa vuota e la aggiorno con ogni post della lista
        Map<String, Set<String>> m = new HashMap<String, Set<String>>();
        for (Post p : ps) {
            updateFollowing(p, m);
        }
        return m;
    }

    /*
    Ritorna sotto forma di mappa <String, Set<String>> la funzione che associa ad
    ogni utente, nella rete indotta da ps, il Set di utenti che segue

    @requires:  ps != null
                && (∀ i. 0 <= i < ps.size() && ps.get(i) != null)
    @throws:    Se ps == null oppure
                (∃ i. 0 <= i < ps.size() && ps.get(i) == null)
                allora solleva NullPointerException
    @effects:   Ritorna la rete sociale derivata da ps, ovvero la
                funzione che
                ∀ i. i ∊ {ps.get(j).getAuthor() : 0 <= j < ps.size()}
                mappa
                i
                →
                {ps.get(k).getLikes() : 0 <= k < ps.size() && ps.get(k).getAuthor().equals(i)}
    */
    public Map<String, Set<String>> guessFollowing(List<Post> ps) throws NullPointerException {
        if (ps == null)
            throw new NullPointerException();
        Map<String, Set<String>> m = new HashMap<String, Set<String>>();
        for (Post p : ps) {
            updateFollowers(p, m);
        }
        return m;
    }

    /*  Ritorna la lista (senza duplicati) degli utenti in this che sono seguiti da più
        persone di quante ne seguano.
        (posso limitarmi a considerare le chiavi della mappa followers in quanto un
        utente vi compare ha ricevuto almeno un like e quindi quelli esclusi non posso
        essere influencers).

    @requires:  true
    @effects:   Ritorna [i :
                            i ∊ followers.keySet()
                            && #{followers.get(i)} > #{following.get(i)}
                        ]
    */
    public List<String> influencers() {
        List<String> influencers = new ArrayList<String>();
        for (String user : this.followers.keySet()) {
            if (!(influencers.contains(user) || this.followers.get(user).isEmpty())) {
                if (this.following.get(user) == null
                        || this.followers.get(user).size() > this.following.get(user).size()) {
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
    public static List<String> influencers(Map<String, Set<String>> followers, int treshold) {
        if (followers == null)
            throw new NullPointerException();
        List<String> influencers = new ArrayList<String>();
        for (String user : followers.keySet()) {
            if (!influencers.contains(user) && followers.get(user).size() > treshold) {
                influencers.add(user);
            }
        }
        return influencers;
    }

    /*
    Chiamo la funzione con parametro per determinare il Set di utenti
    che sono autori di almeno un post in this

    @requires:  true
    @effects:   ritorna
                {posts.get(i).getAuthor() : 0 <= i < posts.size()}
    */
    public Set<String> getMentionedUsers() {
        return getMentionedUsers(this.posts);
    }

    /*
    @requires:  ps != null
                && (∀ i. 0 <= i < ps.size() && ps.get(i) != null)
    @throws:    Se ps == null oppure
                (∃ i. 0 <= i < ps.size() && ps.get(i) == null)
                allora solleva NullPointerException
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
                [posts.get(i) :
                     0 <= i < posts.length()
                     && posts.get(i).getAuthor().equals(username)
                ]
    */
    public List<Post> writtenBy(String username) throws NullPointerException {
        if (username == null)
            throw new NullPointerException();
        return writtenBy(this.posts, username);
    }

    /*
    @requires:  username != null
                && ps != null
                && (∀ i. 0 <= i < ps.size() && ps.get(i) != null)
    @throws:    Se username == null
                || ps == null
                || (∃ i. 0 <= i < ps.size() && ps.get(i) == null)
                allora solleva NullPointerException
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
                && (∀ i. 0 <= i < words.size() && words.get(i) != null)
    @throws:    Se words == null
                || (∃ i. 0 <= i < words.size() && words.get(i) == null)
                allora solleva NullPointerException
    @effects:   Ritorna la lista (senza duplicati) di p ∊ posts il cui testo
                contiene almeno una parola tra quelle della lista words, ovvero

                {posts.get(k) :
                    0 <= k < posts.size()
                    && posts.get(k) ∊
                        {posts.get(i) :
                            ∃ (i, j).
                                0 <= i < posts.size()
                                && 0 <= j < words.size()
                                && posts.get(i).getText().contains(words.get(j))
                        }
                }

    Nota: Il fatto di aver considerato un insieme è solo per non ammettere
    formalmente i duplicati: in realtà ritorna una lista come da specifica
    @returns:   List<Post>
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

    /*
    Ritorna una copia della lista di post (non modificabile)

    @requires:  true
    @effects:   [posts.get(i) : 0 <= i < posts.size()]
    */
    public List<Post> getPosts() {
        return List.copyOf(this.posts);
    }

    /*  sovrascrivo il metodo equals per confrontare istanze di SocialNetwork
        Se other == null ritorna false come richiesto dalla specifica di equals

    @requires:  true
    @effects:   Ritorna il risultato della valuazione di

                (other == null)
                && (∀ (i, j). i ∊ following.keySet() && i ∊ other.following.keySet()
                        && j ∊ followers.keySet() && j ∊ other.followers.keySet()
                    )
                && (∀ i. i ∊ following.keySet()
                    && following.get(i).equals(other.following.get(i))
                    && followers.get(i).equals(other.followers.get(i))
                    )
                && posts.equals(other.getPosts())
    */
    public boolean equals(SocialNetwork other) {
        if (other == null)
            return false;
        // mi creo copie delle mappe e dei post tramite gli appositi metodi
        List<Post> otherPosts = other.getPosts();
        /**
        L'apparente incongruenza dei nomi delle variabili deriva dal fatto che
        guessFollowing ritorna la mappa {utente} → {utenti che lo seguono}
        e guessFollowers ritorna la mappa {utente} → {utenti che segue}
        */
        Map<String, Set<String>> otherFollowers = other.guessFollowing(otherPosts);
        Map<String, Set<String>> otherFollowing = other.guessFollowers(otherPosts);

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
        // tutto ok → true
        return true;
    }

    /*
    Sovrascrivo il metodo toString

    @requires:  true
    @effects:   Ritorna la tripla (followers: K → V, following: K → V, posts) che
                rappresenta questa istanza di SocialNetwork, come specificato dalla AF
    */
    public String toString() {
        String s = new String();

        s += "(\n[\n";
        for (Post p : this.posts) {
            s += "\t" + p.toString();
        }
        s += "]\n,\n";
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
