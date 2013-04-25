//package org.basketball
//
//import ch.usi.inf.sape.hac.experiment.DissimilarityMeasure
//import ch.usi.inf.sape.hac.experiment.Experiment
//
///**
// * Created with IntelliJ IDEA.
// * User: jleo
// * Date: 13-3-20
// * Time: 下午2:39
// * Let's RocknRoll
// */
//class EuclideanDissimilarityMeasure implements DissimilarityMeasure{
//    @Override
//    double computeDissimilarity(Experiment experiment, int observation1, int observation2) {
//        EuclideanDoublePoint o1 = ((PlayerAbilityExperiment) experiment).getObservations(observation1)
//        EuclideanDoublePoint o2 = ((PlayerAbilityExperiment) experiment).getObservations(observation2)
//
//        return o1.distanceFrom(o2)
//    }
//}
