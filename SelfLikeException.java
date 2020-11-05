/* sottoclasse di Exception che implementa l'eccezione SelfLike */
class SelfLikeException extends Exception {
    SelfLikeException() {
        super("Cannot like your post!");
        // crea un istanza di Exception con argomento la stringa "Cannot like your post"
    }
};