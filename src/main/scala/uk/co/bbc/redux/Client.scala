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

  /****************************************
   * PUBLIC API
   ****************************************/

  val REDUX_API_HOST : String = "http://api.bbcredux.com"
  val REDUX_WWW_HOST : String = "http://g.bbcredux.com"

  var httpClient:HttpClient = new HttpClient

  def login (username: String, password: String) : User = {
    val path:String = "/user/login?username="+username+"&password="+password
    userRequest(path, xml => User.createFromXMLResponse(xml) )
  }

  def logout (user: User) : User = {
    val path:String = REDUX_API_HOST+"/user/logout?token="+user.session.token
    getRequestWithStringResponse(path, otherHttpException)
    user.session = null
    user
  }

  def key (diskReference:String, session:Session) : Key = {
    val path:String = "/content/"+diskReference+"/key?token="+session.token
    contentRequest (path, xml => Key.createFromXMLResponse(xml) )
  }

  def content (diskReference:String, session:Session) : Content = {
    val path:String = "/content/"+diskReference+"/data?token="+session.token
    contentRequest (path, xml => Content.createFromXMLResponse(xml) )
  }

  def frames (diskReference:String, minute:Int, key:Key) : Frames = {
    val secs:String = "%05d" format (minute * 60)
    val path:String = "/programme/"+diskReference+"/download/"+key.value+"/frame-270-"+secs+"-60.jpg"
    imageRequest (path, image => new Frames(image) )
  }

  /****************************************
   * DOMAIN SPECIFIC GET REQUEST METHODS
   ****************************************/

   private def imageRequest[T] (path:String, block: BufferedImage => T) : T = {
     var response:BufferedImage = getRequestWithImageResponse(REDUX_WWW_HOST+path, status => status match {
       case 404 => throw new ContentNotFoundException
       case _   => otherHttpException(status)
     })
     block(response)
   }

   private def contentRequest[T] (path:String, block: NodeSeq => T) : T = {
     var response:NodeSeq = getRequestWithXmlResponse(REDUX_API_HOST+path, status => status match {
       case 403 => throw new SessionInvalidException
       case 404 => throw new ContentNotFoundException
       case _   => otherHttpException(status)
     })
     block(response)
   }

   private def userRequest[T] (path:String, block: NodeSeq => T) : T = {
     var response:NodeSeq = getRequestWithXmlResponse(REDUX_API_HOST+path, status => status match {
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
    var method:GetMethod      = new GetMethod(url)
    var status:Int            = httpClient.executeMethod(method)
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
