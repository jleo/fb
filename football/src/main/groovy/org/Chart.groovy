import com.mongodb.DB
import com.mongodb.Mongo
import groovy.swing.SwingBuilder
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.ui.ApplicationFrame

import javax.swing.WindowConstants as WC
import java.awt.*

/**
 * Created with IntelliJ IDEA.
 * User: shangrz
 * Date: 13-1-24
 * Time: 上午12:21
 * To change this template use File | Settings | File Templates.
 */
class Visual extends ApplicationFrame {

    static DB db

    Visual(String title) {
        super(title)
    }

    static DB getDb() {

        if (!db)
            db = new Mongo("58.215.168.166", 15000).getDB("fb")
        return db
    }

//    DBCollection standrad_product = standrad_db.getCollection("product");

//    def getDb(){
//        if (!db)
//            db = new Mongo("58.215.168.166", 15000).getDB("fb")
//        return db
//
//    }

    def test(Closure clouser) {
        def summaryDate = getDb().getCollection("summaryDate")
        println summaryDate.count()
        def options = [true, true, true]
        XYSeries dataSeries = new XYSeries("胜率");
        clouser.call(summaryDate, dataSeries)
//        summaryDate.find().each {
//            
//
//            if (it.value.total >= 100)  {
//                def aid = it._id?.aid
//                println "##:${aid}"
//                aid = aid.replace("Guarantee","")
//                def _ = aid.split("\\.")
//                def x1 = "0.${_[1][0..-2]}"
//                def x2 = "0.${_[2]}"
//                println "${x1}  ${x2} ${it.value.total}"
//
//
//                dataSeries.add(x2.toDouble(),it.value.total);
//
//            }
//
//
//        }
//
//        summaryDate.find().each {
//            if (it.value.count >= 100)  {
//                def aid = it._id?.aid
//                aid = aid.replace("Guarantee","")
//                def _ = aid.split("\\.")
//                def x1 = "0.${_[1][0..-2]}"
//                def x2 = "0.${_[2]}"
//                println x1
//                dataSeries.add(x2.toDouble(), it.value.correctRate);
//
//            }
//
//
//        }


        XYSeriesCollection xyDataset = new XYSeriesCollection();

        xyDataset.addSeries(dataSeries);

        def chart = ChartFactory.createXYLineChart("chart", "x", "y", xyDataset, PlotOrientation.VERTICAL, true, false, false)
        final ChartPanel chartPanel = new ChartPanel(chart);

//        def chart = ChartFactory.createPieChart("Pie Chart Sample",
//                piedataset, *options)
        chart.backgroundPaint = Color.white
        def swing = new SwingBuilder()
        def frame = swing.frame(title: 'Groovy fb',
                defaultCloseOperation: WC.EXIT_ON_CLOSE) {
            panel(id: 'canvas') {
                def panel = new ChartPanel(chart)
                panel.setPreferredSize(new java.awt.Dimension(1280, 800));
                widget(panel)
            }
        }
        frame.pack()
        frame.show()

    }

    public static void main(String[] args) {
        def v = new Visual()
        v.test() { def collection, XYSeries dataSeries ->
            def r = []
            def map = [:].withDefault {
                0
            }
            collection.find().each {
                if (it.value.total >= 1 ) {
                    def aid = it._id?.aid
                    println "##:${aid}"
                    aid = aid.replace("Guarantee", "")
                    def _ = aid.split("\\.")
                    def x1 = "0.${_[1][0..-2]}"
                    def x2 = "0.${_[2]}"

                    def value = it.value.net

                    map.put(x2, map.get(x2) + value)
                }
            }
            map.each { k, value ->
                dataSeries.add(Double.valueOf(k.toString()), value)
            }
        }
    }
}
