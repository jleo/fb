package org.basketball

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-3-25
 * Time: 下午8:06
 * Let's RocknRoll
 */


def file = new File("/Users/jleo/players.txt")
('a'..'z').each { letter ->
    def url = new URL("http://www.basketball-reference.com/players/" + letter + "/")
    println letter
    url.text.eachMatch("href=\"/players/.*?.html") {
        file.append("http://www.basketball-reference.com" + it[6..-1] + "\n")
    }
}
