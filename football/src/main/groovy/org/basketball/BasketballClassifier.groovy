package org.basketball

import com.enigmastation.ml.bayes.Feature
import com.enigmastation.ml.bayes.annotations.BayesClassifier
import com.enigmastation.ml.bayes.annotations.NaiveBayesClassifier
import com.enigmastation.ml.bayes.impl.SimpleClassifier
import com.mongodb.BasicDBObject

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-13
 * Time: 上午9:47
 * To change this template use File | Settings | File Templates.
 */
@BayesClassifier
@NaiveBayesClassifier
class BasketballClassifier extends SimpleClassifier {
    protected List<Object> getFeatures(Object source) {
        BasicDBObject snapshot = (BasicDBObject) source;

        def featureAndCount = []
        Sharding.keyEventAbbr.values().each { abbr ->
            def countA = snapshot.get("ae").get(abbr)
            if (countA) {
                featureAndCount.add(new AbbrAndCount("ae:" + abbr, countA as int))
            }

            def countB = snapshot.get("be").get(abbr)
            if (countB) {
                featureAndCount.add(new AbbrAndCount("be:" + abbr, countB as int))
            }
        }
        featureAndCount.add(new AbbrAndCount("time", snapshot.get("sec") as int))
        return featureAndCount
    }

    @Override
    public void train(Object source, Object classification) {
        List<Object> features = getFeatures(source);
        for (AbbrAndCount anc : features) {
            String feature = anc.getAbbr()
            incrementFeature(feature, classification, anc.getCount());
        }
        incrementCategory(classification);
    }

    private void incrementFeature(Object feature, Object category, int count) {
        Feature f = features.get(feature);
        if (f == null) {
            f = new Feature();
            f.setFeature(feature);
            f.setCategories(new HashMap<Object, Integer>());
        }
        features.put(feature, f);
        count.times {
            f.incrementCategoryCount(category);
        }
    }

    public void incrementCategory(Object category) {
        Integer oldCount = categories.get(category);
        if (oldCount == null) {
            oldCount = 0;
        }
        categories.put(category, oldCount + 1);
    }
}
