package uk.co.bbc.redux

import scala.xml._
import scala.io.Source
import java.io.InputStreamReader
import org.apache.commons.httpclient._
import org.apache.commons.httpclient.methods._
import org.apache.commons.httpclient.params.HttpMethodParams

/**
 * @author ${user.name}
 */
class ClientHttpException(status:String) extends Exception(status:String) {}

class Client {

  val REDUX_HOST : String = "http://api.bbcredux.com"

  var httpClient:HttpClient = new HttpClient

  def login (username: String, password: String) : User = {
    var (status:Int, body:String) = getRequestWithStringBody("/user/login?username="+username+"&password="+password)
    userOrError(status, () => { User.createFromXMLResponse(XML.loadString(body)) })
  }

  def logout (user: User) : User = {
    var (status:Int, _) = getRequestWithStringBody("/user/logout?token="+user.session.token)
    userOrError(status, () => { user.session = null; user })
  }

  def key (diskReference:String, session:Session) : Key = {
    var (status:Int, body:String) = getRequestWithStringBody("/content/"+diskReference+"/key?token="+session.token)
    contentOrError(status, () => { Key.createFromXMLResponse(XML.loadString(body)) })
  }

  def content (diskReference:String, session:Session) : Content = {
    var (status:Int, body:String) = getRequestWithStringBody("/content/"+diskReference+"/data?token="+session.token)
    contentOrError(status, () => { Content.createFromXMLResponse(XML.loadString(body)) })
  }

  private def getRequestWithStringBody(path: String) : (Int, String) = {
    var url:String            = REDUX_HOST+path
    var method:GetMethod      = new GetMethod(url)
    var status:Int            = httpClient.executeMethod(method)
    try {
      var source:Source   = Source.fromInputStream(method.getResponseBodyAsStream(), method.getResponseCharSet())
      var response:String = source.getLines().mkString("\n")
      (status, response)
    } finally {
      method.releaseConnection()
    }
  }

  private def userOrError(status:Int, user: () => User) : User = status match {
    case 200 => user()
    case 403 => throw new UserPasswordException
    case 404 => throw new UserNotFoundException
    case _   => otherHttpException(status)
  }

  private def contentOrError[T](status:Int, block: () => T) : T = status match {
    case 200 => block()
    case 403 => throw new SessionInvalidException
    case 404 => throw new ContentNotFoundException
    case _   => otherHttpException(status)
  }

  private def otherHttpException(status:Int) = {
    throw new ClientHttpException(status.toString)
  }

}
