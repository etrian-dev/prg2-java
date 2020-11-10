import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SocialNetwork {
    /*
    @overview:  SocialNetwork è una coppia di funzioni parziali mutabili sia nel dominio che nel codominio
    elemento tipico:    followers: user -> {user_1, ..., user_n}
                        following: user -> {user*_1, ..., user*_n}
    
    Funzione di astrazione
    AF(x) = followers: x.followers.keySet() -> x.followers.get(i)
            following: x.followers.keySet() -> x.followers.get(i)
    */

    /* variabili d'istanza (private) */
    private Map<String, Set<String>> followers;
    private Map<String, Set<String>> following;
    private List<Post> posts;

    /*
    @requires:  true
    @effects:   Crea una rete sociale vuota
    */
    public SocialNetwork() {
        this.followers = new HashMap<String, Set<String>>();
        this.following = new HashMap<String, Set<String>>();
        this.posts = new ArrayList<Post>();
    }

    public SocialNetwork(List<Post> ps) {
        this.followers = new HashMap<String, Set<String>>();
        this.following = new HashMap<String, Set<String>>();
        this.posts = new ArrayList<Post>();

        for (Post p : ps) {
            // aggiorna le mappe con i dati di p
            updateFollowers(p, this.followers);
            updateFollowing(p, this.following);
            this.posts.add(p); // aggiunge p alla lista di post
        }
    }

    private void updateFollowers(Post post, Map<String, Set<String>> myFollowers) throws NullPointerException {
        if (post == null)
            throw new NullPointerException();

        /** Aggiorno la mappa dei follower dell'autore del post */
        if (myFollowers.get(post.getAuthor()) != null) {
            // aggiungo al set quelli del post passato come parametro
            myFollowers.get(post.getAuthor()).addAll(post.getLikes());
        } else {
            // l'autore non era presente nella mappa, allora devo inserire una nuova chiave con il relativo valore
            myFollowers.put(post.getAuthor(), post.getLikes());
        }
    }

    private void updateFollowing(Post post, Map<String, Set<String>> iFollow) throws NullPointerException {
        if (post == null)
            throw new NullPointerException();

        /** Aggiorno la mappa degli autori seguiti da chi ha messo like al post */
        Set<String> helper = new HashSet<String>(1);
        // esamino chi ha messo like al post
        for (String s : post.getLikes()) {
            if (iFollow.get(s) != null) {
                // se s è già presente nella mappa, aggiorna il set di seguiti da s
                iFollow.get(s).add(post.getAuthor());
            } else {
                // altrimenti estende la mappa con la coppia chiave-valore
                // (s, {post.getAuthor}) usando un set di supporto
                helper.add(post.getAuthor());
                iFollow.put(s, new HashSet<String>(helper));
                helper.clear();
            }
        }
    }

    /* metodi modificatori per aggiungere e rimuovere Post dalla rete */
    public void addPost(Post p) throws NullPointerException, DuplicatePostException {
        if (p == null)
            throw new NullPointerException();
        for (Post el : this.posts) {
            if (el.getId().equals(p.getId()))
                throw new DuplicatePostException(p.getId());
        }
        // aggiungo alla lista
        this.posts.add(p);
        // aggiorno le mappe
        updateFollowers(p, this.followers);
        updateFollowing(p, this.following);
    }

    public void rmPost(Post p) throws NullPointerException, NoSuchPostException {
        if (p == null)
            throw new NullPointerException();
        if (this.posts.contains(p) == false) {
            throw new NoSuchPostException();
        }
        // rimuovo dalla lista
        this.posts.remove(p);
        // e aggiorno le mappe
        this.rmFromMaps(p);
    }

    /** quando rimuovo un post devo aggiornare entrambe le mappe */
    /** Se l'autore del post aveva solo questo post nella mappa, devo toglierlo dai seguiti di tutti i like del post
        Altrimenti devo toglierlo soltanto da quegli utenti che avevano messo like solo a quel post dell'autore */
    private void rmFromMaps(Post post) {
        boolean noMorePosts = true;
        // hashmap con coppie (utente, se ha messo like all'autore del post)
        Set<String> hasOtherLikes = new HashSet<String>();
        // Seleziono, tra i post rimanenti, quelli dello stesso autore del post
        for (Post element : this.posts) {
            if (element.getAuthor().equals(post.getAuthor())) {
                for (String liker : element.getLikes()) {
                    hasOtherLikes.add(liker);
                }
                noMorePosts = false; // ho trovato almeno un altro post con lo stesso autore
            }
        }
        // rimuovo da chi non è nel set ricavato l'associazione nei following all'autore del post
        // e anche l'autore dalla mappa dei followers se non ha altri post
        for (String user : this.following.keySet()) {
            // se l'utente user è stato inserito, allora ha almeno un altro like ad author
            if (!hasOtherLikes.contains(user)) {
                this.following.get(user).remove(post.getAuthor());
            }
        }
        if (noMorePosts) {
            this.following.remove(post.getAuthor());
            this.followers.remove(post.getAuthor());
        }
    }

    public Map<String, Set<String>> guessFollowers(List<Post> ps) {
        // ritorna la mappa che associa ad ogni utente gli utenti seguiti, limitatamente a quelli che compaiono tra i like dei post
        Map<String, Set<String>> networkOnList = new HashMap<String, Set<String>>();
        for (Post p : ps) {
            updateFollowing(p, networkOnList);
        }
        return networkOnList;
    }

    public Map<String, Set<String>> guessFollowing(List<Post> ps) {
        //ritorna la mappa che associa ad ogni utente gli utenti che lo seguono, limitatamente a quelli che compaiono tra i like dei post
        Map<String, Set<String>> networkOnList = new HashMap<String, Set<String>>();
        for (Post p : ps) {
            updateFollowers(p, networkOnList);
        }
        return networkOnList;
    }

    /** 
        Ritorna la lista di utenti che nella rete sociale (passata come parametro)
        hanno più follower di following
    */
    public List<String> influencers(Map<String, Set<String>> followers) {
        List<String> influencers = new ArrayList<String>();

        // per ognuno di essi conto se hanno più followers o following
        for (String user : followers.keySet()) {
            int countFollowed = followers.get(user).size();
            int countFollower = 0;
            for (Set<String> setLikes : followers.values()) {
                if (setLikes.contains(user)) {
                    countFollower++;
                }
            }
            if (countFollower > countFollowed) {
                influencers.add(user);
            }
        }
        return influencers;
    }

    /*
    @requires:  true
    @effects:   ritorna
                {this.posts.get(i).getAuthor() : 0 <= i < this.posts.length()}
    */
    public Set<String> getMentionedUsers() {
        return getMentionedUsers(this.posts);
    }

    /*
    @requires:  ps != null
    @throws:    NullPointerException
    @effects:   Se ps == null solleva NullPointerException, altrimenti ritorna
                {this.posts.get(i).getAuthor() : 0 <= i < this.posts.length()}
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
    @requires:  username != null
    @throws:    NullPointerException
    @effects:   Se username == null solleva NullPointerException, altrimenti
                ritorna
                [this.posts.get(i) : 0 <= i < this.posts.length() && this.posts.get(i).getAuthor().equals(username)]
    */
    public List<Post> writtenBy(String username) throws NullPointerException {
        if (username == null)
            throw new NullPointerException();
        return writtenBy(this.posts, username);
    }

    /*
    @requires:  username != null && ps != null
    @throws:    NullPointerException
    @effects:   Se (username == null || ps == null) solleva NullPointerException,       
                altrimenti ritorna
                [ps.get(i) : 0 <= i < ps.length() && ps.get(i).getAuthor().equals(username)]
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
                && ∀ i. 0 <= i < words.length()
                    words.get(i) != null
    @throws:    NullPointerException
    @effects:   Se words == null
                oppure se ∃ i. 0 <= i < words.length() && words.get(i) == null
                solleva NullPointerException.
                Altrimenti ritorna la lista (senza duplicati) di post ∊ this.posts il cui testo contiene almeno una parola tra quelle della lista words, ovvero
        
                [this.posts.get(k) : 
                    0 <= k < this.posts.length() 
                    && this.posts.get(k) ∊ 
                        {
                        this.posts.get(i) :
                            ∃ (i, j).
                                0 <= i < this.posts.length() 
                                && 0 <= j < words.length()
                                && this.posts.get(i).getText().contains(words.get(j))
                        }
                ]
            
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

    /* metodo per ritornare una copia dei post presenti nella rete (per esigenze di testing) */
    public List<Post> getPosts() {
        return new ArrayList<Post>(this.posts);
    }

    /* sovrascrivo il metodo equals per confrontare istanze di SocialNetwork */
    /*
    @requires:  other != null
    @throws:    NullPointerException
    @effects:   Se other == null solleva NullPointerException, altrimenti
                ritorna il risultato (boolean) della valuazione di
    
                ∀ (i, j). i ∊ this.following.keySet() && i ∊ other.following.keySet()
                    && j ∊ this.followers.keySet() && j ∊ other.followers.keySet()
                && ∀ i. i ∊ this.following.keySet()
                    && this.following.get(i) == other.following.get(i)
                    && this.followers.get(i) == other.followers.get(i)
                && this.posts.equals(other.getPosts())
    */
    public boolean equals(SocialNetwork other) {
        List<Post> net2Posts = other.getPosts();
        Map<String, Set<String>> net2Following = other.guessFollowers(net2Posts);
        Map<String, Set<String>> net2Followers = other.guessFollowing(net2Posts);

        // devono essere presenti gli stessi post
        if (this.posts.equals(net2Posts) == false)
            return false;

        // la mappa dei following deve avere le stesse chiavi associate agli stessi Set
        for (String liker : this.following.keySet()) {
            if (net2Following.containsKey(liker) == false
                    || this.following.get(liker).equals(net2Following.get(liker)) == false) {
                return false;
            }
        }
        // la mappa dei followers deve avere le stesse chiavi associate agli stessi Set
        for (String liked : this.followers.keySet()) {
            if (net2Followers.containsKey(liked) == false
                    || this.followers.get(liked).equals(net2Followers.get(liked)) == false) {
                return false;
            }
        }
        // tutto ok => uguali
        return true;
    }

    /*
        sovrascrivo il metodo toString che ritorna una String contenente la
        rappresentazione di this in accordo con la AF
    */
    public String toString() {
        String s = new String();

        s += "(\n[\n";
        for (Post p : this.posts) {
            s += "\t" + p.toString();
        }
        s += "],\n";
        for (String user : this.followers.keySet()) {
            s += "followers(" + user + ") = " + this.followers.get(user) + "\n";
        }
        s += ",\n";
        for (String user : this.following.keySet()) {
            s += "following(" + user + ") = " + this.following.get(user) + "\n";
        }
        s += ")\n";

        return s;
    }
};
