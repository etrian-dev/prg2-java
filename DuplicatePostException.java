/* sottoclasse di Exception che implementa l'eccezione Post duplicato */
class DuplicatePostException extends Exception {
    /* due versioni: una senza id come parametro ed una con l'id del post duplicato */
    DuplicatePostException() {
        super("Esiste già un post con questo id");
    }

    DuplicatePostException(Integer id) {
        super("Esiste già un post con id = " + id);
    }
};