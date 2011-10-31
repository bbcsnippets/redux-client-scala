import Console._
import java.text.SimpleDateFormat
import java.io.InputStream
import uk.co.bbc.redux._

val username = Console.readLine("Enter your Redux username:")
val password = Console.readLine("Enter your Redux password:")

// Get an instance of the client
val client:Client   = new Client

// HTTP Proxy stuff
if (System.getenv().get("http_proxy") != null) {
  client.httpClient.getHostConfiguration().setProxy("www-cache.reith.bbc.co.uk", 80);
}

// Login to redux
val user:User       = client.login(username, password)

// You now have a user and a session
println("\nUser details ...")
println(user.id)               // = Int
println(user.firstName)        // = String
println(user.lastName)         // = String
println(user.username)         // = String
println(user.email)            // = String
println(user.session)          // = Session

// Get data
val data  = client.content("5286433008768041518", user.session)

println("\nData details ...")
println(data.diskReference) // = String
println(data.duration)      // = Int (seconds)
println(data.startDate)     // = Date
println(data.channel)       // = String (e.g. "bbcone")
println(data.frames)        // = Boolean (has the frame collection been generated)
println(data.title)         // = String
println(data.description)   // = String
println(data.seriesCrid)    // = String
println(data.programmeCrid) // = String
println(data.key)           // = Key (a key for accessing media files)

// Get Key
val key = client.key("5286433008768041518", user.session)

// Get download
val url       = Url.montage("5286433008768041518", key)

client.download(url, inputStream => inputStream.close)

println("\nKey details ...")
println(key)

// Get schedule
val schedule = client.tvSchedule(new SimpleDateFormat("yyyy-MM-dd").parse("2011-02-01"), user.session)
println("\nA disk ref from the schedule ...")
println(schedule.head)

// When your finished logout or redux might flag your account as compromised!
client.logout(user)

