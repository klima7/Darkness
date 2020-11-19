import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBase {

    public static final String DRIVER = "org.sqlite.JDBC";
    public static final String DB_URL = "jdbc:sqlite:base.db";

    private Connection conn;

    public DBase() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Lack of JDBC driver");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("Unable to open connection");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            createTables();
        } catch (SQLException e) {
            System.err.println("Unable to create table");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createTables() throws SQLException {
        String createEntries = "CREATE TABLE IF NOT EXISTS entries (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pair varchar(10), " +
                "word varchar(255), " +
                "best int)";

        Statement stat = conn.createStatement();
        stat.execute(createEntries);
    }

    public void insert(String pair, String word, boolean best) throws SQLException {
        WordEntry present = getWord(word);
        if(present != null) {
            throw new PresentException(present.getPair(), word);
        }

        PreparedStatement statement = conn.prepareStatement("insert into entries values (NULL, ?, ?, 0)");
        statement.setString(1, pair.toLowerCase());
        statement.setString(2, word.toLowerCase());
        statement.executeUpdate();
        conn.commit();

        PairEntry p = getPair(pair);
        if(best || p.getWords().length == 1)
            setBest(word);
    }

    public PairEntry getPair(String pair) throws SQLException {
        PreparedStatement stat = conn.prepareStatement("SELECT * FROM entries WHERE pair = ?");
        stat.setString(1, pair.toLowerCase());
        ResultSet res = stat.executeQuery();

        List<String> list = new ArrayList<String>();

        String best = null;
        while(res.next()) {
            list.add(res.getString("word"));
            if(res.getInt("best") == 1)
                best = res.getString("word");
        }
        String[] array = new String[list.size()];
        array = list.toArray(array);
        return new PairEntry(pair, array, best);
    }

    public void deleteWord(String word) throws SQLException {
        WordEntry w = getWord(word);
        if(w == null)
            throw new AbsentException(word);

        PreparedStatement stat = conn.prepareStatement("DELETE FROM entries WHERE word = ?");
        stat.setString(1, word);
        stat.executeUpdate();
        conn.commit();

        PairEntry p = getPair(w.getPair());
        String[] words = p.getWords();
        if(words.length > 0) setBest(words[0]);
    }

    public void setBest(String word) throws SQLException {
        WordEntry w = getWord(word);
        if(w == null)
            throw new AbsentException(word);

        PreparedStatement stat = conn.prepareStatement("UPDATE entries SET best = 0 WHERE pair = ?");
        stat.setString(1, w.getPair());
        stat.executeUpdate();

        stat = conn.prepareStatement("UPDATE entries SET best = 1 WHERE pair = ? AND word = ?");
        stat.setString(1, w.getPair());
        stat.setString(2, word);
        int updated = stat.executeUpdate();

        if(updated > 0)
            conn.commit();
        else
            conn.rollback();

    }

    public WordEntry getWord(String word) throws SQLException {
        PreparedStatement stat = conn.prepareStatement("SELECT * FROM entries WHERE word = ?");
        stat.setString(1, word.toLowerCase());
        ResultSet set = stat.executeQuery();
        if(set.next()) {
            String p = set.getString("pair");
            String w = set.getString("word");
            boolean b = set.getInt("best") == 1;
            return new WordEntry(p, w, b);
        }
        return null;
    }

    public double getFillIndex() throws SQLException {
        Statement stat = conn.createStatement();
        ResultSet res = stat.executeQuery("select count(*) from (SELECT * FROM entries group by pair)");
        if(res.next()) {
            return res.getInt("count(*)") / Math.pow(CorrectChecker.CORRECT_LETTERS.length(), 2) * 100;
        }
        else
            return 0;
    }
}
