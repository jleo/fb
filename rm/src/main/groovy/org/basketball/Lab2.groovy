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

String batchId = UUID.randomUUID().toString()
//String batchId = "1a5b7085-0717-4aac-bf68-1c39f37f0685"
final int projectId = 13038
final int mediaId = 2259
final int placementId = 200159526

def sql = Sql.newInstance("jdbc:mysql://58.215.141.163:3306/db_snap?useUnicode=true&characterEncoding=utf8&autoReconnect=true", "root", "000000", "com.mysql.jdbc.Driver")
//new File("/Users/jleo/Downloads/12537.txt").eachLine {
new File("/Users/jleo/Downloads/13038_20015926.csv").eachLine {
    if (it.contains("t.cn") || it.contains("qzone"))
        return

    def url = it.split(",")[3]
    def rows = sql.rows("select id  from ad_url where url = ?", [url])
    if (rows.size() == 0) {
        sql.executeInsert("insert into ad_url (version,url, date_created,last_updated) values (0,?,?,?)", [url, new Date(), new Date()])
        rows = sql.rows("select id  from ad_url where url = ?", [url])
    }
    def id = rows[0]['id']
    def projectIdRow = sql.rows("select id from project where original_project_id = $projectId")
    if (projectIdRow.size() == 0)
        throw new RuntimeException("cannot find the project with id $projectId")

    def placementIdRow = sql.rows("select id from placement where original_placement_id = $placementId")
    if (placementIdRow.size() == 0)
        throw new RuntimeException("cannot find the placement with id $placementId")

    def mediaIdRow = sql.rows("select id from media where original_media_id = $mediaId")
    if (mediaIdRow.size() == 0)
        throw new RuntimeException("cannot find the media with id $mediaId")

    projectJoinId = projectIdRow[0]['id']
    placementJoinId = placementIdRow[0]['id']
    mediaJoinId = mediaIdRow[0]['id']

    sql.executeInsert("insert into collected_view(version,ad_url_id,batch_id,creativity_id,date_created,last_updated,media_id,placement_id,project_id,view_url) values(0,?,?,?,?,?,?,?,?,?)", [id, batchId, null, new Date(), new Date(), mediaJoinId, placementJoinId, projectJoinId, url])
}
sql.executeInsert("insert into collected_view_transaction(version,batch_end_time,batch_start_time,batch_id,date_created,last_updated) values(0,null,null,?,?,?)", [batchId, new Date(), new Date()])
