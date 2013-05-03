package org.basketball

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-5-3
 * Time: 下午5:36
 * Let's RocknRoll
 */
def gameList = new File("/Users/jleo/Dropbox/nba/meta/gameList.txt")
def gameList1996 = new File("/Users/jleo/Dropbox/nba/meta/gameList-1996.txt")

if (gameList.exists())
    gameList.delete()

gameList.createNewFile()

if (gameList1996.exists())
    gameList1996.delete()

gameList1996.createNewFile()

new File("/Users/jleo/Dropbox/nba/meta/list1980-2013.txt").eachLine {
    gameList.append(it - "/boxscores/" - ".html")
}