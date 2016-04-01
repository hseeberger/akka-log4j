import sbt._

object Version {
  final val Akka       = "2.4.3"
  final val Log4j      = "2.5"
  final val Scala      = "2.11.8"
  final val ScalaCheck = "1.12.5"
  final val ScalaTest  = "2.2.6"
}

object Library {
  val akkaActor   = "com.typesafe.akka"        %% "akka-actor"   % Version.Akka
  val akkaTestkit = "com.typesafe.akka"        %% "akka-testkit" % Version.Akka
  val log4jApi    = "org.apache.logging.log4j" %  "log4j-api"    % Version.Log4j
  val log4jCore   = "org.apache.logging.log4j" %  "log4j-core"   % Version.Log4j
  val scalaCheck  = "org.scalacheck"           %% "scalacheck"   % Version.ScalaCheck
  val scalaTest   = "org.scalatest"            %% "scalatest"    % Version.ScalaTest
}
