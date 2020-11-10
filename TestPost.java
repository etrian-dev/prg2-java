
/* testa la classe Post */
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

class TestPost {
    /* 
        Il main crea delle istanze di Post a partire da dati letti dal file
        fornito come argomento al programma, le stampa. Dopo aggiungo dei like e stampo di nuovo i post creati.
    */
    public static void main(String[] args) {
        /* Se non è stato passato nessun file solleva l'eccezione FileNotFoundException */
        File ifile = new File(args[0]);
        Scanner s = null;
        try {
            s = new Scanner(ifile);
        } catch (FileNotFoundException ex) {
            System.out.println("Nessun file di test trovato. Riprovare.");
            return;
        }

        /* creo una lista di Post a cui aggiungo tutti i post letti tramite lo Scanner */
        ArrayList<Post> pList = new ArrayList<Post>();
        Post p = null;

        String auth, text;
        Date dt = new Date();
        // legge una lista di post fino alla stringa fine
        while (!s.hasNext("fine")) {
            auth = s.nextLine();
            text = s.nextLine(); // per semplicità non sono consentite \n nel testo

            /* legge il timestamp nel formato indicato,
            altrimenti solleva un'eccezione di parsing */
            try {
                dt = new SimpleDateFormat("dd-mm-yyyy hh:mm").parse(s.nextLine());
            } catch (ParseException ex) {
                System.out.println("Caught: " + ex);
            }

            /* ho preparato i dati per creare un post: lo creo e se non solleva
            eccezioni lo aggiungo alla lista e stampo il Post con il metodo
            toString()*/
            try {
                p = new Post(auth, text, dt);
                pList.add(p);
                System.out.print("New Post: " + p.toString());
            } catch (Exception ex) {
                System.out.println("Caught: " + ex);
            }
        }
        s.skip("fine\n"); // salto la stringa fine

        /* se non ho post è inutile eseguire il resto del codice */
        if (pList.size() <= 0) {
            throw new IllegalArgumentException("Lista di post vuota");
        }

        int i;
        /* stampa tutti i post inseriti */
        System.out.println("***Lista dei post inseriti***");
        for (i = 0; i < pList.size(); i++) {
            System.out.print("pList[" + i + "] = " + pList.get(i).toString());
        }

        /* adesso aggiungo un po' di like */
        Random rng = new Random();
        System.out.println("***Aggiungo dei likes***");
        /* prendo la lista di autori dei post dalla lista di post */
        int j;
        String liker;
        while (s.hasNext()) {
            j = rng.nextInt(pList.size()); // scelgo a caso uno dei post della lista
            // scelgo da riga di comando chi mette like al post
            System.out.println("Chi mette like al post:\n" + pList.get(j).toString() + "?");
            liker = s.nextLine();
            System.out.println("liker = " + liker);
            // Se è consentito l'utente del post k mette like al post j
            try {
                pList.get(j).addLike(liker);
            } catch (Exception ex) {
                System.out.println("Caught: " + ex);
            }
        }

        /* stampa (di nuovo) tutti i post inseriti con i relativi likes */
        System.out.println("***Lista dei post inseriti***");
        for (i = 0; i < pList.size(); i++) {
            System.out.print("pList[" + i + "] = " + pList.get(i).toString());
        }

        s.close();

        /*
            costruisco due istanze di SocialNetwork: una inizializzata da pList
            l'altra vuota
        */
        SocialNetwork net = new SocialNetwork(pList);
        SocialNetwork net2 = new SocialNetwork();

        //devono essere diverse
        assert net.equals(net2) == false;

        // aggiungo a net2 tutti i post di pList tramite il metodo apposito
        try {
            for (Post pp : pList) {
                net2.addPost(pp);
            }
        } catch (Exception ex) {
            System.out.println("caught: " + ex);
        }

        // perciò ora devono risultare uguali
        assert net.equals(net2) == true;

        // creo un nuovo post e lo aggiungo a net2
        // voglio provocare un conflitto di id
        Date d = new Date();
        Post another = null;
        try {
            another = new Post("Qualcuno", "TestTestTest", d);
            another.addLike("Qualcun altro");
            net2.addPost(another);
        } catch (Exception ex) {
            System.out.println("caught: " + ex);
        }

        // allora adesso net e net2 devono essere diverse
        assert net.equals(net2) == false;

        // stampo net e net2 tramite il metodo toString
        System.out.println("*******net*******\n" + net.toString());
        System.out.println("*******net2*******\n" + net2.toString());

        // rimuovo ogni post da net2
        try {
            for (Post pp : pList) {
                net2.rmPost(pp);
            }
            net2.rmPost(another);
        } catch (Exception ex) {
            System.out.println("caught: " + ex);
        }

        // chiaramente sono diverse
        assert net.equals(net2) == false;

        // stampo net e net2 tramite il metodo toString (net2 deve essere vuota)
        System.out.println("*******net*******\n" + net.toString());
        System.out.println("*******net2*******\n" + net2.toString());
    }
};