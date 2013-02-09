package com.ncs.hj;

import BetMatchProcessing.BetMatchBatchProcessor;
import BetMatchProcessing.ByChProbabilityAndExpectation;
import BetMatchProcessing.FromPropProbabilityAndExpectationFinder;
import BetMatchProcessing.ProbabilityAndExpectation;
import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.DBObject;
import org.Latest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpringQtz {
    private static int counter = 0;

    protected void execute() {
        long ms = System.currentTimeMillis();
        System.out.println("\t\t" + new Date(ms));

        Latest l = new Latest();
        l.latest(1, true);

        final List<DBObject> matchList = BetMatchBatchProcessor.getAllBettingMatch(true);

        ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(Props.getProperty("thread")));

        MongoDBUtil dbUtil = MongoDBUtil.getInstance(Props.getProperty("MongoDBRemoteHost"),
                Props.getProperty("MongoDBRemotePort"),
                Props.getProperty("MongoDBRemoteName"));

        ProbabilityAndExpectation probabilityAndExpectation = new ByChProbabilityAndExpectation(new FromPropProbabilityAndExpectationFinder());

        BetMatchBatchProcessor betMatchBatchProcessor = new BetMatchBatchProcessor(executorService, matchList, dbUtil, false);
        betMatchBatchProcessor.betBatchMatchHandicapGuarantee(probabilityAndExpectation, matchList);
    }

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext("/spring-config.xml");
        Thread.sleep(100000000);
    }
}
