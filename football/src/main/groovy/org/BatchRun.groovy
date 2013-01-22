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
        BigDecimal seedExpectation = new BigDecimal("0.0");
        BigDecimal seedProbability = new BigDecimal("0.5");

        int loopingExpectation = 40;
        int loppingProbability = 12;

        GearmanClientImpl client = new GearmanClientImpl();
        client.addJobServer(new GearmanNIOJobServerConnection("58.215.168.165", 5730));

        for (int i = 0; i < loopingExpectation; ++i) {
            for (int j = 0; j < loppingProbability; ++j) {
                seedProbability = seedProbability.add(new BigDecimal("0.02"));
                System.out.println("trying seedExpectation:" + seedExpectation + ", " + "seedProbability:" + seedProbability);

                GearmanJob job = GearmanJobImpl.createBackgroundJob("org.GearmanFunction", (seedExpectation.toString() + "," + seedProbability.toString()).getBytes(),
                        UUID.randomUUID().toString());

                client.submit(job)
            }
            seedExpectation = seedExpectation.add(new BigDecimal("0.005"));
            seedProbability = new BigDecimal("0.5");
        }
    }
}
