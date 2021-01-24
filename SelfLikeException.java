/* sottoclasse di Exception che implementa l'eccezione SelfLike */
class SelfLikeException extends Exception {
    SelfLikeException() {
        super("Non è possibile mettere like al proprio post");
        // crea un istanza di Exception con argomento la stringa indicata
    }

    SelfLikeException(String reason, Throwable parent_exc) {
        super(reason, parent_exc);
    }
};