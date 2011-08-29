package uk.co.bbc.redux

import java.util.Date

class SessionInvalidException extends Exception

class Session (val token:String) {

  val createdAt:Date = new java.util.Date

}
