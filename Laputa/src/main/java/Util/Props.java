package Util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 12-12-12
 * Time: 下午4:51
 * To change this template use File | Settings | File Templates.
 */
public class Props {
    private static Properties props = null;
    private static final String propertyFile = "Laputa.properties";

    static {
        try {
            String dir = System.getProperty("user.dir");
            FileInputStream file = new FileInputStream(new File(dir + "/Laputa.properties"));
            props = new Properties();
            props.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static String getProperty(String property) {
        return props.getProperty(property);
    }

    public synchronized static String getProperty(String property, String defaultValue) {
        return props.getProperty(property, defaultValue);
    }

    public static void main(String[] args) {
        System.out.println(Props.getProperty("MongoDBHost"));
    }
}
