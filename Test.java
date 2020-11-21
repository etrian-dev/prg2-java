
/* testa la classe Post */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

class Test {
    /* 
        Il main crea delle istanze di Post a partire da dati letti dal file
        fornito come argomento al programma, le stampa. Dopo aggiungo dei like e stampo di nuovo i post creati.
    */
    public static void main(String[] args) {
        SocialNetwork MicroBlog = new SocialNetwork();

        /* Creo uno Scanner che legge da stdin */
        Scanner s = new Scanner(System.in);

        /* leggo una lista di Post dal file */
        ArrayList<Post> pList = PostReader(s);

        int i;
        System.out.println("***lista di post***");
        /* stampa tutti i post letti */
        for (i = 0; i < pList.size(); i++) {
            System.out.print(pList.get(i).toString());
        }

        /*  verifico con un assert 
            l'unicita' degli id, 
            la lunghezza del testo 
            l'impossibilità di mettere like al proprio post    
        */
        int j;
        for (i = 0; i < pList.size(); i++) {
            for (j = i + 1; j < pList.size(); j++) {
                assert pList.get(i).getId() != pList.get(j).getId();
            }
            assert (pList.get(i).getLikes().contains(pList.get(i).getAuthor()) == false)
                    && (pList.get(i).getText().length() <= 140);
        }

        /* aggiungo dei like ad alcuni post (utenti che mettono like sono letti nel file di test) */
        System.out.println("***Test addLike()***");
        Random rng = new Random();
        String liker;
        if (pList.size() > 0) {
            while (!s.hasNext("fine")) {
                // scelgo a caso un indice della lista (quindi un post)
                j = rng.nextInt(pList.size());
                liker = s.nextLine();
                // uso il metodo addLike(), che potrebbe sollevare eccezioni
                try {
                    pList.get(j).addLike(liker);
                    System.out.println("aggiunto il like di " + liker + " al post con id = " + pList.get(j).getId());
                } catch (Exception ex) {
                    System.out.println("Caught: " + ex);
                }
            }
            s.skip("fine\n");
        }

        System.out.println("***lista di post + like***");
        /* stampa tutti i post inseriti con i relativi like e prova ad aggiungerli a MicroBlog*/
        for (Post p : pList) {
            System.out.print(p.toString());
            try {
                MicroBlog.addPost(p);
            } catch (Exception ex) {
                System.out.println("Caught: " + ex);
            }
        }

        /* stampo la rete sociale per testare toString() */
        System.out.println("***MicroBlog***\n" + MicroBlog.toString());

        /* creo una seconda rete vuota */
        SocialNetwork net2 = new SocialNetwork();

        /* se MicroBlog ha almeno un post allora sono diverse */
        assert !(MicroBlog.getPosts().size() > 0 && MicroBlog.equals(net2));

        /*  
            aggiungo a net2 tutti i post di pList tramite il metodo apposito
            che potrebbe lanciare eccezioni
         */
        System.out.println("***Test addPost()***");
        try {
            for (Post pp : pList) {
                net2.addPost(pp);
            }
        } catch (Exception ex) {
            System.out.println("caught: " + ex);
        }

        /* perciò ora devono risultare uguali */
        assert MicroBlog.equals(net2);

        /*  voglio provocare un conflitto di id in MicroBlog, 
            che deve sollevare DuplicatePostException */
        Date now = new Date();
        Post another = null;
        if (pList.size() > 0) {
            Integer conflictId = pList.get(0).getId();
            try {
                // uso il costruttore di Post con parametro id
                another = new Post(conflictId, "Autore1", "Prova1", now);
                another.addLike("Placeholder");
                net2.addPost(another); // qui dovrebbe sollevare l'eccezione DuplicatePost
            } catch (Exception ex) {
                System.out.println("caught: " + ex + "\nOk, eccezione sollevata e gestita");
            }
            // il post che ho provato ad inserire non deve comparire in MicroBlog
            assert !MicroBlog.getPosts().contains(another);
        }

        /* rimuovo ogni post della lista da net2 */
        System.out.println("***Test rmPost()***");
        try {
            for (Post toRm : pList) {
                net2.rmPost(toRm.getId());
            }
            /* 
                provo a rimuovere un post sicuramente non presente in MicroBlog -> deve sollevare 
                NoSuchPostException.
            */
            another = new Post(-1, "Autore", "Deve sollevare NoSuchPostException", now);
            MicroBlog.rmPost(another.getId());
        } catch (Exception ex) {
            System.out.println("caught: " + ex + "\nOk, eccezione sollevata e gestita");
        }
        try {
            /* deve sollevare NullPointerException */
            MicroBlog.rmPost(null);
        } catch (Exception ex) {
            System.out.println("caught: " + ex + "\nOk, eccezione sollevata e gestita");
        }

        /* quindi net2 deve risultare vuota e diversa da MicroBlog */
        assert net2.getPosts().size() == 0 && net2.getMentionedUsers().size() == 0
                && !(MicroBlog.getPosts().size() > 0 && MicroBlog.equals(net2));

        /* stampo la mappa di followers e following indotte dalla lista di post */
        System.out.println("***test guessFollowers() con stessa lista***");
        Map<String, Set<String>> m = MicroBlog.guessFollowers(pList);
        for (String k : m.keySet()) {
            System.out.println(k + "=" + m.get(k));
        }
        System.out.println("***test guessFollowing() con stessa lista***");
        m = MicroBlog.guessFollowing(pList);
        for (String k : m.keySet()) {
            System.out.println(k + "=" + m.get(k));
        }

        /* stampo influencers di MicroBlog */
        System.out.println("***Influencers di MicroBlog***\n" + MicroBlog.influencers());

        /*  
            Verifico con degli assert che per ogni autore della lista di post devo 
            avere lo stesso output sia che chiami writtenBy su una lista di post, 
            sia su this (perchè pList è uguale). Lo stesso vale per getMentionedUsers()
        */
        for (Post p : pList) {
            assert MicroBlog.writtenBy(pList, p.getAuthor()).equals(MicroBlog.writtenBy(p.getAuthor()));
            assert MicroBlog.getMentionedUsers(pList).equals(MicroBlog.getMentionedUsers());
        }

        /* leggo la lista di parole da cercare all'interno dei post (sempre dal test) */
        List<String> someWords = new ArrayList<String>();
        while (s.hasNext()) {
            someWords.add(s.nextLine());
        }
        s.close();

        System.out.println("***test di containing()***\nwords = " + someWords + "\nMicroBlog.containing(words) = \n[");
        for (Post p : MicroBlog.containing(someWords)) {
            System.out.print(p);
        }
        System.out.println("]");

        // creo un'istanza del social moderato
        ModeratedSocialNetwork mnet = new ModeratedSocialNetwork(pList);
        System.out.print("***Social moderato inizializzato***\n[\n");
        System.out.println("badwords: " + ModeratedSocialNetwork.getBadwords().toString());
        System.out.println(mnet.toString());
        System.out.println("]\nI post segnalati sono:\n" + mnet.getOffensive().toString());
    }

    /* per leggere post secondo il loro formato */
    public static ArrayList<Post> PostReader(Scanner scan) {

        /* creo una lista di Post a cui aggiungo tutti i post letti tramite lo Scanner */
        ArrayList<Post> pList = new ArrayList<Post>();
        Post p = null;

        String auth, text;
        Date dt = new Date();
        // legge una lista di post fino alla stringa fine
        while (!scan.hasNext("fine")) {
            auth = scan.nextLine();
            text = scan.nextLine(); // per semplicità non sono consentite \n nel testo

            /* legge il timestamp nel formato indicato,
            altrimenti solleva un'eccezione di parsing */
            try {
                dt = new SimpleDateFormat("dd-mm-yyyy hh:mm").parse(scan.nextLine());
            } catch (ParseException ex) {
                System.out.println("Caught: " + ex);
            }

            /* ho preparato i dati per creare un post: lo creo e se non solleva
            eccezioni lo aggiungo alla lista e stampo il Post con il metodo
            toString()*/
            try {
                p = new Post(auth, text, dt);
                pList.add(p);
            } catch (Exception ex) {
                System.out.println("Caught: " + ex);
            }
        }
        scan.skip("fine\n"); // salto la stringa fine

        /* se non ho post */
        if (pList.size() <= 0) {
            System.out.println("Lista di post vuota");
        }
        /* ritorno la lista di post al chiamante */
        return pList;
    }
};