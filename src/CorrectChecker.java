public class CorrectChecker {

    public static final String CORRECT_LETTERS = "abcdefghijklłmnoprstuwz";

    public static boolean isCorrectLetter(char c) {
        char lower = Character.toLowerCase(c);
        return CORRECT_LETTERS.indexOf(lower) >= 0;
    }

    public static boolean isCorrectPair(String pair) {
        return pair.length() == 2 && isCorrectLetter(pair.charAt(0)) && isCorrectLetter(pair.charAt(1));
    }
}
