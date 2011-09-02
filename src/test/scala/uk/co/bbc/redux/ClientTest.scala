package uk.co.bbc.redux

import org.junit._
import Assert._
import org.apache.commons.httpclient.HttpClientMock;

@Test
class ClientTest {

  var client:Client = new Client

  def session:Session = {
    new Session("some-token")
  }

  def user:User = {
    new User(1, session, "", "", "", "")
  }

  @Test
  def testLoginParsesGoodResponse() {
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
    client.httpClient = new HttpClientMock(200, xml.toString)
    var user:User = client.login("i-do-exist", "good-password")
    assertEquals(user.id, 1234)
  }

  @Test
  def testLogoutWorks() {
    client.httpClient = new HttpClientMock(200, "")
    client.logout(user)
  }

  @Test
  def testKeyWorks() {
    val xml = <response>
      <programme>
        <key>some-value</key>
      </programme>
    </response>
    client.httpClient = new HttpClientMock(200, xml.toString)
    var key:Key = client.key("blah", session)
    assertEquals(key.value, "some-value")
  }

  @Test
  def testContentWorks() {
    val xml = <response>
      <programme frames="1">
        <channel>bbctwo</channel>
        <reference>5554695377586026009</reference>
        <duration>3900</duration>
        <unixtime>1293303300</unixtime>
        <description>1/2. Jeremy Clarkson, Richard Hammond and James May compare the Mercedes SLS, the Porsche 911 GT3 RS and the Ferrari 458 Italia in an epic road trip up America's east coast. [S]</description>
        <series_crid>foo</series_crid>
        <programme_crid>bar</programme_crid>
        <name>Top Gear</name>
        <key>some-value</key>
      </programme>
    </response>
    client.httpClient = new HttpClientMock(200, xml.toString)
    var content:Content = client.content("blah", session)
    assertEquals(content.key.value, "some-value")
    assertEquals(content.title, "Top Gear")
  }


  @Test(expected = classOf[UserNotFoundException])
  def loginThrowsUserErrorWithInvalidUserNameResponse = withStatus(404, () => { client.login("i-dont-exist", "good-password") })

  @Test(expected = classOf[UserPasswordException])
  def loginThrowsUserErrorWithInvalidPasswordResponse = withStatus(403, () => { client.login("i-do-exist", "rubbish-password") })

  @Test(expected = classOf[ClientHttpException])
  def loginThrowsClientHttpExceptionWithBadResponse   = withStatus(500, () => { client.login("i-do-exist", "good-password") })

  @Test(expected = classOf[ClientHttpException])
  def logoutThrowsExceptionWithBadResponse            = withStatus(500, () => { client.logout(user) })

  @Test(expected = classOf[ClientHttpException])
  def keyThrowsExceptionWithBadResponse               = withStatus(500, () => { client.key("", session) })

  @Test(expected = classOf[ClientHttpException])
  def contentThrowsExceptionWithBadResponse           = withStatus(500, () => { client.content("", session) })

  @Test(expected = classOf[ContentNotFoundException])
  def keyThrowsNotFoundException                      = withStatus(404, () => { client.key("", session) })

  @Test(expected = classOf[ContentNotFoundException])
  def contentThrowsNotFoundException                  = withStatus(404, () => { client.content("", session) })

  @Test(expected = classOf[DownloadNotFoundException])
  def framesThrowsNotFoundException                   = withStatus(404, () => { client.frame("", 1, new Key("some-key")) })

  @Test(expected = classOf[DownloadNotFoundException])
  def downloadThrowsNotFoundException                 = withStatus(404, () => { client.download("http://g.bbcredux.com/", stream => stream) })

  @Test(expected = classOf[SessionInvalidException])
  def keyThrowsSessionInvalidException                = withStatus(403, () => { client.key("", session) })

  @Test(expected = classOf[SessionInvalidException])
  def contentSessionInvalidException                  = withStatus(403, () => { client.content("", session) })

  def withStatus(status:Int, callback: () => _) {
    client.httpClient = new HttpClientMock(status, "")
    callback()
  }


}


