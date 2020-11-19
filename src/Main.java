import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static DBase base = new DBase();
    private static Scanner scanner = new Scanner(System.in);
    private static String lastPair;

    public static void main(String[] args) throws SQLException, PresentException {
        displayWelcome();
        loop();
    }

    private static void displayWelcome() {
        System.out.println("------------ DARKNESS ------------");
        displayFill();
    }

    private static void displayFill() {
        try {
            System.out.printf("Darkness database is full in %.2f%%\n", base.getFillIndex());
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loop() {
        while(true) {
            displayInputSign();
            String input = scanner.nextLine();
            String[] words = input.split(" ");

            if(words.length == 1) {
                String word = words[0];

                if(word.equals("??"))
                    gotoUnknownPair();

                else if(word.length() == 2)
                    displayPair(word);

                else if(word.equals("all"))
                    displayAll();

                else if(word.equals("exit"))
                    exit();

                else if(word.equals("?"))
                    gotoFirstUnknownPair();

                else if(word.equals("%"))
                    displayFill();

                else if(word.length() == 1)
                    displayLetter(word.charAt(0));

                else if(word.length() > 2) {
                    addWord(word);
                    if(lastPair != null)
                        displayPair(lastPair);
                }
            }

            else if(words.length == 2) {
                if(words[0].equals("-")) {
                    deleteWord(words[1]);
                }
            }
        }
    }

    private static void displayInputSign() {
        if(lastPair == null)
            System.out.print(">> ");
        else
            System.out.print(lastPair + " >> ");
    }

    private static void displayPair(String pair) {
        if(!CorrectChecker.isCorrectPair(pair)) {
            System.out.println("ERROR: Pair is incorrect!");
            return;
        }

        lastPair = pair;

        try {
            PairEntry pairEntry = base.getPair(pair);
            if(pairEntry.getWords().length > 0) {
                System.out.println(pair.toLowerCase() + " - " + pairEntry.getBest().toUpperCase() + " - " + String.join(" ", pairEntry.getWords()));
            }
            else
                System.out.println("Darkness...");

        } catch(SQLException e) {
            System.err.println("ERROR: Unable to display pair");
        }
    }

    private static void addWord(String word) {
        if(lastPair == null) {
            System.out.println("ERROR: You have to select pair before adding word!");
            return;
        }

        try {
            WordEntry wordEntry = base.getWord(word);

            if(wordEntry != null && wordEntry.getPair().equals(lastPair)) {
                base.setBest(word);
            }

            else if(wordEntry != null) {
                System.err.println("ERROR: Word is present for pair " + wordEntry.getPair() + "!");
            }

            else {
                base.insert(lastPair, word.toLowerCase(), false);
            }
        } catch (SQLException throwables) {
            System.err.println("ERROR: Unable to add word!");
        }
    }

    private static void deleteWord(String word) {
        if(lastPair == null) {
            System.out.println("ERROR: You have to select pair before deleting word!");
            return;
        }

        try {
            WordEntry wordEntry = base.getWord(word);

            if(wordEntry == null) {
                System.err.println("ERROR: Word doesn't exist is database!");
            }

            else if(!wordEntry.getPair().equals(lastPair)) {
                System.err.println("ERROR: You have to select proper pair before deleting!");
            }

            else {
                base.deleteWord(word);
                displayPair(lastPair);
            }
        } catch (SQLException throwables) {
            System.err.println("ERROR: Unable to delete word!");
        }
    }

    private static void displayAll() {
        int nr = 1;
        for(char i : CorrectChecker.CORRECT_LETTERS.toCharArray()) {
            for(char j : CorrectChecker.CORRECT_LETTERS.toCharArray()) {
                String pair = i + "" + j;
                try {
                    PairEntry pairEntry = base.getPair(pair);
                    if(pairEntry.getWords().length > 0) {
                        System.out.printf("%3d ", nr++);
                        System.out.println(pair.toLowerCase() + " - " + pairEntry.getBest().toUpperCase() + " - " + String.join(" ", pairEntry.getWords()));
                    }
                } catch(SQLException e) {
                    System.err.println("ERROR: Unable to display pair");
                }
            }
        }
    }

    private static void displayLetter(char i) {
        if(!CorrectChecker.isCorrectLetter(i)) {
            System.err.print("ERROR: Incorrect letter!");
            return;
        }

        int nr = 1;
        for(char j : CorrectChecker.CORRECT_LETTERS.toCharArray()) {
            String pair = i + "" + j;
            try {
                PairEntry pairEntry = base.getPair(pair);
                System.out.printf("%3d ", nr++);
                if(pairEntry.getWords().length > 0) {
                    System.out.println(pair.toLowerCase() + " - " + pairEntry.getBest().toUpperCase() + " - " + String.join(" ", pairEntry.getWords()));
                }
                else {
                    System.out.println("----------darkness---------");
                }
            } catch(SQLException e) {
                System.err.println("ERROR: Unable to display pair");
            }
        }
    }

    private static void gotoUnknownPair() {
        try {
            List<String> absentList = new ArrayList<String>();
            for(char i : CorrectChecker.CORRECT_LETTERS.toCharArray()) {
                for(char j : CorrectChecker.CORRECT_LETTERS.toCharArray()) {
                    String pair = i + "" + j;
                    PairEntry pairEntry = base.getPair(pair);
                    if(pairEntry.getWords().length == 0)
                        absentList.add(pair);
                }
            }

            int rand = new Random().nextInt(absentList.size());
            String randPair = absentList.get(rand);
            displayPair(randPair);
        } catch(SQLException e) {
            System.err.println("ERROR: Unable to go to unknown pair!");
            return;
        }
    }

    private static void gotoFirstUnknownPair() {
        try {
            for(char i : CorrectChecker.CORRECT_LETTERS.toCharArray()) {
                for(char j : CorrectChecker.CORRECT_LETTERS.toCharArray()) {
                    String pair = i + "" + j;
                    PairEntry pairEntry = base.getPair(pair);
                    if(pairEntry.getWords().length == 0) {
                        displayPair(pair);
                        return;
                    }
                }
            }
        } catch(SQLException e) {
            System.err.println("ERROR: Unable to go to unknown pair!");
            return;
        }
    }

    private static void exit() {
        System.out.println("Live long and prosper!");
        System.exit(0);
    }
}
