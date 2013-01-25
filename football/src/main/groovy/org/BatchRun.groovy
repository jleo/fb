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
        def initProbability = "0.5"
        def endProbability = "0.8"
        def probalilityStep = "0.01"



        def expectation = "0.0"
        def endExpectation = "0.5"
        def expectationStep = "0.01"



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
