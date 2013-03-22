package org.basketball;

import ch.usi.inf.sape.hac.HierarchicalAgglomerativeClusterer;
import ch.usi.inf.sape.hac.agglomeration.AgglomerationMethod;
import ch.usi.inf.sape.hac.agglomeration.AverageLinkage;
import ch.usi.inf.sape.hac.dendrogram.*;
import ch.usi.inf.sape.hac.experiment.DissimilarityMeasure;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-3-20
 * Time: 下午2:28
 * Let's RocknRoll
 */
public class Test {
    public static void main(String[] args) throws IOException {
        PlayerAbilityExperiment experiment = new PlayerAbilityExperiment();
        experiment.load(1990);

        DissimilarityMeasure dissimilarityMeasure = new EuclideanDissimilarityMeasure();
        AgglomerationMethod agglomerationMethod = new AverageLinkage();
        DendrogramBuilder dendrogramBuilder = new DendrogramBuilder(experiment.getNumberOfObservations());
        HierarchicalAgglomerativeClusterer clusterer = new HierarchicalAgglomerativeClusterer(experiment, dissimilarityMeasure, agglomerationMethod);
        clusterer.cluster(dendrogramBuilder);
        Dendrogram dendrogram = dendrogramBuilder.getDendrogram();
        dump(experiment, dendrogram);

        FileOutputStream stream = new FileOutputStream("test3");
        ObjectOutputStream out = new ObjectOutputStream(stream); out.writeObject(dendrogram);
        out.close();
    }

    public static void dump(PlayerAbilityExperiment playerAbilityExperiment, Dendrogram dendrogram) {
        dumpNode("  ", dendrogram.getRoot(), playerAbilityExperiment);
    }

    private static void dumpNode(final String indent, final DendrogramNode node, PlayerAbilityExperiment playerAbilityExperiment) {
        if (node == null) {
            System.out.println(indent + "<null>");
        } else if (node instanceof ObservationNode) {
            System.out.println(indent + "Observation: " + playerAbilityExperiment.getId(((ObservationNode) node).getObservation()));
        } else if (node instanceof MergeNode) {
            System.out.println(indent + "Merge:");
            dumpNode(indent + "  ", ((MergeNode) node).getLeft(), playerAbilityExperiment);
            dumpNode(indent + "  ", ((MergeNode) node).getRight(), playerAbilityExperiment);
        }
    }

}
