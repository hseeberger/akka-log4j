lazy val akkaLog4j = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin, GitVersioning)

name := "akka-log4j"

libraryDependencies ++= List(
  Library.scalaCheck % "test",
  Library.scalaTest  % "test"
)

initialCommands := """|import de.heikoseeberger.akkalog4j._""".stripMargin
