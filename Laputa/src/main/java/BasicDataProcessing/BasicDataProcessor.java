package BasicDataProcessing;

import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-6
 * Time: 下午5:56
 * To change this template use File | Settings | File Templates.
 */
public class BasicDataProcessor {
    public static void main(String args[]){

        BasicDataProcessing bdp = new BasicDataProcessing();
        bdp.processBasicData(5.55, 3.95, 1.47, 0.3, 0.3, 0.3);
        BasicData basicData = bdp.getBasicData();

        System.out.println("All match: " + basicData.getMatchCount());

        DecimalFormat df = new DecimalFormat("#.00");
        for (int i = 0; i < 5; ++i){
            for (int j = 0; j < 5; ++j){
                System.out.print(String.valueOf(i)+": " + String.valueOf(j) + "\t\t");
                System.out.print(basicData.getResultSet()[i][j] + "\t\t" + df.format(basicData.getResultSet()[i][j]/basicData.getMatchCount()*100) + "%\t\t");
                System.out.print(basicData.getResultSet()[i][j]/(double)basicData.getMatchCount());
                System.out.println();
            }
        }
        System.out.println("Others:\t\t" + basicData.getResultSet()[5][5] + "\t\t" + df.format(basicData.getResultSet()[5][5]/basicData.getMatchCount()*100)
                + "%\t\t"+ basicData.getResultSet()[5][5]/(double)basicData.getMatchCount());
        System.out.println("Others win: " + basicData.getResultSet()[5][0]);
        System.out.println("Others push: " + basicData.getResultSet()[5][1]);
        System.out.println("Others lose: " + basicData.getResultSet()[5][2]);


        System.out.println("\n**********\n");

        System.out.println("win probability: " + basicData.getWinMatch() + "\t" + df.format(basicData.getWinMatch() / basicData.getMatchCount() * 100) + "%");
        System.out.println("push probability: " + basicData.getPushMatch() + "\t" + df.format(basicData.getPushMatch() / basicData.getMatchCount() * 100) + "%");
        System.out.println("lose probability: " + basicData.getLoseMatch() + "\t" + df.format(basicData.getLoseMatch() / basicData.getMatchCount() * 100) + "%");

        System.out.println("\n**********\n");

        System.out.println("win +3.5: " + (int) basicData.getWin4Game() + "\t" + df.format(basicData.getWin4Game() / basicData.getMatchCount() * 100) + "%");
        System.out.println("win +2.5: " + (int) basicData.getWin3Game() + "\t" + df.format(basicData.getWin3Game() / basicData.getMatchCount() * 100) + "%");
        System.out.println("win +1.5: " + (int) basicData.getWin2Game() + "\t" + df.format(basicData.getWin2Game() / basicData.getMatchCount() * 100) + "%");
        System.out.println("win +0.5: " + (int) basicData.getWin1Game() + "\t" + df.format(basicData.getWin1Game() / basicData.getMatchCount() * 100) + "%");
        System.out.println("push: \t" + (int) basicData.getPushGame() + "\t" + df.format(basicData.getPushGame() / basicData.getMatchCount() * 100) + "%");
        System.out.println("win -0.5: " + (int) basicData.getLose1Game() + "\t" + df.format(basicData.getLose1Game() / basicData.getMatchCount() * 100) + "%");
        System.out.println("win -1.5: " + (int) basicData.getLose2Game() + "\t" + df.format(basicData.getLose2Game() / basicData.getMatchCount() * 100) + "%");
        System.out.println("win -2.5: " + (int) basicData.getLose3Game() + "\t" + df.format(basicData.getLose3Game() / basicData.getMatchCount() * 100) + "%");
        System.out.println("win -3.5: " + (int) basicData.getLose4Game() + "\t" + df.format(basicData.getLose4Game() / basicData.getMatchCount() * 100) + "%");

    }
}
