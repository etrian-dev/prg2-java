/* sottoclasse di Exception che implementa l'eccezione TextOverflow */
class TextOverflowException extends Exception {
    TextOverflowException() {
        super("Il testo deve contenere al più 140 caratteri");
        // crea un istanza di Exception con argomento la stringa "Text too long"
    }
};