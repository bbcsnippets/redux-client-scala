package uk.co.bbc.redux

import scala.xml._
import org.junit._
import Assert._
import java.util.Date

@Test
class ContentTest extends TestFile {

  @Test
  def testParsesXMLOK() {
    var content:Content = Content.createFromXMLResponse(XML.load(testFile("content.xml")))
    assertEquals(content.diskReference, "5554695377586026009")
    assertEquals(content.duration, 3900)
    assertEquals(content.channel, "bbctwo")
    assertEquals(content.startDate, new Date(1293303300 * 1000))
    assertEquals(content.title, "Top Gear")
    assertEquals(content.description, "Some description")
    assertEquals(content.seriesCrid, "fp.bbc.co.uk/KRYRHR")
    assertEquals(content.programmeCrid, "fp.bbc.co.uk/1RE93D")
    assertEquals(content.key.value, "some-value")
    assertEquals(content.frames, true)
  }

}
