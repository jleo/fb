import BasicDataProcessing.BasicData
import Util.MongoDBUtil
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import org.codehaus.jackson.map.ObjectMapper

def m = MongoDBUtil.getInstance("58.215.168.166","15000","fb")
c = m.getCollection("obj")

//c.save(new BasicDBObject("aa",new BasicData()))

def bd = new BasicData()
bd.lose1Game=1.2

ObjectMapper mapper = new ObjectMapper();
try {
    String basicDataJson = mapper.writeValueAsString(bd);

    BasicDBObject cacheQuery = new BasicDBObject();
    cacheQuery.append("win", 1);
    cacheQuery.append("push", 2);
    cacheQuery.append("lose", 3);
    cacheQuery.append("winFactor", 4);
    cacheQuery.append("pushFactor", 5);
    cacheQuery.append("loseFactor", 6);

    cacheQuery.append("basicDataJson", basicDataJson);
    m.insert(cacheQuery, "resultcache");
} catch (IOException e) {
    e.printStackTrace();
}

BasicDBObject cacheQuery = new BasicDBObject();
cacheQuery.append("win", 1);
cacheQuery.append("push", 2);
cacheQuery.append("lose", 3);
cacheQuery.append("winFactor", 4);
cacheQuery.append("pushFactor", 5);
cacheQuery.append("loseFactor", 6);

DBObject cached = m.findOne(cacheQuery, "resultcache");
if (cached != null) {
    String basicDataJson = (String) cached.get("basicDataJson");
    try {
        basicData = mapper.readValue(basicDataJson, BasicData.class);
        println basicData
        return;
    } catch (IOException e) {
        e.printStackTrace();
    }

}
