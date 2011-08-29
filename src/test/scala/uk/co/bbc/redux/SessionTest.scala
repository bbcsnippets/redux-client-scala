package uk.co.bbc.redux

import org.junit._
import Assert._
import java.util.Date

@Test
class SessionTest {

  var session:Session = new Session("some-token")

  @Test
  def testCreatedAtSetOK() {
    Thread.sleep(1) // Not so fast!
    assertTrue(session.createdAt.compareTo(new Date()) < 0)
  }

}


