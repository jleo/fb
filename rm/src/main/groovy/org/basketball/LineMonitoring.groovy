//package org.basketball
//
//import com.mongodb.BasicDBObject
//import com.mongodb.Mongo
//import org.apache.commons.mail.DefaultAuthenticator
//import org.apache.commons.mail.SimpleEmail
//import org.apache.http.*
//import org.apache.http.client.HttpClient
//import org.apache.http.client.entity.GzipDecompressingEntity
//import org.apache.http.client.methods.HttpGet
//import org.apache.http.impl.client.DefaultHttpClient
//import org.apache.http.protocol.HttpContext
//
///**
// * Created with IntelliJ IDEA.
// * User: jleo
// * Date: 13-3-14
// * Time: 下午2:08
// * Let's RocknRoll
// */
//class LineMonitoring {
//    HttpClient httpClient
//    Mongo m = new Mongo("localhost", 27017)
//
//    LineMonitoring() {
//        httpClient = new DefaultHttpClient()
//        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
//            public void process(
//                    final HttpRequest request,
//                    final HttpContext context) throws HttpException, IOException {
//                if (!request.containsHeader("Accept-Encoding")) {
//                    request.addHeader("Accept-Encoding", "gzip");
//                }
//            }
//        });
//        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
//
//            public void process(
//                    final HttpResponse response,
//                    final HttpContext context) throws HttpException, IOException {
//                HttpEntity entity = response.getEntity();
//                if (entity != null) {
//                    Header ceheader = entity.getContentEncoding();
//                    if (ceheader != null) {
//                        HeaderElement[] codecs = ceheader.getElements();
//                        for (int i = 0; i < codecs.length; i++) {
//                            if (codecs[i].getName().equalsIgnoreCase("gzip")) {
//                                response.setEntity(
//                                        new GzipDecompressingEntity(response.getEntity()));
//                                return;
//                            }
//                        }
//                    }
//                }
//            }
//
//        });
//    }
//
//    public void run() {
//        def c = m.getDB("bb").getCollection("lines")
//
//        HttpGet getMethod = new HttpGet("http://www.vegasinsider.com/nba/odds/offshore/")
//        HttpResponse httpResponse = httpClient.execute(getMethod)
//        int statusCode = httpResponse.getStatusLine().getStatusCode()
//        String pageString = null
//        if (200 <= statusCode && statusCode <= 299) {
//            pageString = new String(httpResponse.entity.content.bytes, "ISO-8859-1")
//        } else {
//            pageString = ""
//        }
//        getMethod.releaseConnection()
//
//        def html = MyHTMLParser.asHTML(pageString, "ISO-8859-1")
//        def root = html.breadthFirst().find {
//            it.@class == "viBodyBorderNorm"
//        }
//
//        String s1, s2
//
//        root.table[1][0].children()[11..-1].each { nn ->
//            if (nn.attributes.size() == 0) {
//                def dateStr = GregorianCalendar.getInstance().get(Calendar.YEAR) + "/" + nn.children[0].children[0].children[0].toString().split(" ")[0]
//                def date = Date.parse("yyyy/MM/dd", dateStr)
//                def teamA = nn.children[0].children[3].children[0].children[0].toString()
//                def teamB = nn.children[0].children[6].children[0].children[0].toString()
//
//                [pinnacle: 3, bookmaker: 8].each { company, column ->
//                    if (nn.children[column].children[0] instanceof String) {
//                        println "empty line,skip"
//                        return
//                    }
//
//                    def ss = nn.children[column].children[0].children[2, 4]*.trim()
//                    s1 = ss[0]
//                    s2 = ss[1]
//
//                    if (!c.findOne([a: teamA.trim(), b: teamB.trim(), date: date, c: company] as BasicDBObject))
//                        c.save([a: teamA.trim(), b: teamB.trim(), s1: s1, s2: s2, date: date, updateOn: new Date(), c: company] as BasicDBObject)
//                    else {
//                        def existing = c.find([a: teamA.trim(), b: teamB.trim(), date: date, c: company] as BasicDBObject).sort([updateOn: -1] as BasicDBObject)
//                        def latest = existing.next()
//
//                        if (latest.get("s1") != s1 || latest.get("s2") != s2) {
//                            onUpdate([a: teamA.trim(), b: teamB.trim(), s1: s1, s2: s2, date: date, updateOn: new Date(), c: company], [s1: latest.get("s1"), s2: latest.get("s2")])
//                            c.save([a: teamA.trim(), b: teamB.trim(), s1: s1, s2: s2, date: date, updateOn: new Date(), c: company] as BasicDBObject)
//                        } else {
//                            println "nothing happens"
//                        }
//                    }
//                }
//
//
//            }
//
//        }
//    }
//
//    static receiver = []
////    static receiver = [pinnacle: "ggyyleo@gmail.com,ggyyleo@gmail.com", bookmaker: "ggyyleo@gmail.com,ggyyleo@gmail.com"]
//
//    def onUpdate(o, old) {
//        println "email..."
//        def receiver = receiver[o.c]
//        if (!receiver){
//            println "no need..."
//            return
//        }
//
//        SimpleEmail email = new SimpleEmail();
//        email.setCharset("utf-8");
//        email.setHostName("smtp.googlemail.com");
//        email.setSmtpPort(465);
//        email.setAuthenticator(new DefaultAuthenticator("ggyyleo", "jf3hf2l1"));
//        email.setSSLOnConnect(true);
//        email.setFrom("ggyyleo@gmail.com");
//        email.setSubject("New Line for " + o.a + " V.S " + o.b);
//
//        String msg = o.a + " V.S " + o.b + "\n" + o.s1 + "/" + o.s2 + "\n" + "from " + old.s1 + "/" + old.s2
//
//        email.setMsg(msg);
//        receiver.split(",").each { r ->
//            email.addTo(r);
//        }
//
//        email.send();
//        println "email sent"
//    }
//
//    public static void main(String[] args) {
//        while (true) {
//            try {
//                LineMonitoring lm = new LineMonitoring()
//                lm.run()
//                sleep 180000
//            } catch (e) {
//                e.printStackTrace()
//            }
//        }
//    }
//}
//