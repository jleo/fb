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
    node.tbody.each {
        if (it.@"data-row" != 5) {
            def name = it.tr[0].td[0].toString()
            if (node.@id.toString().contains("basic")) {
                ['MP', 'FG', 'FGA', 'FG%', '3P', '3PA', '3P%', 'FT', 'FTA', 'FT%', 'ORB', 'DRB', 'TRB', 'AST', 'STL', 'BLK', 'TOV', 'PF', 'PTS', '+/-'].eachWithIndex { stat, idx ->
                        def s = null
                    if (stat == "MP")
                        s = it.tr[0].td[idx + 1] as String
                    else
                        s = it.tr[0].td[idx + 1] as float
                    map.get(name).put(stat, s)

                }
            } else {//advanced
                ['MP', 'TSp', 'eFGp', 'ORBp', 'DRBp', 'TRBp', 'ASTp', 'STLp', 'BLKp', 'TOVp', 'USGp', 'ORtg', 'DRtg'].eachWithIndex { stat, idx ->
                    map.get(name).put(stat, it.tr[0].td[idx + 1])
                }
            }
        }
    }
}

println map
println t.size()