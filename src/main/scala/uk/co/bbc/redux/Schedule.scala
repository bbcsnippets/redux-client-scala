package uk.co.bbc.redux

import scala.xml._
import scala.collection.immutable.Seq._
import scala.util.matching.Regex

object Schedule {

  val diskRefUrl = new Regex("""\/programme\/(\d+)\/download\/image-150.jpg""")

  def createFromXMLResponse(xml:NodeSeq) : scala.collection.immutable.Seq[String] = {
    xml \\ "img" flatMap {
      node => node \ "@src" text match {
        case diskRefUrl(diskRef) => Some(diskRef)
        case _ => None
      }
    }
  }

}
