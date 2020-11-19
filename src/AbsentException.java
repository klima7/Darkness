public class AbsentException extends RuntimeException {
    private String word;

    public AbsentException(String word) {
        super("Word " + word + " is absent");
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
