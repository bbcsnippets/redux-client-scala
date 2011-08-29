package uk.co.bbc.redux

import org.junit._
import Assert._
import java.util.Date

@Test
class KeyTest {

  var key:Key = new Key("some-key")

  @Test
  def testParsesXMLOK() {
    val xml = <response>
      <programme>
        <key>some-key</key>
      </programme>
    </response>
    var key:Key = Key.createFromXMLResponse(xml)
    assertEquals(key.value, "some-key")
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
