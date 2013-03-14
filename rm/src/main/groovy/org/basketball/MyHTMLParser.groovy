package org.basketball

import org.ccil.cowan.tagsoup.AutoDetector

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-3-14
 * Time: 下午2:10
 * Let's RocknRoll
 */
class MyHTMLParser {
    static final autoDetectorPropertyName = 'http://www.ccil.org/~cowan/tagsoup/properties/auto-detector'

    static def asHTML(content, encoding = "utf-8") {
        org.ccil.cowan.tagsoup.Parser parser = new org.ccil.cowan.tagsoup.Parser()
        parser.setProperty(autoDetectorPropertyName, [autoDetectingReader: { inputStream ->
            new InputStreamReader(inputStream, encoding)
        }
        ] as AutoDetector)
        new XmlSlurper(parser).parseText(content)
    }
}
