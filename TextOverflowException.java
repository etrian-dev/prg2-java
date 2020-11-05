/* sottoclasse di Exception che implementa l'eccezione TextOverflow */
class TextOverflowException extends Exception {
    TextOverflowException() {
        super("Text too long");
        // crea un istanza di Exception con argomento la stringa "Text too long"
    }
};