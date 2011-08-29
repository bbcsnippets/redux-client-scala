package uk.co.bbc.redux

import scala.xml._

/**
 * @author ${user.name}
 */

class UserException extends Exception
class UserNotFoundException extends UserException
class UserPasswordException extends UserException
class User (var id:Int, var session:Session, var username:String, var firstName:String, var lastName:String, var email:String)

object User {
  def createFromXMLResponse(xml:NodeSeq):User = {
    var details:NodeSeq  = xml \\ "response" \\ "user"
    var id:Int           = (details \ "id").text.toInt
    var token:String     = (details \ "token").text
    var username:String  = (details \ "username").text
    var firstName:String = (details \ "first_name").text
    var lastName:String  = (details \ "last_name").text
    var email:String     = (details \ "email").text
    var session:Session  = new Session(token)
    new User(id, session, username, firstName, lastName, email)
  }
}