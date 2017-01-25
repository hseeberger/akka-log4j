// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `akka-log4j` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin, GitVersioning)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.akkaActor,
        library.log4jApi,
        library.akkaTestkit % Test,
        library.log4jCore   % Test,
        library.scalaCheck  % Test,
        library.scalaTest   % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val akka       = "2.4.16"
      val log4j      = "2.8"
      val scala      = "2.12.1"
      val scalaCheck = "1.13.4"
      val scalaTest  = "3.0.1"
    }
    val akkaActor   = "com.typesafe.akka"        %% "akka-actor"   % Version.akka
    val akkaTestkit = "com.typesafe.akka"        %% "akka-testkit" % Version.akka
    val log4jApi    = "org.apache.logging.log4j" %  "log4j-api"    % Version.log4j
    val log4jCore   = "org.apache.logging.log4j" %  "log4j-core"   % Version.log4j
    val scalaCheck  = "org.scalacheck"           %% "scalacheck"   % Version.scalaCheck
    val scalaTest   = "org.scalatest"            %% "scalatest"    % Version.scalaTest
}

// *****************************************************************************
// Settings
// *****************************************************************************        |

lazy val settings =
  commonSettings ++
  scalafmtSettings ++
  gitSettings ++
  headerSettings ++
  sonatypeSettings

lazy val commonSettings =
  Seq(
    // scalaVersion from .travis.yml
    // crossScalaVersions from .travis.yml
    organization := "de.heikoseeberger",
    licenses += ("Apache 2.0",
                 url("http://www.apache.org/licenses/LICENSE-2.0")),
    mappings.in(Compile, packageBin) +=
      baseDirectory.in(ThisBuild).value / "LICENSE" -> "LICENSE",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8"
    ),
    javacOptions ++= Seq(
      "-source", "1.8",
      "-target", "1.8"
    ),
    unmanagedSourceDirectories.in(Compile) :=
      Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) :=
      Seq(scalaSource.in(Test).value)
)

lazy val scalafmtSettings =
  reformatOnCompileSettings ++
  Seq(
    formatSbtFiles := false,
    scalafmtConfig :=
      Some(baseDirectory.in(ThisBuild).value / ".scalafmt.conf"),
    ivyScala :=
      ivyScala.value.map(_.copy(overrideScalaVersion = sbtPlugin.value)) // TODO Remove once this workaround no longer needed (https://github.com/sbt/sbt/issues/2786)!
  )

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

import de.heikoseeberger.sbtheader.license.Apache2_0
lazy val headerSettings =
  Seq(
    headers := Map("scala" -> Apache2_0("2015", "Heiko Seeberger"))
  )

lazy val sonatypeSettings =
  Seq(
    homepage := Some(url("https://github.com/hseeberger/akka-log4j")),
    scmInfo := Some(ScmInfo(url("https://github.com/hseeberger/akka-log4j"),
                            "git@github.com:hseeberger/akka-log4j.git")),
    developers += Developer("hseeberger",
                            "Heiko Seeberger",
                            "mail@heikoseeberger.de",
                            url("https://github.com/hseeberger")),
    pomIncludeRepository := (_ => false)
  )
