package uk.co.bbc.redux

import org.junit._
import Assert._
import java.text.SimpleDateFormat

@Test
class ClientTest extends MockableHttp with TestFile {

  var client:Client   = new Client

  def session:Session = new Session("some-token")

  def user:User       = new User(1, session, "", "", "", "")

  @Test
  def testLoginParsesGoodResponse() {
    client.httpClient = mockClient(200, testFile("login.xml"))
    var user:User = client.login("i-do-exist", "good-password")
    assertEquals(user.id, 1234)
  }

  @Test
  def testLogoutWorks()  = withStatus(200, () => { client.logout(user) })

  @Test
  def testKeyWorks() {
    client.httpClient = mockClient(200, testFile("key.xml"))
    var key:Key = client.key("blah", session)
    assertEquals(key.value, "some-value")
  }

  @Test
  def testContentWorks() {
    client.httpClient = mockClient(200, testFile("content.xml"))
    var content:Content = client.content("blah", session)
    assertEquals(content.key.value, "some-value")
    assertEquals(content.title, "Top Gear")
  }

  @Test
  def testDownloadWorks() {
    client.httpClient = mockClient(200, testFile("frame_collection.jpg"))
    client.download("http://blah/file", stream => {})
  }

  @Test
  def testFrameWorks() {
    client.httpClient = mockClient(200, testFile("frame_collection.jpg"))
    var frame = client.frame("blah", 1, new Key("som-key"))
    assertTrue(frame.getWidth == 480)
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

  @Test(expected = classOf[ClientHttpException])
  def htmlThrowsExceptionWithBadResponse              = withStatus(404, () => { client.html("http://g.bbcredux.com", session) })

  @Test(expected = classOf[SessionInvalidException])
  def htmlThrowsSessionInvalidException               {
    client.httpClient = mockClient(200, testFile("login.html"))
    client.html("http://g.bbcredux.com", session)
  }

  @Test
  def scheduleWorks {
    client.httpClient = mockClient(200, testFile("tv_schedule.html"))
    val resp = client.tvSchedule(new SimpleDateFormat("yyyy-MM-dd").parse("2011-02-01"), session)
    assertEquals(resp.length, 201)
  }

  def withStatus(status:Int, callback: () => _) {
    client.httpClient = mockClient(status, "")
    callback()
  }

}
