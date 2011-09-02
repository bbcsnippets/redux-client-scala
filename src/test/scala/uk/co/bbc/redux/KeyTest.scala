package uk.co.bbc.redux

import org.junit._
import Assert._
import java.util.Date
import scala.xml._

@Test
class KeyTest extends TestFile {

  var key:Key = new Key("some-key")

  @Test
  def testParsesXMLOK() {
    var key:Key = Key.createFromXMLResponse(XML.load(testFile("key.xml")))
    assertEquals(key.value, "some-value")
  }

  @Test
  def testCreatedAtSetOK() {
    Thread.sleep(1) // Not so fast!
    assertTrue(key.createdAt.compareTo(new Date()) < 0)
  }

  @Test
  def testExpiresAtSetOK() {
    assertTrue(key.expiresAt.compareTo(new Date(key.createdAt.getTime() + (24 * 60 * 60 * 1000))) == 0)
  }

}
