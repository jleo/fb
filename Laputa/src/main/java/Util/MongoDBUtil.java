package Util;

import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 12-12-12
 * Time: 下午5:28
 * To change this template use File | Settings | File Templates.
 */
public class MongoDBUtil {
    private String mongoDBHost;
    private String mongoDBPort;
    private String mongoDBName;

    private Mongo mongo;
    private DB mongoDB;

    private boolean isConnected;

    private static MongoDBUtil dbUtil;

    private MongoDBUtil(String MongoDBHost, String MongoDBPort, String MongoDBName) {
        this.mongoDBHost = MongoDBHost;
        this.mongoDBPort = MongoDBPort;
        this.mongoDBName = MongoDBName;

        try {
            MongoOptions options = new MongoOptions();
            options.threadsAllowedToBlockForConnectionMultiplier = 30;
            options.connectionsPerHost = 300;
            mongo = new Mongo(mongoDBHost, Integer.parseInt(mongoDBPort));
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        mongoDB = mongo.getDB(mongoDBName);
    }

    public static synchronized MongoDBUtil getInstance(String MongoDBHost, String MongoDBPort, String MongoDBName) {
        if (dbUtil == null) {
            dbUtil = new MongoDBUtil(MongoDBHost, MongoDBPort, MongoDBName);
        }
        return dbUtil;
    }

    public String getMongoDBHost() {
        return mongoDBHost;
    }

    public void setMongoDBHost(String mongoDBHost) {
        this.mongoDBHost = mongoDBHost;
    }

    public String getMongoDBPort() {
        return mongoDBPort;
    }

    public void setMongoDBPort(String mongoDBPort) {
        this.mongoDBPort = mongoDBPort;
    }

    public String getMongoDBName() {
        return mongoDBName;
    }

    public void setMongoDBName(String mongoDBName) {
        this.mongoDBName = mongoDBName;
    }

    public void insert(DBObject object, String collectionName) {
        DBCollection collection = mongoDB.getCollection(collectionName);
        collection.insert(object);
    }

    public DBObject findOne(DBObject query, String collectionName) {
        DBCollection collection = mongoDB.getCollection(collectionName);
        return collection.findOne(query);
    }

    public DBObject findOne(DBObject query, DBObject field, String collectionName) {
        DBCollection collection = mongoDB.getCollection(collectionName);
        return collection.findOne(query, field);
    }

    public List<DBObject> findAll(DBObject query, String collectionName) {
        List<DBObject> resultList = new ArrayList<DBObject>();
        DBCollection collection = mongoDB.getCollection(collectionName);
        DBCursor cursor = collection.find(query);
        while (cursor.hasNext()) {
            DBObject dbObject = cursor.next();
            resultList.add(dbObject);
        }
        return resultList;
    }

    public List<DBObject> findAll(DBObject query, DBObject filed, String collectionName) {
        List<DBObject> resultList = new ArrayList<DBObject>();
        DBCollection collection = mongoDB.getCollection(collectionName);
        DBCursor cursor = collection.find(query, filed);
        while (cursor.hasNext()) {
            DBObject dbObject = cursor.next();
            resultList.add(dbObject);
        }
        return resultList;
    }

    public void update(DBObject query, DBObject update, String collectionName) {
        DBCollection collection = mongoDB.getCollection(collectionName);
        collection.update(query, update);
    }

    public void upsert(DBObject query, DBObject update, boolean multi, String collectionName) {
        DBCollection collection = mongoDB.getCollection(collectionName);
        collection.update(query, update, true, multi);
    }

    public void remove(DBObject query, String collectionName) {
        DBCollection collection = mongoDB.getCollection(collectionName);
        collection.remove(query);
    }

    public void removeAll(String collectionName) {
        DBCollection collection = mongoDB.getCollection(collectionName);
    }

    public void drop(String collectionName) {
        DBCollection collection = mongoDB.getCollection(collectionName);
        collection.drop();
    }


    public int getCount(DBObject query, String collectionName) {
        DBCollection collection = mongoDB.getCollection(collectionName);
        return (int) collection.count(query);
    }

    public int getCollectionSize(String collectionName) {
        DBCollection collection = mongoDB.getCollection(collectionName);
        return collection.find().size();
    }

    public DBCollection getCollection(String collectionName) {
        return mongoDB.getCollection(collectionName);
    }
}
