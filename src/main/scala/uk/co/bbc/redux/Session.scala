package uk.co.bbc.redux

import java.util.Date

class SessionInvalidException extends Exception

class Session (var token:String) {

  var createdAt:Date = new java.util.Date

}
