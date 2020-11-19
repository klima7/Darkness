public class PresentException extends RuntimeException {

    private String pair;
    private String word;

    public PresentException(String pair, String word) {
        super("Word " + word + " is already present for pair " + pair);
        this.word = word;
        this.pair = pair;
    }

    public String getPair() {
        return pair;
    }

    public String getWord() {
        return word;
    }
}
