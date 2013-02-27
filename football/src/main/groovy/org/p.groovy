package org

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-27
 * Time: 下午8:20
 * To change this template use File | Settings | File Templates.
 */
def file = new File("/Users/jleo/Downloads/result.txt")

def max = 47;
file.readLines().eachWithIndex { it, idx ->
    if ((idx - 4) % 6 == 0) {
        try {
            def d = it[0..4] as double
            if (d > (max as double)) {
                println d
            }
        } catch (e) {
            println it.replaceAll("%", "")
        }

    }
}
println max