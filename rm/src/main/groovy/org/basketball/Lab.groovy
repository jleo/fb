package org.basketball

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-26
 * Time: 下午4:48
 * Let's RocknRoll
 */

def pptv = new File("/Users/jleo/Downloads/Aegis PPTV0304.csv")
def sdk = new File("/Users/jleo/Downloads/sdk_11055_12334_0304.csv")

def sdkMap = [:]
sdk.eachLine { sdkLine ->
    def split = sdkLine.split(",")
    if (split.size() >= 7)
        sdkMap.put(split[0], split[6..-1].join(";"))
}

def file = new File("/Users/jleo/Downloads/output.csv")
file.createNewFile()
pptv.eachLine { line ->
    if (line) {
        def split = line.split(";")
        def cookie = split[2]
        def id = split[0]
        String toAppend = sdkMap.get(cookie.substring(1, cookie.length() - 1))
        if (toAppend)
            file.append(cookie + ";" + toAppend + ";" + id + "\n")
    }
}

//println ([*(0..25)].subsequences())