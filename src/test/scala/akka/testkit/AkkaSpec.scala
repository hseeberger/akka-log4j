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

/**
  * Copyright (C) 2009-2014 Typesafe Inc. <http://www.typesafe.com>
  */
package akka.testkit

import language.{ postfixOps, reflectiveCalls }

import org.scalatest.{ BeforeAndAfterAll, WordSpecLike }
import org.scalatest.Matchers
import akka.actor.ActorSystem
import akka.event.{ Logging, LoggingAdapter }
import scala.concurrent.duration._
import scala.concurrent.Future
import com.typesafe.config.{ Config, ConfigFactory }
import akka.dispatch.Dispatchers
import akka.testkit.TestEvent._

object AkkaSpec {
  val testConf: Config =
    ConfigFactory.parseString("""
      akka {
        loggers = ["akka.testkit.TestEventListener"]
        loglevel = "WARNING"
        stdout-loglevel = "WARNING"
        actor {
          default-dispatcher {
            executor = "fork-join-executor"
            fork-join-executor {
              parallelism-min = 8
              parallelism-factor = 2.0
              parallelism-max = 8
            }
          }
        }
      }
                                                    """)

  def mapToConfig(map: Map[String, Any]): Config = {
    import scala.collection.JavaConverters._
    ConfigFactory.parseMap(map.asJava)
  }

  def getCallerName(clazz: Class[_]): String = {
    val s = (Thread.currentThread.getStackTrace map (_.getClassName) drop 1)
      .dropWhile(_ matches "(java.lang.Thread|.*AkkaSpec.?$)")
    val reduced = s.lastIndexWhere(_ == clazz.getName) match {
      case -1 ⇒ s
      case z  ⇒ s drop (z + 1)
    }
    reduced.head.replaceFirst(""".*\.""", "").replaceAll("[^a-zA-Z_0-9]", "_")
  }

}

abstract class AkkaSpec(_system: ActorSystem)
    extends TestKit(_system)
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with WatchedByCoroner {

  def this(config: Config) =
    this(
      ActorSystem(
        AkkaSpec.getCallerName(getClass),
        ConfigFactory.load(config.withFallback(AkkaSpec.testConf))
      ))

  def this(s: String) = this(ConfigFactory.parseString(s))

  def this(configMap: Map[String, _]) = this(AkkaSpec.mapToConfig(configMap))

  def this() =
    this(ActorSystem(AkkaSpec.getCallerName(getClass), AkkaSpec.testConf))

  val log: LoggingAdapter = Logging(system, this.getClass)

  override val invokeBeforeAllAndAfterAllEvenIfNoTestsAreExpected = true

  final override def beforeAll {
    startCoroner
    atStartup()
  }

  final override def afterAll {
    beforeTermination()
    shutdown()
    afterTermination()
    stopCoroner()
  }

  protected def atStartup() {}

  protected def beforeTermination() {}

  protected def afterTermination() {}

  def spawn(dispatcherId: String = Dispatchers.DefaultDispatcherId)(
      body: ⇒ Unit): Unit =
    Future(body)(system.dispatchers.lookup(dispatcherId))

  override def expectedTestDuration: FiniteDuration = 60 seconds

  def muteDeadLetters(messageClasses: Class[_]*)(
      sys: ActorSystem = system): Unit =
    if (!sys.log.isDebugEnabled) {
      def mute(clazz: Class[_]): Unit =
        sys.eventStream.publish(
          Mute(DeadLettersFilter(clazz)(occurrences = Int.MaxValue)))
      if (messageClasses.isEmpty) mute(classOf[AnyRef])
      else messageClasses foreach mute
    }

}
