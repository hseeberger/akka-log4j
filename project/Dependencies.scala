import sbt._

object Version {
  final val Akka       = "2.4.9"
  final val Log4j      = "2.6.2"
  final val Scala      = "2.11.8"
  final val ScalaCheck = "1.13.1"
  final val ScalaTest  = "3.0.0"
}

object Library {
  val akkaActor   = "com.typesafe.akka"        %% "akka-actor"   % Version.Akka
  val akkaTestkit = "com.typesafe.akka"        %% "akka-testkit" % Version.Akka
  val log4jApi    = "org.apache.logging.log4j" %  "log4j-api"    % Version.Log4j
  val log4jCore   = "org.apache.logging.log4j" %  "log4j-core"   % Version.Log4j
  val scalaCheck  = "org.scalacheck"           %% "scalacheck"   % Version.ScalaCheck
  val scalaTest   = "org.scalatest"            %% "scalatest"    % Version.ScalaTest
}
