/* sottoclasse di Exception che implementa l'eccezione Post duplicato */
class DuplicatePostException extends Exception {
    DuplicatePostException() {
        super("Esiste già un post con questo id");
    }

    DuplicatePostException(Integer id) {
        super("Esiste già un post con id = " + id);
    }
};