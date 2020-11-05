class Post {
    /*
    @overview:  Post è una 6-tupla (sei campi ordinati) di valori, di cui solo l'ultimo
                è modificabile (un insieme modificabile di String):
                Elemento tipico: (id, auth, text, date, time, {s_1, ..., s_n})
    
    Funzione di astrazione
    AF(x) = (x.id, x.author, x.text, x.date, x.tm, x.likes)
    Invariante di rappresentazione
    IR(x) = x != null 
            && x.id != null
            && x.author != null
            && x.text != null
            && x.date != null 
            && x.tm != null 
            && x.likes != null
            && x.text.length() <= 140                         (max 140 caratteri String text)
            && for all i. 0 <= i < x.likes.size() - 1
                for all j. i < j < x.likes.size()
                    x.likes.get(j) != null
                    && x.likes.get(i) != x.likes.get(j)     (non ho duplicati nel set likes)
                    && x.likes.get(j) != x.author           (autore del post non può mettere like ad esso)
    */

    private Integer id;
    private String author;
    private String text;
    private Date date;
    private Time tm;
    private Set<String> likes;
    
    /* costruttore con tutti i parametri necessari ad istanziare il Post con 0 like*/
    /*
    @requires:  id != null
                && author != null
                && text != null
                && date != null 
                && tm != null 
                && text.length() <= 140
    @throws:    TextOverflowException, NullPointerException
    @modifies:  this
    @effects:   Se almeno uno dei parametri è uguale a null, allora solleva NullPointerException.
                Altrimenti se text.length() > 140 solleva TextOverflowException.
                Altrimenti crea un'istanza di Post con un set di like vuoto e gli altri parametri.
    */
    public Post(Integer id, String author, String text, Date date, Time tm) 
    throws TextOverflowException, NullPointerException {
        if(id == null || author == null || text == null || date == null || tm == null) {
            throw new NullPointerException();
        }
        if(text.length() > 140) {
            throw new TextOverflowException();
        }
    
        this.id = id;
        this.author = author;
        this.text = text;
        this.date = date;
        this.tm = tm;
        this.likes = HashSet<String>(); //inizializzo ad un set di likes vuoto
    }
    
    /* 
        metodi osservatori
        La clausola modifies è omessa in quanto non modificano l'oggetto (this)
    */
    /*
    @requires:  true
    @effects:   ritorna una copia di this.id
    */
    public Integer getId() {return new Integer(this.id);}
    /*
    @requires:  true
    @effects:   ritorna una copia di this.author
    */
    public String getAuthor() {return new String(this.author);}
    /*
    @requires:  true
    @effects:   ritorna una copia di this.text
    */
    public String getText() {return new String(this.text);}
    /*
    @requires:  true
    @effects:   ritorna una copia sotto forma di array di String di
                {this.likes.get(i) : for all i. 0 <= i < this.likes.size() this.likes.get(i) != null}
                (cioè tutti gli elementi non nulli conteuti nel Set)
    */
    public String[] getLikes() {return this.likes.toArray();}
    
    /* metodi modificatori */
    /*
    @requires:  follower != null
    @throws:    NullPointerException
    @modifies:  this
    @effects:   Se follower == null, allora solleva NullPointerException().
                Altrimenti se 
                (this.author.equals(follower) == true) || (this.likes.contains(follower) == true)
                allora non fa niente.
                Altrimenti esegue this.likes.add(follower)
    */
    public void addLike(String follower) {
        if(follower == null) throw new NullPointerException();
        
        if((this.author.equals(follower) == false) && (this.likes.contains(follower) == false)) {
            this.likes.add(follower);
        }
    }
    
    /*
    @requires:  true
    @effects:   ritorna, su una String, la rappresentazione di this nel formato espresso da AF
    */
    public String toString() {
        String s =  "( " + this.id + ",\n" + this.author + ",\n" 
                    + this.text + ",\n" + this.date + ",\n" + this.tm + ",\n"
                    + "{" + this.likes.toString() + "} )\n";
    }
};
