import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression

println "11-8+2W".find("[0-9]*[0-9]*[0-9]-[0-9]*[0-9]*[0-9]")

OLSMultipleLinearRegression olsMultipleLinearRegression = new OLSMultipleLinearRegression();

FileInputStream stream = new FileInputStream("train");
ObjectInputStream out = new ObjectInputStream(stream);
def allTraining = out.readObject()
out.close()

stream = new FileInputStream("real");
out = new ObjectInputStream(stream);
def allReal = out.readObject()
out.close()

def size = allReal.length
double[] real = new double[size]

allReal.eachWithIndex { it, idx ->
    real[idx] = it[0]
}
olsMultipleLinearRegression.newSampleData(allTraining, allReal)
