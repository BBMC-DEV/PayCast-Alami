package kr.co.bbmc.paycastdid.util

import com.orhanobut.logger.Logger
import kr.co.bbmc.paycastdid.deviceId
import kr.co.bbmc.paycastdid.storeId
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

fun onParseXML(fls: String?): Boolean {

    if (fls == null || fls.isEmpty()) return false

    try {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()

        val `is`: InputStream = ByteArrayInputStream(fls.toByteArray())
        parser.setInput(`is`, null)
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    val name = parser.name
                    if (name == "Server") {
                        val ref = parser.getAttributeValue(null, "ref")
                        var i = 0
                        while (i < parser.attributeCount) {
                            val attrName = parser.getAttributeName(i)
                            if (attrName == "storeId") {
                                storeId = runCatching { parser.getAttributeValue(i).toInt() }.getOrDefault(-1)
                            }  else if (attrName == "deviceId") {
                                deviceId = runCatching { parser.getAttributeValue(i) }.getOrDefault("")
                            }
                            i++
                        }
                    }
                }
            }
            //                Log.d(TAG, "parser.NEXT="+parser.nextToken());
            eventType = parser.next()
        }
    } catch (e: Exception) {
        Logger.e("ParseError : ${e.message}")
    }
    return storeId != -1 && deviceId.isNotBlank()
}

@Suppress("DUPLICATE_LABEL_IN_WHEN")
fun parsePlayerOptionXMLV2(fileName: String): Boolean {
    Logger.w("File name : $fileName")
    try {
        val fXmlFile = File(fileName)
        Logger.d("parseAgentOptionXML fileName=$fileName")
        val xmlInfo: InputStream = fXmlFile.inputStream()
        val factory: XmlPullParserFactory = XmlPullParserFactory.newInstance()
        val parser: XmlPullParser = factory.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(xmlInfo, null)

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (event) {
                XmlPullParser.START_TAG -> {
                    when (tagName) {
                        "Server.storeId" -> storeId = runCatching { parser.nextText().toInt() }.getOrDefault(-1)
                        "Server.deviceId" -> deviceId = runCatching { parser.nextText() }.getOrDefault("")
                        else -> {}
                    }
                }
            }
            event = parser.next()
        }
    } catch (e: Exception) {
        Logger.e("Error: ${e.message}")
    }
    Logger.w("Current StoreId : $storeId // deviceId : $deviceId")
    return storeId != -1 && deviceId.isNotBlank()
}