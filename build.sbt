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
      val akka       = "2.5.8"
      val log4j      = "2.10.0"
      val scalaCheck = "1.13.5"
      val scalaTest  = "3.0.4"
    }
    val akkaActor   = "com.typesafe.akka"        %% "akka-actor"   % Version.akka
    val akkaTestkit = "com.typesafe.akka"        %% "akka-testkit" % Version.akka
    val log4jApi    = "org.apache.logging.log4j" % "log4j-api"     % Version.log4j
    val log4jCore   = "org.apache.logging.log4j" % "log4j-core"    % Version.log4j
    val scalaCheck  = "org.scalacheck"           %% "scalacheck"   % Version.scalaCheck
    val scalaTest   = "org.scalatest"            %% "scalatest"    % Version.scalaTest
  }

// *****************************************************************************
// Settings
// *****************************************************************************        |

lazy val settings =
commonSettings ++
gitSettings ++
scalafmtSettings ++
publishSettings

lazy val commonSettings =
  Seq(
    // scalaVersion from .travis.yml via sbt-travisci
    // scalaVersion := "2.12.3",
    organization := "de.heikoseeberger",
    organizationName := "Heiko Seeberger",
    startYear := Some(2015),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding",
      "UTF-8"
    ),
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value)
  )

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true
  )

lazy val publishSettings =
  Seq(
    homepage := Some(url("https://github.com/hseeberger/akka-log4j")),
    scmInfo := Some(
      ScmInfo(url("https://github.com/hseeberger/akka-log4j"),
              "git@github.com:hseeberger/akka-log4j.git")
    ),
    developers += Developer("hseeberger",
                            "Heiko Seeberger",
                            "mail@heikoseeberger.de",
                            url("https://github.com/hseeberger")),
    pomIncludeRepository := (_ => false)
  )
