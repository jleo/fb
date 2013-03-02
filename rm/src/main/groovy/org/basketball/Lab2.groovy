package org.basketball

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-26
 * Time: 下午4:48
 * Let's RocknRoll
 */

//def pptv = new File("/Users/jleo/Downloads/Aegis PPTV0228.csv")
//def sdk = new File("/Users/jleo/Downloads/sdk_11003_12334.csv")
//
//def sdkMap = [:]
//sdk.eachLine { sdkLine ->
//    def split = sdkLine.split(",")
//    if (split.size() >= 7)
//        sdkMap.put(split[0], split[6..-1].join(";"))
//}
//
//def file = new File("/Users/jleo/Downloads/output.csv")
//file.createNewFile()
//pptv.eachLine { line ->
//    if (line) {
//        def cookie = line.split(";")[2]
//
//        String toAppend = sdkMap.get(cookie.substring(1,cookie.length()-1))
//        if (toAppend)
//            file.append(cookie + ";" + toAppend+"\n")
//    }
//}

FileInputStream stream2 = new FileInputStream("/Users/jleo/train");
ObjectInputStream out2 = new ObjectInputStream(stream2);
def allTraining2 = out2.readObject()
out2.close()

println allTraining2[100]