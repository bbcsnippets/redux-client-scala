package uk.co.bbc.redux

import scala.xml._
import java.util.Date

object Content {
  def createFromXMLResponse(xml:NodeSeq) : Content = {
    var details:NodeSeq      = xml \\ "response" \\ "programme"
    var diskReference:String = (details \ "reference").text
    var duration:Int         = (details \ "duration").text.toInt
    var startDate:Date       = new Date((details \ "unixtime").text.toInt * 1000)
    var channel:String       = (details \ "channel").text
    var frames:Boolean       = (details \ "@frames").text == "1"
    var title:String         = (details \ "name").text
    var description:String   = (details \ "description").text
    var seriesCrid:String    = (details \ "series_crid").text
    var programmeCrid:String = (details \ "programme_crid").text
    var keyValue:String      = (details \ "key").text
    var key:Key              = new Key(keyValue)
    new Content(diskReference, duration, startDate, channel, frames, title, description, seriesCrid, programmeCrid, key)
  }
}

class Content(val diskReference:String, val duration:Int, val startDate:Date, val channel:String, val frames:Boolean,
                  val title:String, val description:String, val seriesCrid:String, val programmeCrid:String, var key:Key)











