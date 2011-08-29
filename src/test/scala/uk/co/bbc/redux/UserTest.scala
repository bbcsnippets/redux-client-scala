package uk.co.bbc.redux

import org.junit._
import Assert._

@Test
class UserTest {

  @Test
  def testParsesXMLOK() {
    val xml = <response>
      <user>
        <token>some-token</token>
        <id>1234</id>
        <username>testuser</username>
        <email>test.user@example.com</email>
        <first_name>Test</first_name>
        <last_name>User</last_name>
      </user>
    </response>
    var user:User = User.createFromXMLResponse(xml)
    assertEquals(user.id, 1234)
    assertEquals(user.username, "testuser")
    assertEquals(user.firstName, "Test")
    assertEquals(user.lastName, "User")
    assertEquals(user.email, "test.user@example.com")
    assertEquals(user.session.token, "some-token")
  }

}


