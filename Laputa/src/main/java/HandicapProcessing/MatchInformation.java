package HandicapProcessing;

import Util.MongoDBUtil;
import Util.Props;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-9
 * Time: 下午8:33
 * To change this template use File | Settings | File Templates.
 */
public class MatchInformation {

    private double win;
    private double push;
    private double lose;

    private double handicap;
    private double winRate;
    private double loseRate;

    private String matchId;
    private String clientId;
    private String cid;

    public MatchInformation(double win, double push, double lose, double handicap, double winRate, double loseRate,
                            String matchId, String clientId, String cid) {
        this.win = win;
        this.push = push;
        this.lose = lose;
        this.handicap = handicap;
        this.winRate = winRate;
        this.loseRate = loseRate;
        this.matchId = matchId;
        this.clientId = clientId;
        this.cid = cid;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public double getWin() {
        return win;
    }

    public void setWin(double win) {
        this.win = win;
    }

    public double getPush() {
        return push;
    }

    public void setPush(double push) {
        this.push = push;
    }

    public double getLose() {
        return lose;
    }

    public void setLose(double lose) {
        this.lose = lose;
    }

    public double getHandicap() {
        return handicap;
    }

    public void setHandicap(double handicap) {
        this.handicap = handicap;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public double getLoseRate() {
        return loseRate;
    }

    public void setLoseRate(double loseRate) {
        this.loseRate = loseRate;
    }

}
