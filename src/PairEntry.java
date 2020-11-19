public class PairEntry {

    private String pair;
    private String[] words;
    private String best;

    public PairEntry(String pair, String[] words, String best) {
        this.pair = pair;
        this.words = words;
        this.best = best;
    }

    public String getPair() {
        return pair;
    }

    public String[] getWords() {
        return words;
    }

    public String getBest() {
        return best;
    }

    @Override
    public String toString() {
        String w = String.join(" ", words);
        return PairEntry.class.getSimpleName() + "[pair=" + pair + "; words=" + w + "; best=" + best + "]";
    }
}
