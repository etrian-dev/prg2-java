
/* testa la classe Post */
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

class TestPost {
    public static void main(String[] args) {
        /* 
        leggo una lista di post dal file passato in args[0] e
        con tali dati istanzio una lista di Post
        */
        File ifile = new File(args[0]);
        Scanner s = null;
        try {
            s = new Scanner(ifile);
        } catch (FileNotFoundException ex) {
            System.out.println("Nessun file di test trovato. Riprovare.");
            return;
        }

        ArrayList<Post> pList = new ArrayList<Post>();
        Post p = null;

        Integer id;
        String dummy, auth, text;
        Date dt = new Date();
        while (s.hasNext()) {
            id = s.nextInt();
            dummy = s.nextLine(); // scarta \n lasciato dalla riga prima
            auth = s.nextLine();
            text = s.nextLine(); // per semplicità non sono consentite \n nel testo
            try {
                dt = new SimpleDateFormat("dd-mm-yyyy hh:mm").parse(s.nextLine());
            } catch (ParseException e) {
                System.out.println(e);
            }

            /* ho preparato i dati per creare un post */
            try {
                p = new Post(id, auth, text, dt);
                pList.add(p);
                System.out.print("New Post: " + p.toString());
            } catch (TextOverflowException ex) {
                System.out.println("Caught: " + ex);
            }
        }
        s.close();

        /* stampa tutti i post inseriti */
        for (int i = 0; i < pList.size(); i++) {
            System.out.print("pList[" + i + "] = " + pList.get(i).toString());
        }
    }
};