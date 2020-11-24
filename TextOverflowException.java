/* sottoclasse di Exception che implementa l'eccezione TextOverflow */
class TextOverflowException extends Exception {
    TextOverflowException(Integer id) {
        super("Il testo del post con id = " + id + " contiene più di 140 caratteri");
        // crea un istanza di Exception con argomento la stringa indicata 
        // (usa l'id del post)
    }
};