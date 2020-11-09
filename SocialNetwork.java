import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SocialNetwork {
    /*
    @overview:  SocialNetwork è una funzione parziale mutabile sia nel dominio che nel codominio
    elemento tipico: f: K -> {V_1, ..., V_n}
    
    Funzione di astrazione
    */

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
            myFollowers.get(post.getAuthor()).addAll(post.getLikesSet());
        } else {
            // l'autore non era presente nella mappa, allora devo inserire una nuova chiave con il relativo valore
            myFollowers.put(post.getAuthor(), post.getLikesSet());
        }
    }

    private void updateFollowing(Post post, Map<String, Set<String>> iFollow) throws NullPointerException {
        if (post == null)
            throw new NullPointerException();

        /** Aggiorno la mappa degli autori seguiti da chi ha messo like al post */
        Set<String> helper = new HashSet<String>(1);
        // esamino chi ha messo like al post
        for (String s : post.getLikesSet()) {
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
    public void addPost(Post p) {
        if (p == null)
            throw new NullPointerException();

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
        Set<String> hasOtherLikes = new TreeSet<String>();
        // Seleziono, tra i post rimanenti, quelli dello stesso autore del post
        for (Post element : this.posts) {
            if (element.getAuthor().equals(post.getAuthor())) {
                for (String liker : element.getLikesSet()) {
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

    public Set<String> getMentionedUsers() {
        return getMentionedUsers(this.posts);
    }

    public Set<String> getMentionedUsers(List<Post> ps) {
        Set<String> mentioned = new HashSet<String>();
        for (Post p : ps) {
            // Dato che è un Set, non ho problemi di duplicati
            mentioned.add(p.getAuthor());
        }
        return mentioned;
    }

    public List<Post> writtenBy(String username) {
        return writtenBy(this.posts, username);
    }

    public List<Post> writtenBy(List<Post> ps, String username) {
        List<Post> postedBy = new ArrayList<Post>();
        for (Post p : ps) {
            if (p.getAuthor().equals(username)) {
                postedBy.add(p);
            }
        }
        return postedBy;
    }

    public List<Post> getPosts() {
        return new ArrayList<Post>(this.posts);
    }

    public List<Post> containing(List<String> words) {
        List<Post> hasSome = new ArrayList<Post>();
        for (String s : words) {
            for (Post p : this.posts) {
                if (!hasSome.contains(p) && p.getText().contains(s)) {
                    hasSome.add(p);
                }
            }
        }
        return hasSome;
    }

    public boolean equals(SocialNetwork net2) {
        List<Post> net2Posts = net2.getPosts();
        Map<String, Set<String>> net2Following = net2.guessFollowers(net2Posts);
        Map<String, Set<String>> net2Followers = net2.guessFollowing(net2Posts);

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
                    || this.following.get(liked).equals(net2Followers.get(liked)) == false) {
                return false;
            }
        }
        // tutto ok => uguali
        return true;
    }

    public String toString() {
        String s = new String();
        return s;
    }
};
