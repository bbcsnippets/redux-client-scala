package uk.co.bbc.redux

import scala.xml._
import java.util.Date

/**
 * @author ${user.name}
 */
object Key {
  def createFromXMLResponse(xml:NodeSeq):Key = {
    var value:String = (xml \\ "response" \\ "programme" \\ "key").text
    new Key(value)
  }
}

class Key (val value:String) {

  val createdAt:Date = new Date
  val expiresAt:Date = new Date(createdAt.getTime() + (24 * 60 * 60 * 1000))

}
