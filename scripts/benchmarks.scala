import uk.co.bbc.redux._
import java.util.Date
import java.awt.image.BufferedImage
import com.thebuzzmedia.imgscalr._

var username:String = Console.readLine("Redux User: ")
var password:String = Console.readLine("Redux Pass: ")

var client:Client   = new Client
var user:User       = client.login(username, password)
var content:Content = client.content("5110029682485761909", user.session)
var frames:Frames   = client.frames(content.diskReference, 1, content.key)

def benchmark (description:String) (block: => Unit) {
  var start:Date = new Date
  block
  var time:Long = (new Date).getTime() - start.getTime()
  println(description+" took : "+time.toString+" ms")
}

benchmark ("Loading frames object from Redux API") {
  frames = client.frames(content.diskReference, 1, content.key)
}

benchmark ("Get "+frames.seconds.toString+" frames from a Frames object") {
  for (i <- 0 until frames.seconds) {
    var frame:BufferedImage = frames.getFrame(i)
  }
}

benchmark ("Scale by 0.5, "+frames.seconds.toString+" frames from a Frames object") {
  for (i <- 0 until frames.seconds) {
    var frame:BufferedImage = frames.getFrame(i)
    Scalr.resize(frame, Scalr.Method.AUTOMATIC, frames.FRAME_WIDTH / 2, frames.FRAME_HEIGHT / 2, null)
  }
}

client.logout(user)
