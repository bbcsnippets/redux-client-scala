# redux-scala-client

A scala lib to help navigate BBC Redux API's and to screen scrape where an API does not exist.

If you're reading this and you're not a BBC developer or authorised contractor then it probably won't make much sense.

BBC Snippets and BBC Redux are tools designed to allow BBC staff to develop new ways to view and navigate content. As such, they're not open to the public.

If you've been contracted by the BBC to do work in this area please feel free to message us.

## Installation

### Maven

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

### SBT

    resolvers += "BBC Snippets Releases" at "http://bbcsnippets.github.com/redux-client-scala/maven2"
    libraryDependencies += "uk.co.bbc" % "redux-client-scala" % {VERSION}

## Usage

### Users and sessions

You need to use the client to login into Redux, this will return you a `User` object with an associated `Session` that you need to use to make further requests ...

    // Get an instance of the client
    val client:Client   = new Client

    // Login to redux
    val user:User       = client.login("username", "password")

    // You now have a user and a session
    user.id               // = Int
    user.firstName        // = String
    user.lastName         // = String
    user.username         // = String
    user.email            // = String
    user.session          // = Session

    // When your finished logout or redux might flag your account as compromised!
    client.logout(user)

May throw any one of the following errors ...

    uk.co.bbc.redux.UserNotFoundException // Bad username
    uk.co.bbc.redux.UserPasswordException // Bad password
    uk.co.bbc.redux.ClientHttpException   // Some other HTTP error

### Getting some data

You can retrieve data from Redux ...

    val data  = client.content("5286433008768041518", user.session)

    data.diskReference // = String
    data.duration      // = Int (seconds)
    data.startDate     // = Date
    data.channel       // = String (e.g. "bbcone")
    data.frames        // = Boolean (has the frame collection been generated)
    data.title         // = String
    data.description   // = String
    data.seriesCrid    // = String
    data.programmeCrid // = String
    data.key           // = Key (a key for accessing media files)

May throw any one of the following errors ...

    uk.co.bbc.redux.ContentNotFoundException // Bad disk reference (or for some reason Redux cannot find it)
    uk.co.bbc.redux.SessionInvalidException  // Your session has expired / become invalid
    uk.co.bbc.redux.ClientHttpException      // Some other HTTP error

### Keys

Keys are required to access any media files on Redux, a key is returned each time you call client.content, but a slightly faster API call is available ...

    val key = client.key("5286433008768041518", user.session)

May throw any one of the following errors ...

    uk.co.bbc.redux.ContentNotFoundException // Bad disk reference (or for some reason Redux cannot find it)
    uk.co.bbc.redux.SessionInvalidException  // Your session has expired / become invalid
    uk.co.bbc.redux.ClientHttpException      // Some other HTTP error

A key should be valid for ~24 hours, there are some helper methods around this

    key.createdAt  // Date
    key.expiresAt  // Date

### Getting some files

You can download files from redux, available urls are mpeg2, mpeg4, mp3, flv, h264Lo, h264Hi, dvbsubs. You can use the Url.* functions to build a URL and use within your own code, or you can use it with client.download to parse the response stream.

    val key       = client.key("5286433008768041518", user.session)
    val url       = Url.flv("5286433008768041518", key)
    client.download url, inputStream => {
      ... code that reads and closes the InputStream ...
    }

May throw any one of the following errors ...

    uk.co.bbc.redux.SessionInvalidException  // Your session has expired / become invalid
    uk.co.bbc.redux.ClientHttpException      // Some other HTTP error


### Getting a schedule

Currently you can only retrieve a TV Schedule and to do that we rely on screen scraping Redux. Also it's not so much a schedule, rather an array of disk references broadcast on a particular date.

    schedule = client.tvSchedule(date, user.session)

    schedule foreach {
      diskReference:String => client.content(diskReference, user.session)
    }

May throw any one of the following errors ...

    uk.co.bbc.redux.SessionInvalidException  // Your session has expired / become invalid
    uk.co.bbc.redux.ClientHttpException      // Some other HTTP error


### Frames


    // Get an individual frame - uses a fairly efficient approach to
    // crop a single frame from a jpeg strip of 60
    var frame:BufferedImage = client.frame("some-disk-reference", second, key)

    // Get a montage of frames (1 farme per 20 secs)
    var montage:BufferedImage = client.montage("some-disk-reference", key)

### Shutdown

When instance is no longer needed, shut down connection manager to ensure immediate deallocation of all system resources

    client.httpClient.getHttpConnectionManager().shutdown()

### Scala Docs

Docs are available at http://bbcsnippets.github.com/redux-client-scala/scaladocs/

## Caveats / Known Issues

### Using a proxy server

The Client uses the Apache commons HTTP Client 3.x, you can access the underlying httpClient instance to set proxies or connection managers, etc.

      client.httpClient.getHostConfiguration().setProxy("www-cache.reith.bbc.co.uk", 80);

### Password is sent to Redux unencrypted

Redux has no HTTPS, also for some reason the API call for logging in is a GET request.

### "Your account has been comprimised"

You might get your account locked if you repeatedly login, especially from multiple IP's. To be on the safe side you should reuse the user and user.session object throughout you application (though beware it may time out with several hours inactivity).

Also, always remember to use client.logout(user.session) when you are finished.

### No radio schedule / disk reference lists

The schedule list relies on screen scraping Redux, unfortunately we can't get the disk references for radio in a single request (it's n+1 requests, where n = number of radio shows in a day! See Redux radio schedule pages HTML for the problem).


## Development

Please send new code in the form of a pull requests with tests. Run the current test suite with ...

    mvn test  # Runs junit test suite
    mvn scala:script -DscriptFile=scripts/sanity_test.scala  # Does an integration test of codebase + redux

## Release

Add this config to your `~/.m2/settings.xml`:

    <servers>
      <server>
        <id>github</id>
        <username>YOUR-USERNAME</username>
        <password>YOUR-PASSWORD</password>
        <token>YOUR-OAUTH2-TOKEN</token>
      </server>
    </servers>

 Now run the release tasks:

    mvn release:prepare
    mvn release:perform
