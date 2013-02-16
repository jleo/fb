//package org.basketball
//
//import com.enigmastation.ml.bayes.annotations.BayesClassifier
//import com.enigmastation.ml.bayes.annotations.NaiveBayesClassifier
//import com.enigmastation.ml.bayes.impl.SimpleClassifier
//import com.mongodb.BasicDBObject
//
///**
// * Created with IntelliJ IDEA.
// * User: jleo
// * Date: 13-2-13
// * Time: 上午9:47
// * To change this template use File | Settings | File Templates.
// */
//@BayesClassifier
//@NaiveBayesClassifier
//class BasketballClassifier extends SimpleClassifier {
//    protected List<Object> getFeatures(Object source) {
//        BasicDBObject snapshot = (BasicDBObject) source;
//
//        def featureAndCount = []
//        Sharding.keyEventAbbr.values().each { abbr ->
//            def countA = snapshot.get("ae").get(abbr)
//            if (countA) {
//                countA = countA as int
//                for (int i = 0; i < countA; i++) {
//                    featureAndCount.add(abbr)
//                }
////                featureAndCount.add(new AbbrAndCount(abbr, countA as int))
//            }
//
//            def countB = snapshot.get("be").get(abbr)
//            if (countB) {
//                countB = countB as int
//                for (int i = 0; i < countB; i++) {
//                    featureAndCount.add(abbr)
//                }
////                featureAndCount.add(new AbbrAndCount(abbr, countB as int))
//            }
//        }
//        def scores = snapshot.get("score").split("-")
//        int current = (scores[0] as int) + (scores[1] as int)
//        for (int i = 0; i < current; i++) {
//            featureAndCount.add("current")
//        }
////        featureAndCount.add(new AbbrAndCount("current", current))
//
//
//        int sec = (snapshot.get("sec") as int)
//        for (int i = 0; i < sec; i++) {
//            featureAndCount.add("sec")
//        }
////        featureAndCount.add(new AbbrAndCount("time", snapshot.get("sec") as int))
//        return featureAndCount
//    }
//
////    @Override
////    public void train(Object source, Object classification) {
////        List<Object> features = getFeatures(source);
////        for (AbbrAndCount anc : features) {
////            String feature = anc.getAbbr()
////            incrementFeature(feature, classification, anc.getCount());
////        }
////        incrementCategory(classification);
////    }
////
////    private void incrementFeature(Object feature, Object category, int count) {
////        Feature f = features.get(feature);
////        if (f == null) {
////            f = new Feature();
////            f.setFeature(feature);
////            f.setCategories(new HashMap<Object, Integer>());
////        }
////        features.put(feature, f);
////        count.times {
////            f.incrementCategoryCount(category);
////        }
////    }
////
////    public void incrementCategory(Object category) {
////        Integer oldCount = categories.get(category);
////        if (oldCount == null) {
////            oldCount = 0;
////        }
////        categories.put(category, oldCount + 1);
////    }
////
////    int featureCount(Object feature, Object category) {
////        //if (features.containsKey(feature) && features.get(feature).containsKey(category)) {
////        //  return features.get(feature).get(category);
////        //}
////        AbbrAndCount abbrAndCount = feature;
////        Feature f = features.get(abbrAndCount.getAbbr());
////        if (f == null) {
////            return 0;
////        }
////        return f.getCountForCategory(category);
////    }
//
////    public double documentProbability(Object source, Object category) {
////        List<Object> features = getFeatures(source);
////        double p = 1.0;
////        for (Object f : features) {
////            AbbrAndCount abbrAndCount = f;
////            abbrAndCount.count.times {
////                p *= weightedProb(f, category);
////            }
////        }
////        return p;
////    }
//}
