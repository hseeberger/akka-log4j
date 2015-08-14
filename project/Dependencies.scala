import sbt._

object Version {
  val akka       = "2.4-M3"
  val log4j      = "2.3"
  val scala      = "2.11.7"
  val scalaCheck = "1.12.4"
  val scalaTest  = "2.2.5"
}

object Library {
  val akkaActor   = "com.typesafe.akka"        %% "akka-actor"   % Version.akka
  val akkaTestkit = "com.typesafe.akka"        %% "akka-testkit" % Version.akka
  val log4jApi    = "org.apache.logging.log4j" %  "log4j-api"    % Version.log4j
  val log4jCore   = "org.apache.logging.log4j" %  "log4j-core"   % Version.log4j
  val scalaCheck  = "org.scalacheck"           %% "scalacheck"   % Version.scalaCheck
  val scalaTest   = "org.scalatest"            %% "scalatest"    % Version.scalaTest
}
