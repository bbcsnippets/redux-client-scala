package uk.co.bbc.redux

import org.junit._
import Assert._
import scala.xml._

@Test
class ScheduleTest extends TestFile {

  @Test
  def testParsesXMLOK() {
    var htmlParser = (new Client).htmlParser
    var xml = htmlParser.load(testFile("tv_schedule.html"))
    var schedule:Seq[String] = Schedule.createFromXMLResponse(xml)
    assertEquals(schedule.length, 201)
    assertEquals(schedule.head, "5564143876136277914")
    assertEquals(schedule.last, "5564507230385969419")
  }

}


