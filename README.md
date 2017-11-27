# akka-log4j #

[![Build Status](https://travis-ci.org/hseeberger/akka-log4j.svg?branch=master)](https://travis-ci.org/hseeberger/akka-log4j)
[![Maven Central](https://img.shields.io/maven-central/v/de.heikoseeberger/akka-log4j_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/de.heikoseeberger/akka-log4j_2.12)

akka-log4j is a logging backend implementation for [Akka](http://akka.io) based on [Log4j 2](http://logging.apache.org/log4j/2.x).
It is an alternative to the official akka-slf4j backend which uses SLF4J.

## Installation

Grab it while it's hot:

``` scala
// All releases including intermediate ones are published here,
// final ones are also published to Maven Central.
resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies ++= Seq(
  "de.heikoseeberger" %% "akka-log4j" % "1.6.0",
  ...
)
```

## Usage

Configure `akka.loggers` and `akka.logging-filter`:

```
akka {
  loggers        = [de.heikoseeberger.akkalog4j.Log4jLogger]
  logging-filter = de.heikoseeberger.akkalog4j.Log4jLoggingFilter
  ...
}
```

## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license. Whether or not you state this explicitly, by submitting any copyrighted material via pull request, email, or other means you agree to license the material under the project's open source license and warrant that you have the legal authority to do so.

## License ##

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
