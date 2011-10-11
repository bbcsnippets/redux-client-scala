# redux-scala-client

## Maven install

Latest version number at: http://bbcsnippets.github.com/redux-client-scala/project-reports.html

    <repositories>
      <repository>
        <id>Redux Client Repo</id>
        <url>http://bbcsnippets.github.com/redux-client-scala/maven2</url>
      </repository>
    </repositories>

    <dependency>
       <groupId>uk.co.bbc</groupId>
       <artifactId>redux-client-scala</artifactId>
       <version>${latest}</version>
    </dependency>

## Scala Docs

http://bbcsnippets.github.com/redux-client-scala/scaladocs/

## Quick Start

    // Get an instance of the client
    var client:Client   = new Client

    // Login to redux
    var user:User       = client.login("username", "password")

    // You now have a user and a session
    user.id               // = Int
    user.firstName        // = String
    user.lastName         // = String
    user.username         // = String
    user.email            // = String
    user.session          // = Session

    // When your finished logout or redux might flag your account as compromised!
    client.logout(user)

    // Get some content
    var content:Content = client.content("some-disk-reference", user.session)

    // And some info about that content
    content.diskReference // = String
    content.duration      // = Int (seconds)
    content.startDate     // = Date
    content.channel       // = String (e.g. "bbcone")
    content.frames        // = Boolean (has the frame collection been generated)
    content.title         // = String
    content.description   // = String
    content.seriesCrid    // = String
    content.programmeCrid // = String
    content.key           // = Key (a key for accessing media files)

    // Or just get a Key, this is a faster query
    var key:Key        = client.key("some-disk-reference", user.session)

    // Get a download url
    var mpeg2:String   = Url.mpeg2("some-disk-reference", key)
    var mpeg4:String   = Url.mpeg4("some-disk-reference", key)
    var mp3:String     = Url.mp3("some-disk-reference", key)
    var dvbsubs:String = Url.dvbsubs("some-disk-reference", key)
    var frames:String  = Url.frames("some-disk-reference", minute, key)
    var montage:String = Url.montage("some-disk-reference", key)

    // Download a file
    client.download downloadUrl, inputStream:InputStream => {
      ... code that reads and closes the InputStream ...
    }

    // Get an individual frame - uses a fairly efficient approach to
    // crop a single frame from a jpeg strip of 60
    var frame:BufferedImage = client.frame("some-disk-reference", second, key)

    // Get schedule for tv / radio data
    var schedule:Schedule   = client.tvSchedule(date, user.session)

    // And iteregate the schedule
    schedule.channels             // = List ("bbcone", "bbctwo", etc)
    schedule.channel("bbcone")    // = List ("some-disk-reference", "another-disk-reference", etc)


## Working with a proxy


