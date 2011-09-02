package uk.co.bbc.redux

import org.junit._
import Assert._
import java.text.SimpleDateFormat

@Test
class UrlTest {

  val token   = "some-token"
  val key     = new Key("some-key")

  @Test
  def apiHost = assertEquals("http://api.bbcredux.com", Url.API_HOST)

  @Test
  def wwwHost = assertEquals("http://g.bbcredux.com", Url.WWW_HOST)

  @Test
  def login   = assertEquals(Url.API_HOST+"/user/login?username=foo&password=bar", Url.login("foo", "bar"))

  @Test
  def logout  = assertEquals(Url.API_HOST+"/user/logout?token="+token, Url.logout(token))

  @Test
  def _key    = assertEquals(Url.API_HOST+"/content/foo/key?token="+token, Url.key("foo", token))

  @Test
  def content = assertEquals(Url.API_HOST+"/content/foo/data?token="+token, Url.content("foo", token))

  @Test
  def frames  = assertEquals(Url.WWW_HOST+"/programme/foo/download/"+key.value+"/frame-270-00120-60.jpg", Url.frames("foo", 2, key))

  @Test
  def montage = assertEquals(Url.WWW_HOST+"/programme/foo/download/"+key.value+"/frame-180-all.jpg", Url.montage("foo", key))

  @Test
  def mpeg2   = assertEquals(Url.WWW_HOST+"/programme/foo/download/"+key.value+"/original/foo_mpeg2.ts", Url.mpeg2("foo", key))

  @Test
  def mpeg4   = assertEquals(Url.WWW_HOST+"/programme/foo/download/"+key.value+"/2m-mp4/foo_mpeg4.ts", Url.mpeg4("foo", key))

  @Test
  def mp3     = assertEquals(Url.WWW_HOST+"/programme/foo/download/"+key.value+"/2m-mp4/foo.mp3", Url.mp3("foo", key))

  @Test
  def dvbsubs = assertEquals(Url.WWW_HOST+"/programme/foo/download/"+key.value+"/foo-dvbsubs.xml", Url.dvbsubs("foo", key))

  @Test
  def tv      = assertEquals(Url.WWW_HOST+"/day/2011-02-01", Url.tv(new SimpleDateFormat("yyyy-MM-dd").parse("2011-02-01"), token))

}
