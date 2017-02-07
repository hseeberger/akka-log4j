/*
 * Copyright 2015 Heiko Seeberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.heikoseeberger.akkalog4j

import akka.actor.{ Actor, DiagnosticActorLogging, Props }
import akka.event.Logging
import akka.testkit.AkkaSpec
import java.nio.file.{ Files, Path, Paths, StandardOpenOption }
import org.scalatest.{ BeforeAndAfterAll, BeforeAndAfterEach, ConfigMap }
import scala.collection.JavaConverters._
import scala.concurrent.duration.DurationInt

object Log4jLoggerSpec {

  // This test depends on log4j configuration in src/test/resources/log4j2.xml

  final val Config =
    """|akka {
       |  loglevel               = info
       |  loggers                = ["de.heikoseeberger.akkalog4j.Log4jLogger"]
       |  logging-filter         = "de.heikoseeberger.akkalog4j.Log4jLoggingFilter"
       |  logger-startup-timeout = 30s
       |}
       |""".stripMargin

  class LogProducer extends Actor with DiagnosticActorLogging {

    def receive = {
      case e: Exception =>
        log.error(e, e.getMessage)

      case (s: String, x: Int, y: Int) =>
        log.info(s, x, y)

      case (s: String, mdc: Map[String, Any] @unchecked) =>
        log.mdc(mdc)
        log.info(s)
        log.clearMDC()
    }
  }

  class TestLogSource

  val out: Path =
    Paths.get(System.getProperty("java.io.tmpdir"), "log4j-logger-spec.log")

  def outputString: String =
    Files.readAllLines(out).asScala.mkString(sys.props("line.separator"))
}

class Log4jLoggerSpec
    extends AkkaSpec(Log4jLoggerSpec.Config)
    with BeforeAndAfterEach
    with BeforeAndAfterAll {
  import Log4jLoggerSpec._

  val producer = system.actorOf(Props(new LogProducer), "logProducer")

  val sourceThreadRegex =
    "sourceThread=\\[Log4jLoggerSpec-akka.actor.default-dispatcher-[1-9][0-9]*\\]"

  "Slf4jLogger" should {

    "log error with stackTrace" in {
      producer ! new RuntimeException("Simulated error")

      awaitCond(outputString.contains("----"), 10.seconds)
      val s = outputString
      s should include("akkaSource=[akka://Log4jLoggerSpec/user/logProducer]")
      s should include("level=[ERROR]")
      s should include("logger=[de.heikoseeberger.akkalog4j.Log4jLoggerSpec$LogProducer]")
      s should include regex sourceThreadRegex
      s should include("msg=[Simulated error]")
      s should include("java.lang.RuntimeException: Simulated error")
      s should include("at de.heikoseeberger.akkalog4j.Log4jLoggerSpec")
    }

    "log info with parameters" in {
      producer ! (("test x={} y={}", 3, 17))

      awaitCond(outputString.contains("----"), 5.seconds)
      val s = outputString
      s should include("akkaSource=[akka://Log4jLoggerSpec/user/logProducer]")
      s should include("level=[INFO]")
      s should include("logger=[de.heikoseeberger.akkalog4j.Log4jLoggerSpec$LogProducer]")
      s should include regex sourceThreadRegex
      s should include("msg=[test x=3 y=17]")
    }

    "put custom MDC values when specified" in {
      producer ! ("Message with custom MDC values", Map("ticketNumber" -> 3671,
                                                        "ticketDesc"   -> "Custom MDC Values"))

      awaitCond(outputString.contains("----"), 5.seconds)
      val s = outputString
      s should include("akkaSource=[akka://Log4jLoggerSpec/user/logProducer]")
      s should include("level=[INFO]")
      s should include("logger=[de.heikoseeberger.akkalog4j.Log4jLoggerSpec$LogProducer]")
      s should include regex sourceThreadRegex
      s should include("mdc=[ticket-#3671: Custom MDC Values]")
      s should include("msg=[Message with custom MDC values]")
    }

    "Support null values in custom MDC" in {
      producer ! ("Message with null custom MDC values", Map("ticketNumber" -> 3671,
                                                             "ticketDesc"   -> null))

      awaitCond(outputString.contains("----"), 5.seconds)
      val s = outputString
      s should include("akkaSource=[akka://Log4jLoggerSpec/user/logProducer]")
      s should include("level=[INFO]")
      s should include("logger=[de.heikoseeberger.akkalog4j.Log4jLoggerSpec$LogProducer]")
      s should include regex sourceThreadRegex
      s should include("mdc=[ticket-#3671: null]")
      s should include("msg=[Message with null custom MDC values]")
    }

    "include system info in akkaSource when creating Logging with system" in {
      val log =
        Logging(system, "de.heikoseeberger.akkalog4j.Log4jLoggerSpec.TestLogSource")
      log.info("test")
      awaitCond(outputString.contains("----"), 5.seconds)
      val s = outputString
      s should include(
        "akkaSource=[de.heikoseeberger.akkalog4j.Log4jLoggerSpec.TestLogSource(akka://Log4jLoggerSpec)]"
      )
      s should include(
        "logger=[de.heikoseeberger.akkalog4j.Log4jLoggerSpec.TestLogSource(akka://Log4jLoggerSpec)]"
      )
    }

    "not include system info in akkaSource when creating Logging with system.eventStream" in {
      val log =
        Logging(system.eventStream, "de.heikoseeberger.akkalog4j.Log4jLoggerSpec.TestLogSource")
      log.info("test")
      awaitCond(outputString.contains("----"), 5.seconds)
      val s = outputString
      s should include("akkaSource=[de.heikoseeberger.akkalog4j.Log4jLoggerSpec.TestLogSource]")
      s should include("logger=[de.heikoseeberger.akkalog4j.Log4jLoggerSpec.TestLogSource]")
    }

    "use short class name and include system info in akkaSource when creating Logging with system and class" in {
      val log = Logging(system, classOf[TestLogSource])
      log.info("test")
      awaitCond(outputString.contains("----"), 5.seconds)
      val s = outputString
      s should include("akkaSource=[Log4jLoggerSpec$TestLogSource(akka://Log4jLoggerSpec)]")
      s should include("logger=[de.heikoseeberger.akkalog4j.Log4jLoggerSpec$TestLogSource]")
    }

    "use short class name in akkaSource when creating Logging with system.eventStream and class" in {
      val log = Logging(system.eventStream, classOf[TestLogSource])
      log.info("test")
      awaitCond(outputString.contains("----"), 5.seconds)
      val s = outputString
      s should include("akkaSource=[Log4jLoggerSpec$TestLogSource]")
      s should include("logger=[de.heikoseeberger.akkalog4j.Log4jLoggerSpec$TestLogSource]")
    }
  }

  override protected def atStartup(): Unit = {
    super.atStartup()
    Files.deleteIfExists(out)
    Files.createFile(out)
  }

  override protected def beforeEach() = {
    super.beforeEach()
    Files.write(
      out,
      Vector.empty[String].asJava,
      StandardOpenOption.TRUNCATE_EXISTING
    )
  }
}
