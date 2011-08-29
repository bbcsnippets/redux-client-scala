package uk.co.bbc.redux

import org.junit._
import Assert._
import java.util.Date

@Test
class ContentTest {

  @Test
  def testParsesXMLOK() {
    val xml = <response>
      <programme frames="1">
        <time>18:55:00</time>
        <date>2010-12-25</date>
        <channel>bbctwo</channel>
        <reference>5554695377586026009</reference>
        <duration>3900</duration>
        <unixtime>1293303300</unixtime>
        <description>Some description</description>
        <series_crid>fp.bbc.co.uk/KRYRHR</series_crid>
        <programme_crid>fp.bbc.co.uk/1RE93D</programme_crid>
        <name>Top Gear</name>
        <key>some-value</key>
      </programme>
    </response>
    var content:Content = Content.createFromXMLResponse(xml)
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
