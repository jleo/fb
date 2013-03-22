package org.basketball

import ch.usi.inf.sape.hac.dendrogram.Dendrogram
import ch.usi.inf.sape.hac.dendrogram.DendrogramNode
import ch.usi.inf.sape.hac.dendrogram.MergeNode
import ch.usi.inf.sape.hac.dendrogram.ObservationNode
import it.uniroma1.dis.wiserver.gexf4j.core.EdgeType
import it.uniroma1.dis.wiserver.gexf4j.core.Gexf
import it.uniroma1.dis.wiserver.gexf4j.core.Graph
import it.uniroma1.dis.wiserver.gexf4j.core.Mode
import it.uniroma1.dis.wiserver.gexf4j.core.data.Attribute
import it.uniroma1.dis.wiserver.gexf4j.core.data.AttributeClass
import it.uniroma1.dis.wiserver.gexf4j.core.data.AttributeList
import it.uniroma1.dis.wiserver.gexf4j.core.data.AttributeType
import it.uniroma1.dis.wiserver.gexf4j.core.impl.GexfImpl
import it.uniroma1.dis.wiserver.gexf4j.core.impl.StaxGraphWriter
import it.uniroma1.dis.wiserver.gexf4j.core.impl.data.AttributeListImpl

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-3-20
 * Time: 下午4:06
 * Let's RocknRoll
 */
class Gefx {
    public static void main(String[] args) {
        FileInputStream stream = new FileInputStream("test3");
        ObjectInputStream inp = new ObjectInputStream(stream);
        Dendrogram dendrogram = inp.readObject();

        PlayerAbilityExperiment experiment = new PlayerAbilityExperiment();
        experiment.load(2007);

        Test.dump(experiment, dendrogram)

        Gexf gexf = new GexfImpl();
        Calendar date = Calendar.getInstance();

        gexf.getMetadata()
                .setLastModified(date.getTime())
                .setCreator("Leo Shao")
                .setDescription("NBA Player");

        Graph graph = gexf.getGraph();
        graph.setDefaultEdgeType(EdgeType.UNDIRECTED).setMode(Mode.STATIC);


        graph.getAttributeLists().add(attrList);



        dump(experiment, dendrogram, graph)

        StaxGraphWriter graphWriter = new StaxGraphWriter();
        File f = new File("/Users/jleo/player3.gexf");
        Writer out;
        try {
            out = new FileWriter(f, false);
            graphWriter.writeToStream(gexf, out, "UTF-8");
            System.out.println(f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
    static Attribute attASPM = attrList.createAttribute("0", AttributeType.FLOAT, "aspm");
    static Attribute attOASPM = attrList.createAttribute("1", AttributeType.FLOAT, "oaspm");
    static Attribute attDASPM = attrList.createAttribute("2", AttributeType.FLOAT, "daspm")

    static count = 0

    public static void dump(PlayerAbilityExperiment playerAbilityExperiment, Dendrogram dendrogram, Graph graph) {
        int count = 0;
        def center = graph.createNode(count++ + "");
        center.setLabel("ClusterCenter")

        dumpNode("  ", dendrogram.getRoot(), playerAbilityExperiment, graph, center);
    }

    private static void dumpNode(final String indent, final DendrogramNode node, PlayerAbilityExperiment playerAbilityExperiment, Graph graph, center) {
        if (node == null) {
            System.out.println(indent + "<null>");
        } else if (node instanceof ObservationNode) {
            def playerNode = graph.createNode(count++ + "");
            playerNode.connectTo(center)
            playerNode.setLabel(playerAbilityExperiment.getId(((ObservationNode) node).getObservation()))
            playerNode.getAttributeValues().addValue(attASPM, playerAbilityExperiment.getStat(((ObservationNode) node).getObservation())[57] as String)
            playerNode.getAttributeValues().addValue(attOASPM, playerAbilityExperiment.getStat(((ObservationNode) node).getObservation())[61] as String)
            playerNode.getAttributeValues().addValue(attDASPM, playerAbilityExperiment.getStat(((ObservationNode) node).getObservation())[65] as String)
            System.out.println(indent + "Observation: " + playerAbilityExperiment.getId(((ObservationNode) node).getObservation()));
        } else if (node instanceof MergeNode) {
            System.out.println(indent + "Merge:");

            def mergeNode = graph.createNode(count++ + "");
            mergeNode.connectTo(center)
            mergeNode.getAttributeValues().addValue(attASPM, 1 as String)
            dumpNode(indent + "  ", ((MergeNode) node).getLeft(), playerAbilityExperiment, graph, mergeNode);
            dumpNode(indent + "  ", ((MergeNode) node).getRight(), playerAbilityExperiment, graph, mergeNode);
        }
    }
}
