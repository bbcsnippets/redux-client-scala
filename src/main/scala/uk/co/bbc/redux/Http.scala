package uk.co.bbc.redux

import scala.xml._
import scala.io.Source
import org.apache.commons.httpclient._
import org.apache.commons.httpclient.methods._
import org.apache.commons.httpclient.params.HttpMethodParams

trait Http {

  var httpClient:HttpClient = new HttpClient

  /****************************************
   * DOMAIN SPECIFIC GET REQUEST METHODS
   ****************************************/

   protected def contentRequest[T] (url:String, block: NodeSeq => T) : T = {
     var response:NodeSeq = getRequestWithXmlResponse(url, status => status match {
       case 403 => throw new SessionInvalidException
       case 404 => throw new ContentNotFoundException
       case _   => otherHttpException(status)
     })
     block(response)
   }

   protected def userRequest[T] (url:String, block: NodeSeq => T) : T = {
     var response:NodeSeq = getRequestWithXmlResponse(url, status => status match {
       case 403 => throw new UserPasswordException
       case 404 => throw new UserNotFoundException
       case _   => otherHttpException(status)
     })
     block(response)
   }

   protected def otherHttpException(status:Int) = {
     throw new ClientHttpException(status.toString)
   }

  /****************************************
   * GENERIC GET REQUEST METHODS
   ****************************************/

  protected def getRequestWithXmlResponse (url:String, error: Int => NodeSeq) : NodeSeq = {
    getRequest(url, method => {
      XML.load(method.getResponseBodyAsStream())
    }, error)
  }

  protected def getRequestWithStringResponse (url:String, error: Int => String) : String = {
    getRequest(url, method => {
      var source:Source   = Source.fromInputStream(method.getResponseBodyAsStream(), method.getResponseCharSet())
      source.getLines().mkString("\n")
    }, error)
  }

  protected def getRequest[T] (url: String, success: GetMethod => T, error: Int => T) : T = {
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