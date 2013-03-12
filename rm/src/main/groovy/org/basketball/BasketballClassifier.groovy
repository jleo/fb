import org.basketball.BoxScoreParser

def a = new URL("http://www.basketball-reference.com/boxscores/200703110LAL.html").text
def list = []
a.eachMatch("div_.*?_basic") {
    list << it.replaceAll("div_|_basic", "")
}

def html = BoxScoreParser.asHTML(a)

def t = [:]
html.breadthFirst().each { node ->
    list.each { abbr ->
        if (node.@id in [abbr + "_basic", abbr + "_advanced"])
            t.put(node.@id, node)
    }
}

def map = [:].withDefault {
    [:]
}
t.each { abbr, node ->
    int count = 0
    node.tbody.tr.each { c ->
        count++
        if (count != 6) {
            def name = c[0].children()[0].children()[0].children()[0].toString()

            if (node.@id.toString().contains("basic")) {
                ['MP', 'FG', 'FGA', 'FG%', '3P', '3PA', '3P%', 'FT', 'FTA', 'FT%', 'ORB', 'DRB', 'TRB', 'AST', 'STL', 'BLK', 'TOV', 'PF', 'PTS', '+/-'].eachWithIndex { stat, idx ->
                    def s = c[0].children()[idx + 1].children()[0]?.toString()
                    if (stat == "MP") {
                        def time = s.split(":")
                        s = (time[0] as int) * 60 + (time[1] as int)
                    } else {
                        if (s)
                            s = s as float
                        else
                            s = 0
                    }

                    map.get(name).put(stat, s)

                }
            } else {//advanced
                ['MP', 'TS%', 'eFG%', 'ORB%', 'DRB%', 'TRB%', 'AST%', 'STL%', 'BLK%', 'TOV%', 'USG%', 'ORtg', 'DRtg'].eachWithIndex { stat, idx ->
                    def s = c[0].children()[idx + 1].children()[0]?.toString()
                    if (stat == "MP") {
                        def time = s.split(":")
                        s = (time[0] as int) * 60 + (time[1] as int)
                    } else {
                        if (s)
                            s = s as float
                        else
                            s = 0
                    }

                    map.get(name).put(stat, s)
                }
            }
        }
    }
}

map.each {player, stats->

}
println t.size()