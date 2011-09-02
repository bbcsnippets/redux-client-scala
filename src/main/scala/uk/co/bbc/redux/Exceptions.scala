package uk.co.bbc.redux

/**
 * Wrong username
 */
class UserNotFoundException extends Exception

/**
 * Wrong password
 */
class UserPasswordException extends Exception

/**
 * Redux has marked you account as "comprimised". Too many concurrent logins
 */
class UserAccountLockedException extends Exception

/**
 * You session token was rejected
 */
class SessionInvalidException extends Exception

/**
 * You tried to get some data or a key for a disk reference that is unavailable
 */
class ContentNotFoundException extends Exception

/**
 * An HTTP error occurred
 */
class ClientHttpException(status:String) extends Exception(status:String)

/**
 * You tried to generate a frame from a strip that doesn't have it
 */
class FrameNotFoundException extends Exception

/**
 * The file you tried to donwload does not exist
 */
class DownloadNotFoundException extends Exception
