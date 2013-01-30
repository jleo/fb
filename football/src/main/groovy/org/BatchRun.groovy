package org

import org.gearman.client.GearmanClientImpl
import org.gearman.client.GearmanJob
import org.gearman.client.GearmanJobImpl
import org.gearman.common.GearmanNIOJobServerConnection

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-1-18
 * Time: 下午6:08
 * Let's RocknRoll
 */
class BatchRun {
    public static void main(String[] args) {
//        def initProbability = "0.6375"
//        def endProbability = "0.6376"
//        def probalilityStep = "0.0001"
//
//
//
//        def expectation = "0.01"
//        def endExpectation = "0.1"
//        def expectationStep = "0.001"
        //------iter 1--------
        def initProbability = "0.5"
        def endProbability = "0.85"
        def probalilityStep = "0.005"



        def expectation = "0.01"
        def endExpectation = "0.1"
        def expectationStep = "0.005"
        //------iter 1--------

        //------iter 1.5--------
//        def initProbability = "0.67"
//        def endProbability = "0.71"
//        def probalilityStep = "0.001"
//
//        def expectation = "0.01"
//        def endExpectation = "0.1"
//        def expectationStep = "0.005"
        //------iter 1.5--------

        //------iter 2--------
//                def initProbability = "0.69"
//        def endProbability = "0.6901"
//        def probalilityStep = "0.0001"
//
//
//
//        def expectation = "0.001"
//        def endExpectation = "0.1"
//        def expectationStep = "0.001"
        //------iter2---------
        BigDecimal seedExpectation = new BigDecimal(expectation);
        BigDecimal seedProbability = new BigDecimal(initProbability);

        GearmanClientImpl client = new GearmanClientImpl();
        client.addJobServer(new GearmanNIOJobServerConnection("58.215.168.165", 5730));

        for (int i = 0; i < ((endExpectation as double) - (expectation as double)) / (expectationStep as double); ++i) {
            for (int j = 0; j < ((endProbability as double) - (initProbability as double)) / (probalilityStep as double); ++j) {
                seedProbability = seedProbability.add(new BigDecimal(probalilityStep));
                System.out.println("trying seedExpectation:" + seedExpectation + ", " + "seedProbability:" + seedProbability);

                GearmanJob job = GearmanJobImpl.createBackgroundJob("org.GearmanFunction", (seedExpectation.toString() + "," + seedProbability.toString()).getBytes(),
                        UUID.randomUUID().toString());

                client.submit(job)
            }
            seedExpectation = seedExpectation.add(new BigDecimal(expectationStep));
            seedProbability = new BigDecimal(initProbability);
        }
    }
}
