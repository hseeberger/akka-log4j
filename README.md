# akka-log4j #

akka-log4j is a logging backend implementation for [Akka](http://akka.io) based on [Log4j 2](http://logging.apache.org/log4j/2.x).
It is an alternative to the official akka-slf4j backend which uses SLF4J.

## Installation

Grab it while it's hot:

``` scala
// All releases including intermediate ones are published here,
// final ones are also published to Maven Central.
resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies ++= List(
  "de.heikoseeberger" %% "akka-log4j" % "1.1.3",
  ...
)
```

## Usage

Configure `akka.loggers` with `de.heikoseeberger.akkalog4j.Log4jLogger`:

```
akka {
  loggers        = ["de.heikoseeberger.akkalog4j.Log4jLogger"]
  logging-filter = "de.heikoseeberger.akkalog4j.Log4jLoggingFilter"
  ...
}
```

## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license. Whether or not you state this explicitly, by submitting any copyrighted material via pull request, email, or other means you agree to license the material under the project's open source license and warrant that you have the legal authority to do so.

## License ##

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
