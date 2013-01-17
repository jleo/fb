package HandicapProcessing;

import com.apple.eawt.AppEvent;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-10
 * Time: 上午1:41
 * To change this template use File | Settings | File Templates.
 */
public class HandicapProcessor {

    private iHandicapProcessing hp = new HandicapProcessing();

    public static void main(String args[]){
        HandicapProcessor handicapProcessor = new HandicapProcessor();
        handicapProcessor.betOneMatch(3.05, 3.15, 2.2, -0.25, 1.02, 0.9, "snow001", "8", "8");
    }

    public void betOneMatch(double win, double push, double lose, double handicap, double winRate, double loseRate,
                            String matchId, String clientId, String cid){
        hp.setMatch(win, push, lose, handicap, winRate, loseRate, matchId, clientId, cid);
        hp.getResult(10000, 10, true);
    }
}
