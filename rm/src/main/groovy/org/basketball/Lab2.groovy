package org.basketball

import groovy.sql.Sql

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-26
 * Time: 下午4:48
 * Let's RocknRoll
 */

//def a = [[1],[2],[3],[4]] as double[][]
//def b = [10,24,32,49] as double[]
//OLSMultipleLinearRegression olsMultipleLinearRegression = new OLSMultipleLinearRegression();
//olsMultipleLinearRegression.newSampleData(b,a)
//
//println olsMultipleLinearRegression.estimateRegressionParameters()
//println olsMultipleLinearRegression.estimateResiduals()
//println olsMultipleLinearRegression.estimateRegressionParametersStandardErrors()
//println olsMultipleLinearRegression.estimateRegressandVariance()
////println olsMultipleLinearRegression.

//def year = 2013
//def yearBefore = year - 1
//
//def shorten = (year - 1900) < 100 ? (year - 1900) : (year - 2000) >= 10 ? (year - 2000) : "0" + (year - 2000)
//
//def result = yearBefore + "-" + shorten
//
//println result

def sql = Sql.newInstance("jdbc:mysql://58.215.141.163:3306/db_snap?useUnicode=true&characterEncoding=utf8&autoReconnect=true", "root", "000000", "com.mysql.jdbc.Driver")
new File("/Users/jleo/Downloads/12537.txt").eachLine {
    sql.executeInsert("insert into ad_url (version,url,ad_location_id,location_id, date_created,project_id,last_updated) values (0,?,80,?,?,5,?)",[it.split(",")[1],'200152198',new Date(),new Date()])
}
