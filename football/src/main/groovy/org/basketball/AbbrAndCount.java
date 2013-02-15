package org.basketball;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-13
 * Time: 上午9:56
 * To change this template use File | Settings | File Templates.
 */
public class AbbrAndCount {
    String abbr;
    int count;

    public AbbrAndCount(String abbr, int count) {
        this.abbr = abbr;
        this.count = count;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
