
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

        Integer id;
        String auth, text;
        Date dt = new Date();
        // legge una lista di post fino alla stringa fine
        while (!s.hasNext("fine")) {
            id = s.nextInt();
            s.skip("\n"); // scarta \n lasciato dalla lettura precedente
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
                p = new Post(id, auth, text, dt);
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

        s.close();

        /* stampa (di nuovo) tutti i post inseriti con i relativi likes */
        System.out.println("***Lista dei post inseriti***");
        for (i = 0; i < pList.size(); i++) {
            System.out.print("pList[" + i + "] = " + pList.get(i).toString());
        }
    }
};