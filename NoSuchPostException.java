class NoSuchPostException extends Exception {
    public NoSuchPostException() {
        super("Il post non è presente nella rete sociale");
        // crea un'istanza di Exception con la stringa indicata
    }
};