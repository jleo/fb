package org.basketball

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-9
 * Time: 下午3:17
 * To change this template use File | Settings | File Templates.
 */
class GameList {
    public static void main(String[] args) {
        //

        def date = Date.parse("yyyy-MM-dd", "1980-02-25")

        def output = new File("/Users/jleo/Dropbox/nba/meta/list1980-2013.txt")

        (Date.parse("yyyyMMdd","20130401")..new Date()).each { d ->
            println d
            def cal = new GregorianCalendar()
            cal.setTime(d)
            def year = cal.get(Calendar.YEAR)
            def month = cal.get(Calendar.MONTH) + 1
            def day = cal.get(Calendar.DAY_OF_MONTH)

            def text = new URL("http://www.basketball-reference.com/boxscores/index.cgi?month=$month&day=$day&year=$year").text

            if (text.indexOf("No games played on this date") != -1)
                return

            text.eachMatch("/boxscores/.*?\\.html") { it ->
                if(it.indexOf("pbp") || it.indexOf("shot"))
                    return

                output.append(it + "\n")
            }
        }

    }
}
