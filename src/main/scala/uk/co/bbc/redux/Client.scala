package uk.co.bbc.redux

import scala.xml._
import scala.io.Source
import javax.imageio.ImageIO
import java.io.BufferedInputStream
import java.awt.image.BufferedImage
import org.apache.commons.httpclient._
import org.apache.commons.httpclient.methods._
import org.apache.commons.httpclient.params.HttpMethodParams

/**
 * @author ${user.name}
 */
class ClientHttpException(status:String) extends Exception(status:String) {}

class Client {

  /***

    // Get an instance of the client
    var client:Client   = new Client

    // Login to redux
    var user:User       = client.login("username", "password")

    // You now have a user and a session
    user.id               // = Int
    user.firstName        // = String
    user.lastName         // = String
    user.username         // = String
    user.email            // = String
    user.session          // = Session

    // When your finished logout or redux might flag your account as compromised!
    client.logout(user)

    // Get some content
    var content:Content = client.content("some-disk-reference", user.session)

    // And some info about that content
    content.diskReference // = String
    content.duration      // = Int (seconds)
    content.startDate     // = Date
    content.channel       // = String (e.g. "bbcone")
    content.frames        // = Boolean (has the frame collection been generated)
    content.title         // = String
    content.description   // = String
    content.seriesCrid    // = String
    content.programmeCrid // = String
    content.key           // = Key (a key for accessing media files)

    // Or just get a Key, this is a faster query
    var key:Key        = client.key("some-disk-reference", user.session)

    // Get a download url
    var mpeg2:String   = Url.mpeg2("some-disk-reference", key)
    var mpeg4:String   = Url.mpeg4("some-disk-reference", key)
    var mp3:String     = Url.mp3("some-disk-reference", key)
    var dvbsubs:String = Url.dvbsubs("some-disk-reference", key)
    var frames:String  = Url.frames("some-disk-reference", minute, key)
    var montage:String = Url.frames("some-disk-reference", key)

    // Download a file
    client.download downloadUrl, inputStream:InputStream => {
      ... code that handles InputStream ...
    }

    // Get an individual frame - uses a fairly efficient approach to
    // crop a single frame from a jpeg strip of 60
    var frame:BufferedImage = client.frame("some-disk-reference", second, key)

  ***/

  /****************************************
   * PUBLIC API
   ****************************************/

  var httpClient:HttpClient = new HttpClient

  def login (username: String, password: String) : User = {
    userRequest (Url.login(username, password), xml => User.createFromXMLResponse(xml) )
  }

  def logout (user: User) : User = {
    getRequestWithStringResponse(Url.logout(user.session.token), otherHttpException)
    user.session = null
    user
  }

  def key (diskReference:String, session:Session) : Key = {
    contentRequest (Url.key(diskReference, session.token), xml => Key.createFromXMLResponse(xml) )
  }

  def content (diskReference:String, session:Session) : Content = {
    contentRequest (Url.content(diskReference, session.token), xml => Content.createFromXMLResponse(xml) )
  }

  def frame (diskReference:String, seconds:Int, key:Key) : BufferedImage = {
    val mins:Int    = seconds / 60
    val secs:Int    = seconds - mins * 60
    getRequest(Url.frames(diskReference, mins, key), method => {
      Frame.fromInputStream(method.getResponseBodyAsStream, secs)
    }, status => status match {
      case 404 => throw new ContentNotFoundException
      case _   => otherHttpException(status)
    })

  }

  /****************************************
   * DOMAIN SPECIFIC GET REQUEST METHODS
   ****************************************/

   private def imageRequest[T] (url:String, block: BufferedImage => T) : T = {
     var response:BufferedImage = getRequestWithImageResponse(url, status => status match {
       case 404 => throw new ContentNotFoundException
       case _   => otherHttpException(status)
     })
     block(response)
   }

   private def contentRequest[T] (url:String, block: NodeSeq => T) : T = {
     var response:NodeSeq = getRequestWithXmlResponse(url, status => status match {
       case 403 => throw new SessionInvalidException
       case 404 => throw new ContentNotFoundException
       case _   => otherHttpException(status)
     })
     block(response)
   }

   private def userRequest[T] (url:String, block: NodeSeq => T) : T = {
     var response:NodeSeq = getRequestWithXmlResponse(url, status => status match {
       case 403 => throw new UserPasswordException
       case 404 => throw new UserNotFoundException
       case _   => otherHttpException(status)
     })
     block(response)
   }

   private def otherHttpException(status:Int) = {
     throw new ClientHttpException(status.toString)
   }


  /****************************************
   * GENERIC GET REQUEST METHODS
   ****************************************/

  private def getRequestWithImageResponse(url: String, error: Int => BufferedImage) : BufferedImage = {
    getRequest(url, method => {
      ImageIO.setUseCache(false)
      ImageIO.read(new BufferedInputStream(method.getResponseBodyAsStream()))
    }, error)
  }

  private def getRequestWithXmlResponse (url:String, error: Int => NodeSeq) : NodeSeq = {
    getRequest(url, method => {
      XML.load(method.getResponseBodyAsStream())
    }, error)
  }

  private def getRequestWithStringResponse (url:String, error: Int => String) : String = {
    getRequest(url, method => {
      var source:Source   = Source.fromInputStream(method.getResponseBodyAsStream(), method.getResponseCharSet())
      source.getLines().mkString("\n")
    }, error)
  }

  private def getRequest[T] (url: String, success: GetMethod => T, error: Int => T) : T = {
    val method:GetMethod = new GetMethod(url)
    val status:Int       = httpClient.executeMethod(method)
    try {
      status match {
        case 200 => success(method)
        case _   => error(status)
      }
    } finally {
      method.releaseConnection()
    }
  }

}
