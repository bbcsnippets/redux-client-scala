package uk.co.bbc.redux

import org.junit._
import Assert._
import scala.xml._

@Test
class UserTest extends TestFile {

  @Test
  def testParsesXMLOK() {
    var user:User = User.createFromXMLResponse(XML.load(testFile("login.xml")))
    assertEquals(user.id, 1234)
    assertEquals(user.username, "testuser")
    assertEquals(user.firstName, "Test")
    assertEquals(user.lastName, "User")
    assertEquals(user.email, "test.user@example.com")
    assertEquals(user.session.token, "some-token")
  }

}


