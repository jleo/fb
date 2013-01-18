package BasicDataProcessing;

import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-6
 * Time: 下午5:03
 * To change this template use File | Settings | File Templates.
 */
public class BasicDataProcessing implements iBasicDataProcessing {

    private MongoDBUtil dbUtil = null;

    private final static String mongoDBHost = Props.getProperty("MongoDBRemoteHost");
    private final static String mongoDBPort = Props.getProperty("MongoDBRemotePort");
    private final static String mongoDBName = Props.getProperty("MongoDBRemoteName");
    private final String collectionName = Props.getProperty("MatchRemoteResult");

    private BasicData basicData = null;

    private void setDBConnection(String MongoDBHost, String MongoDBPort, String MongoDBName) {
        if (dbUtil == null) {
            dbUtil = MongoDBUtil.getInstance(MongoDBHost, MongoDBPort, MongoDBName);
        } else {
            dbUtil.setMongoDBHost(MongoDBHost);
            dbUtil.setMongoDBPort(MongoDBPort);
            dbUtil.setMongoDBName(MongoDBName);
        }
    }

    public void processBasicData(double win, double push, double lose, double winFactor, double pushFactor, double loseFactor) {
        setDBConnection(mongoDBHost, mongoDBPort, mongoDBName);

        basicData = new BasicData();
        dbUtil.getConnection();

        DBObject winQuery = getWinQuery(win, winFactor);
        DBObject pushQuery = getPushQuery(push, pushFactor);
        DBObject loseQuery = getLoseQuery(lose, loseFactor);

        DBObject query = new BasicDBObject();
        query.put("w1", winQuery);
        query.put("p1", pushQuery);
        query.put("l1", loseQuery);

        DBObject queryField = new BasicDBObject();
        queryField.put("resultRA", 1);
        queryField.put("resultRB", 1);

        List<DBObject> resultList = dbUtil.findAll(query, queryField, collectionName);
        basicData.setMatchCount((double) resultList.size());

        for (DBObject dbObject : resultList) {
            double resultRA = ((Number) dbObject.get("resultRA")).doubleValue();
            double resultRB = ((Number) dbObject.get("resultRB")).doubleValue();
            if (resultRA > 4 || resultRB > 4) {
                ++basicData.getResultSet()[5][5];
                if (resultRA > resultRB) {
                    basicData.setWinMatch(basicData.getWinMatch() + 1);
                    ++basicData.getResultSet()[5][0];
                    if (resultRA - resultRB == 1) {
                        basicData.setWin1Game(basicData.getWin1Game() + 1);
                    } else if (resultRA - resultRB == 2) {
                        basicData.setWin2Game(basicData.getWin2Game() + 1);
                    } else if (resultRA - resultRA == 3) {
                        basicData.setWin3Game(basicData.getWin3Game() + 1);
                    } else {
                        basicData.setWin4Game(basicData.getWin4Game() + 1);
                    }
                } else if (resultRA == resultRB) {
                    basicData.setPushMatch(basicData.getPushMatch() + 1);
                    ++basicData.getResultSet()[5][1];
                } else {
                    basicData.setLoseMatch(basicData.getLoseMatch() + 1);
                    ++basicData.getResultSet()[5][2];
                    if (resultRA - resultRB == -1) {
                        basicData.setLose1Game(basicData.getLose1Game() + 1);
                    } else if (resultRA - resultRB == -2) {
                        basicData.setLose2Game(basicData.getLose2Game() + 1);
                    } else if (resultRA - resultRB == -3) {
                        basicData.setLose3Game(basicData.getLose3Game() + 1);
                    } else {
                        basicData.setLose4Game(basicData.getLose4Game() + 1);
                    }
                }
            } else if (resultRA == 0) {
                if (resultRB == 0) {
                    ++basicData.getResultSet()[0][0];
                } else if (resultRB == 1) {
                    ++basicData.getResultSet()[0][1];
                } else if (resultRB == 2) {
                    ++basicData.getResultSet()[0][2];
                } else if (resultRB == 3) {
                    ++basicData.getResultSet()[0][3];
                } else if (resultRB == 4) {
                    ++basicData.getResultSet()[0][4];
                }
            } else if (resultRA == 1) {
                if (resultRB == 0) {
                    ++basicData.getResultSet()[1][0];
                } else if (resultRB == 1) {
                    ++basicData.getResultSet()[1][1];
                } else if (resultRB == 2) {
                    ++basicData.getResultSet()[1][2];
                } else if (resultRB == 3) {
                    ++basicData.getResultSet()[1][3];
                } else if (resultRB == 4) {
                    ++basicData.getResultSet()[1][4];
                }
            } else if (resultRA == 2) {
                if (resultRB == 0) {
                    ++basicData.getResultSet()[2][0];
                } else if (resultRB == 1) {
                    ++basicData.getResultSet()[2][1];
                } else if (resultRB == 2) {
                    ++basicData.getResultSet()[2][2];
                } else if (resultRB == 3) {
                    ++basicData.getResultSet()[2][3];
                } else if (resultRB == 4) {
                    ++basicData.getResultSet()[2][4];
                }
            } else if (resultRA == 3) {
                if (resultRB == 0) {
                    ++basicData.getResultSet()[3][0];
                } else if (resultRB == 1) {
                    ++basicData.getResultSet()[3][1];
                } else if (resultRB == 2) {
                    ++basicData.getResultSet()[3][2];
                } else if (resultRB == 3) {
                    ++basicData.getResultSet()[3][3];
                } else if (resultRB == 4) {
                    ++basicData.getResultSet()[3][4];
                }
            } else if (resultRA == 4) {
                if (resultRB == 0) {
                    ++basicData.getResultSet()[4][0];
                } else if (resultRB == 1) {
                    ++basicData.getResultSet()[4][1];
                } else if (resultRB == 2) {
                    ++basicData.getResultSet()[4][2];
                } else if (resultRB == 3) {
                    ++basicData.getResultSet()[4][3];
                } else if (resultRB == 4) {
                    ++basicData.getResultSet()[4][4];
                }
            } else {
                System.out.println("!!!! Missing !!!");
            }
        }

        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (i > j) {
                    basicData.setWinMatch(basicData.getWinMatch() + basicData.getResultSet()[i][j]);
                } else if (i == j) {
                    basicData.setPushMatch(basicData.getPushMatch() + basicData.getResultSet()[i][j]);
                } else {
                    basicData.setLoseMatch(basicData.getLoseMatch() + basicData.getResultSet()[i][j]);
                }
            }
        }

        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                if ((i - j) >= 4) {
                    basicData.setWin4Game(basicData.getWin4Game() + basicData.getResultSet()[i][j]);
                } else if ((i - j) == 3) {
                    basicData.setWin3Game(basicData.getWin3Game() + basicData.getResultSet()[i][j]);
                } else if ((i - j) == 2) {
                    basicData.setWin2Game(basicData.getWin2Game() + basicData.getResultSet()[i][j]);
                } else if ((i - j) == 1) {
                    basicData.setWin1Game(basicData.getWin1Game() + basicData.getResultSet()[i][j]);
                } else if ((i - j) == 0) {
                    basicData.setPushGame(basicData.getPushGame() + basicData.getResultSet()[i][j]);
                } else if ((i - j) == -1) {
                    basicData.setLose1Game(basicData.getLose1Game() + basicData.getResultSet()[i][j]);
                } else if ((i - j) == -2) {
                    basicData.setLose2Game(basicData.getLose2Game() + basicData.getResultSet()[i][j]);
                } else if ((i - j) == -3) {
                    basicData.setLose3Game(basicData.getLose3Game() + basicData.getResultSet()[i][j]);
                } else if ((i - j) <= -4) {
                    basicData.setLose4Game(basicData.getLose4Game() + basicData.getResultSet()[i][j]);
                }
            }
        }
    }

    public BasicData getBasicData() {
        return basicData;
    }

    private DBObject getLoseQuery(double lose, double loseFactor) {
        DBObject loseQuery = new BasicDBObject();
        loseQuery.put("$gte", lose - loseFactor);
        loseQuery.put("$lte", lose + loseFactor);

        return loseQuery;
    }

    private DBObject getPushQuery(double push, double pushFactor) {
        DBObject pushQuery = new BasicDBObject();
        pushQuery.put("$gte", push - pushFactor);
        pushQuery.put("$lte", push + pushFactor);

        return pushQuery;
    }

    private DBObject getWinQuery(double win, double winFactor) {
        DBObject winQuery = new BasicDBObject();
        winQuery.put("$gte", win - winFactor);
        winQuery.put("$lte", win + winFactor);

        return winQuery;
    }
}
