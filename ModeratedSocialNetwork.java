import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

class ModeratedSocialNetwork extends SocialNetwork {
    private Set<Integer> offensivePosts;
    private static Hashtable<Integer, String> badwords = new Hashtable<>();

    /* costruttore rete sociale vuota */
    public ModeratedSocialNetwork() {
        super();
        this.offensivePosts = new HashSet<>();
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

    /* costruttore rete sociale con lista di post */
    public ModeratedSocialNetwork(List<Post> pList) {
        super(pList);
        this.offensivePosts = new HashSet<>();
        Scanner s = null;
        try {
            File f = new File("badwords.txt");
            s = new Scanner(f);
        } catch (Exception ex) {
            System.out.println("Caught:" + ex);
        }
        String word;
        while (s.hasNext()) {
            word = s.nextLine();
            badwords.put(word.hashCode(), word);
        }
        s.close();
        for (Post p : pList) {
            for (String bad : badwords.values())
                if (p.getText().indexOf(bad) != -1) {
                    offensivePosts.add(p.getId());
                }
        }
    }

    // sovrascrivo addPost()
    public void addPost(Post p) throws DuplicatePostException, NullPointerException {
        super.addPost(p); // prima aggiungo il post alla rete
        for (String bad : badwords.values()) {
            if (p.getText().indexOf(bad) != -1) {
                // se trovo una parola lo aggiungo ai segnalati
                this.offensivePosts.add(p.getId());
            }
        }
    }

    // sovrascrivo rmPost()
    public void rmPost(Post p) throws NoSuchPostException, NullPointerException {
        super.rmPost(p); // prima rimuovo il post dalla rete
        if (this.offensivePosts.contains(p.getId())) {
            // lo rimuovo dai segnalati
            this.offensivePosts.remove(p.getId());
        }
    }

    public Set<Integer> getOffensive() {
        return new HashSet<>(this.offensivePosts);
    }
};