import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

class ModeratedSocialNetwork extends SocialNetwork {
    private Set<Integer> offensivePosts;
    private static Hashtable<Integer, String> badwords = new Hashtable<Integer, String>();

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

    /* costruttore rete sociale moderata vuota */
    public ModeratedSocialNetwork() {
        super();
        this.offensivePosts = new HashSet<Integer>();
    }

    /* costruttore rete sociale moderata con lista di post */
    public ModeratedSocialNetwork(List<Post> pList) {
        super(pList);
        this.offensivePosts = new HashSet<Integer>();
        for (Post p : pList) {
            for (String w : p.getText().split(" ")) {
                if (badwords.get(w.hashCode()) != null) {
                    offensivePosts.add(p.getId());
                }
            }
        }
    }

    // sovrascrivo addPost()
    public void addPost(Post p) throws DuplicatePostException, NullPointerException {
        super.addPost(p); // prima aggiungo il post alla rete
        for (String w : p.getText().split(" ")) {
            if (badwords.get(w.hashCode()) != null) {
                offensivePosts.add(p.getId());
            }
        }
    }

    // sovrascrivo rmPost()
    public void rmPost(Integer pid) throws NoSuchPostException, NullPointerException {
        super.rmPost(pid); // prima rimuovo il post dalla rete
        if (this.offensivePosts.contains(pid)) {
            // lo rimuovo dai segnalati
            this.offensivePosts.remove(pid);
        }
    }

    // ritorna il set di post segnalati
    public Set<Integer> getOffensive() {
        return new HashSet<Integer>(this.offensivePosts);
    }

    public static HashSet<String> getBadwords() {
        return new HashSet<String>(badwords.values());
    }
};