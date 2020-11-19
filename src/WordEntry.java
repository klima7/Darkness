public class WordEntry {

    private String pair;
    private String word;
    private boolean best;

    public WordEntry(String pair, String word, boolean best) {
        this.pair = pair;
        this.word = word;
        this.best = best;
    }

    public String getPair() {
        return pair;
    }

    public String getWord() {
        return word;
    }

    public boolean isBest() {
        return best;
    }
}
