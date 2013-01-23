import groovy.json.JsonSlurper

new JsonSlurper().parseText(
        """
   {"charset":"GBK","body":"fetchJSON_CommentList({"CommentSummary":{"SkuId":"1014782396","ReferenceId":"1014782396","ProductId":"1014782396","Skuids":aaa,"Score1Count":0,"Score2Count":0,"Score3Count":0,"Score4Count":0,"Score5Count":0,"ShowCount":0,"CommentCount":0,"AverageScore":5,"GoodCount":0,"GoodRate":0,"GoodRateShow":0,"GoodRateStyle":0,"PoorCount":0,"PoorRate":-0,"PoorRateShow":-0,"PoorRateStyle":0,"GeneralCount":0,"GeneralRate":1,"GeneralRateShow":100,"GeneralRateStyle":150},"Users":[],"Comments":[],"Score":0})n","token":"PRODUCT-COMMENT,BUY360","url":"http://club.360buy.com/clubservice/newproductcomment-1014782396-0-1.html?callback=fetchJSON_CommentList","extra":{"baseUrl":"http://club.360buy.com/clubservice/newproductcomment-#productId#-0-#pageNum#.html?callback=fetchJSON_CommentList","commentNum":0,"page":1,"ReferenceName":"<E5><BA><B7><E8><B4><9D>combi<E5><A9><B4><E5><84><BF><E6><8E><A8><E8><BD><A6><E4><B8><93><E7><94><A8><E6><A3><89><E5><9E><AB> <E4><BD><BF><E7><94><A8>SJ-3EXSK-3RSJ-8EXlk-2dx","mid":"50ffed9f6341f20c4b404a2c","brandName":"SK-II","productId":"1014782396"}}
"""
)