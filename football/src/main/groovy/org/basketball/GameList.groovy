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

        def date = Date.parse("yyyy-MM-dd","2013-02-07")

        def output = new File("/Users/jleo/list2.txt")
        if (output.exists()){
            output.delete()
        }
        output.createNewFile()

        (date..new Date()).each {d->
            println d
            def cal = new GregorianCalendar()
            cal.setTime(d)
            def year = cal.get(Calendar.YEAR)
            def month = cal.get(Calendar.MONTH) + 1
            def day = cal.get(Calendar.DAY_OF_MONTH)

            def text = new URL("http://www.basketball-reference.com/boxscores/index.cgi?month=$month&day=$day&year=$year").text

            text.eachMatch("/boxscores/pbp/.*?\\.html"){it->
                output.append(it+"\n")
            }
        }

    }
}
