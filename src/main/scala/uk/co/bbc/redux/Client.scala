package uk.co.bbc.redux

import java.util.Date
import java.io.InputStream
import java.io.BufferedInputStream
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import scala.xml._
import scala.xml.factory.XMLLoader

class Client extends Http {

  var htmlParser:XMLLoader[Elem] = XML.withSAXParser(new org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl().newSAXParser())

  /** Login to redux
   *
   * This is done over HTTP, redux doesn't have HTTPS !!
   *
   * Don't abuse this, redux is very overzealous when it comes to
   * locking your account if it sees to many logins especially from
   * multiple IPs. Always use the logout method when finished or reuse
   * an existing session.
   *
   * @param username Your redux username
   * @param password Your redux password
   * @throws UserNotFoundException Your username cannot be found
   * @throws UserPasswordException Your password is wrong
   * @throws ClientHttpException   Some over HTTP error has occured
   * @return a new User instance with an associated Session
   */
  def login (username: String, password: String) : User = {
    userRequest (Url.login(username, password), xml => User.createFromXMLResponse(xml) )
  }

  /**
   * @param user A User with associated and valid Session
   * @throws ClientHttpException   Some over HTTP error has occured
   */
  def logout (user: User) : Unit = {
    getRequestWithStringResponse (Url.logout(user.session.token), otherHttpException)
  }

  /**
   * @param diskReference An identifier for the content
   * @param session A valid Session
   * @throws ContentNotFoundException The diskReference cannot be found
   * @throws SessionInvalidException  The session token is broken
   * @throws ClientHttpException      Some over HTTP error has occured
   */
  def key (diskReference:String, session:Session) : Key = {
    contentRequest (Url.key(diskReference, session.token), xml => Key.createFromXMLResponse(xml) )
  }

  /**
   * @param diskReference An identifier for the content
   * @param session A valid Session
   * @throws ContentNotFoundException The diskReference cannot be found
   * @throws SessionInvalidException  The session token is broken
   * @throws ClientHttpException      Some over HTTP error has occured
   */
  def content (diskReference:String, session:Session) : Content = {
    contentRequest (Url.content(diskReference, session.token), xml => Content.createFromXMLResponse(xml) )
  }

  /** Download some file from redux
   *
   * Pass a block to this method to handle the download. The block is passed the
   * HTTP response body as an InputStream which can be read however you like.
   *
   * The stream needs closing afterwards.
   *
   * @param url The url for the download file
   * @param block A block that takes an InputStream as a param
   * @throws DownloadNotFoundException The requested file cannot be found
   * @throws ClientHttpException      Some over HTTP error has occured
   */
  def download[T] (url: String, block: InputStream => T) : T = {
    getRequest(url, method => block(method.getResponseBodyAsStream), status => status match {
      case 404 => throw new DownloadNotFoundException
      case _   => otherHttpException(status)
    })
  }

  /**
   * Retreive a montage of frames from a programme (frequency 1 frame every 20 secs)
   *
   * @param diskReference An identifier for the content
   * @param key A key for the content
   * @throws DownloadNotFoundException The requested file cannot be found
   * @throws ClientHttpException      Some over HTTP error has occured
   */
  def montage (diskReference:String, key:Key) : BufferedImage = {
    download(Url.montage(diskReference, key), stream => ImageIO.read(stream))
  }

  /**
   * Generate a single frame from a frames download
   *
   * This uses a failry memory efficient approach to crop a single frame from a
   * strip of 60. It is returned as a BufferedImage.
   *
   * @param diskReference An identifier for the content
   * @param seconds Number of seconds into the content to get frame for
   * @param key A key for the content
   * @throws DownloadNotFoundException The requested file cannot be found
   * @throws FrameNotFoundException   Cannot find requested frame (i.e. you asked for something out of duration)
   * @throws ClientHttpException      Some over HTTP error has occured
   */
  def frame (diskReference:String, seconds:Int, key:Key) : BufferedImage = {
    val mins:Int    = seconds / 60
    val secs:Int    = seconds - mins * 60
    download(Url.frames(diskReference, mins, key), stream => Frame.fromInputStream(stream, secs))
  }

  /**
   * Retreive a list of disk references for a date (i.e. a "schedule")
   *
   * @param date A Java date object
   * @param session A valid Session
   * @throws SessionInvalidException  The session token is broken
   * @throws ClientHttpException      Some over HTTP error has occured
   */
  def tvSchedule (date:Date, session:Session) : Seq[String] = {
    val xml = html(Url.tv(date), session)
    Schedule.createFromXMLResponse(xml)
  }

  /**
   * Get an arbitary page from redux and return as NodeSeq ready for XML parsing
   *
   * @param url A url to download
   * @param session A valid Session
   * @throws SessionInvalidException  The session token is broken
   * @throws ClientHttpException      Some over HTTP error has occured
   */
  def html (url:String, session:Session) : NodeSeq = {
    val response:NodeSeq = getRequest(url, "BBC_video="+session.token, method => {
      htmlParser.load(method.getResponseBodyAsStream())
    }, otherHttpException)

    if (response \\ "@name" exists { _.text == "dologin" }) {
      throw new SessionInvalidException
    }

    response
  }

}
