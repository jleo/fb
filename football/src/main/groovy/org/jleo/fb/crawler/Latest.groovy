import com.mongodb.BasicDBObject
import com.mongodb.Mongo

def matches = new URL("http://odds2.zso8.com/html/match.html").getText("utf-8")
def odds = new URL("http://odds2.zso8.com/html/fixed.html").getText("utf-8")

def matcher = [:]
def odd = [:]

println matches
println odds

def join = {Object... obj->
    def sum = ""
    obj.each {
        if(obj){
            sum+=obj
        }else{
            sum+=""
        }
    }

}

odds.eachLine { line ->
    if (line =~ /\.split\(','\)/) {
        def quote1 = line.indexOf("\"")
        line = line.substring(quote1 + 1, line.length() - 13)
        def split = line.split(",")
        odd.put(split[2], split)
    }
}

matches.eachLine { line ->
    if (line =~ /\.split\(','\)/) {
        def quote1 = line.indexOf("\"")
        try {
            line = line.substring(quote1 + 1, line.length() - 13)
        } catch (e) {
            e.printStackTrace()
        }
        def split = line.split(",")
        matcher.put(split[0], split)
    }
}

def finalResult = odd.collect { cmId, oddData ->
    def matchId = oddData[0]
    def r = [* oddData, * matcher[matchId]]
    return ["matchId": r[0], "cid": r[1], "w1": r[3], 'p1': r[4], 'l1': r[5], 'w2': r[6], 'p2': r[7], 'l2': r[8], "mtype": join(r[15], r[16] , r[17]), 'time': r[19], 'tidA': r[20], 'tNameA': join(r[21] , r[22] , r[23]), 'tidB': r[24], 'tNameB': join(r[25] , r[26] , r[27]), "tRankA": r[28], "tRankB": r[29], "resultRA": r[33], "resultRB": r[34], "resultPA": r[35], "resultPB": r[36], "wa1": r[40], "pa1": r[41], "la1": r[42], "wa2": r[43], "pa2": r[44], "la2": r[45]]
}

println finalResult

db = new Mongo("rm4", 15000).getDB("fb");

def save =db.getCollection("resultnew")
save.drop()

def converted = finalResult.collect {
    new BasicDBObject(it)
}
save.insert(converted)

