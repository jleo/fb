/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-1-26
 * Time: 下午5:49
 * Let's RocknRoll
 */
public class ListParser {
    static parse(String str) {
        if (str.startsWith("[")) {
            str = str[1..-2]
            return str.split(",")
        }
        return str
    }
}
