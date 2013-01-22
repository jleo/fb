import com.mongodb.BasicDBObject

win = 1
push = 2
lose =3
winFactor = 4
pushFactor = 5
loseFactor = 6

BasicDBObject cacheQuery = new BasicDBObject();
cacheQuery.append("win", win);
cacheQuery.append("push", push);
cacheQuery.append("lose", lose);
cacheQuery.append("winFactor", winFactor);
cacheQuery.append("pushFactor", pushFactor);
cacheQuery.append("loseFactor", loseFactor);

BasicDBObject cacheQuery2 = new BasicDBObject();
cacheQuery.append("win", win);
cacheQuery.append("push", push);
cacheQuery.append("lose", lose);
cacheQuery.append("winFactor", winFactor);
cacheQuery.append("pushFactor", pushFactor);
cacheQuery.append("loseFactor", loseFactor);

println cacheQuery.toString()