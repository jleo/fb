package org.basketball

import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-5-2
 * Time: 下午11:33
 * To change this template use File | Settings | File Templates.
 */
class GenerateTrainingSamples {
    public static void main(String[] args) {
        Mongo m = new Mongo("localhost")
        def games = m.getDB("bb").getCollection("games")


    }
}
