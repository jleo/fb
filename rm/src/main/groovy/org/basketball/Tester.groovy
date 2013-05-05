package org.basketball
/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-13
 * Time: 上午11:38
 * To change this template use File | Settings | File Templates.
 */

def allTeamMate = new HashSet()
Combination comb = new Combination();

List home = (["a", "b"] as List) + (["c", "d"] as List)
List away = (["e", "f"] as List) + (["g", "h"] as List)


comb.mn(home as String[], 2);
def homesub = comb.getCombList()

comb.mn(away as String[], 2);
def awaysub = comb.getCombList()

allTeamMate.addAll(homesub)
allTeamMate.addAll(awaysub)

println(allTeamMate)

home = (["e", "f"] as List) + (["g", "h"] as List)
away = (["b","a","e"] as List) + (["c", "d"] as List)


comb.mn(home as String[], 2);
homesub = comb.getCombList()

comb.mn(away as String[], 2);
awaysub = comb.getCombList()

allTeamMate.addAll(homesub)
allTeamMate.addAll(awaysub)

println(allTeamMate)