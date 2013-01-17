package BasicDataProcessing;

public class BasicData {

    private double matchCount = 0;

    private int winMatch = 0;
    private int pushMatch = 0;
    private int loseMatch = 0;

    private double win1Game = 0;
    private double win2Game = 0;
    private double win3Game = 0;
    private double win4Game = 0;

    private double pushGame = 0;

    private double lose1Game = 0;
    private double lose2Game = 0;
    private double lose3Game = 0;
    private double lose4Game = 0;

    private int[][] resultSet = new int[6][6];

    public double getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(double matchCount) {
        this.matchCount = matchCount;
    }

    public BasicData() {
    }

    public int[][] getResultSet() {
        return resultSet;
    }

    public void setResultSet(int[][] resultSet) {
        this.resultSet = resultSet;
    }

    public double getWin4Game() {
        return win4Game;
    }

    public void setWin4Game(double win4Game) {
        this.win4Game = win4Game;
    }

    public double getLose4Game() {
        return lose4Game;
    }

    public void setLose4Game(double lose4Game) {
        this.lose4Game = lose4Game;
    }

    public int getWinMatch() {
        return winMatch;
    }

    public void setWinMatch(int winMatch) {
        this.winMatch = winMatch;
    }

    public int getPushMatch() {
        return pushMatch;
    }

    public void setPushMatch(int pushMatch) {
        this.pushMatch = pushMatch;
    }

    public int getLoseMatch() {
        return loseMatch;
    }

    public void setLoseMatch(int loseMatch) {
        this.loseMatch = loseMatch;
    }

    public double getWin2Game() {
        return win2Game;
    }

    public void setWin2Game(double win2Game) {
        this.win2Game = win2Game;
    }

    public double getWin3Game() {
        return win3Game;
    }

    public void setWin3Game(double win3Game) {
        this.win3Game = win3Game;
    }

    public double getPushGame() {
        return pushGame;
    }

    public void setPushGame(double pushGame) {
        this.pushGame = pushGame;
    }

    public double getLose1Game() {
        return lose1Game;
    }

    public void setLose1Game(double lose1Game) {
        this.lose1Game = lose1Game;
    }

    public double getLose2Game() {
        return lose2Game;
    }

    public void setLose2Game(double lose2Game) {
        this.lose2Game = lose2Game;
    }

    public double getLose3Game() {
        return lose3Game;
    }

    public void setLose3Game(double lose3Game) {
        this.lose3Game = lose3Game;
    }

    public double getWin1Game() {

        return win1Game;
    }

    public void setWin1Game(double win1Game) {
        this.win1Game = win1Game;
    }
}