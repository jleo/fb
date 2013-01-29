package HandicapProcessing;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-9
 * Time: 下午8:32
 * To change this template use File | Settings | File Templates.
 */
public interface iHandicapProcessing {

    public void setMatch(double win, double push, double lose, double handicap , double winRate, double loseRate,
                         String matchId, String clientId, String cid, String teamA, String teamB, int ch);

    public int getResult (boolean display);

    public double getMinRate(int betOn, double minExpectation, String type);

    public void saveMatchResult (String matchName, String betWinOrLose, String resultWinOrLose, double betMoney, double resultGain);
}
