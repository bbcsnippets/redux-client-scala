package uk.co.bbc.redux

object Url {

  val API_HOST : String = "http://api.bbcredux.com"
  val WWW_HOST : String = "http://g.bbcredux.com"

  def login (username:String, password:String) : String = {
    API_HOST+"/user/login?username="+username+"&password="+password
  }

  def logout (token:String) : String = {
    API_HOST+"/user/logout?token="+token
  }

  def key (diskReference:String, token:String) : String = {
    API_HOST+"/content/"+diskReference+"/key?token="+token
  }

  def content (diskReference:String, token:String) : String = {
    API_HOST+"/content/"+diskReference+"/data?token="+token
  }

  def frames (diskReference:String, minute:Int, key:Key) : String = {
    val timecode:String = "%05d" format (minute * 60)
    WWW_HOST+"/programme/"+diskReference+"/download/"+key.value+"/frame-270-"+timecode+"-60.jpg"
  }

  def montage (diskReference:String, key:Key) : String = {
    WWW_HOST+"/programme/"+diskReference+"/download/"+key.value+"/frame-180-all.jpg"
  }

  def mpeg2 (diskReference:String, key:Key) : String = {
    WWW_HOST+"/programme/"+diskReference+"/download/"+key.value+"/original/"+diskReference+"_mpeg2.ts"
  }

  def mpeg4 (diskReference:String, key:Key) : String = {
    WWW_HOST+"/programme/"+diskReference+"/download/"+key.value+"/2m-mp4/"+diskReference+"_mpeg4.ts"
  }

  def mp3 (diskReference:String, key:Key) : String = {
    WWW_HOST+"/programme/"+diskReference+"/download/"+key.value+"/2m-mp4/"+diskReference+".mp3"
  }

  def dvbsubs (diskReference:String, key:Key) : String = {
    WWW_HOST+"/programme/"+diskReference+"/download/"+key.value+"/"+diskReference+"-dvbsubs.xml"
  }

}