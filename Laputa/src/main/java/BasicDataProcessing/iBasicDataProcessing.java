package BasicDataProcessing;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-6
 * Time: 下午5:01
 * To change this template use File | Settings | File Templates.
 */
public interface iBasicDataProcessing {

    public void processBasicData(double win, double push, double lose, double winFactor, double pushFactor, double loseFactor);

    public BasicData getBasicData();

}
