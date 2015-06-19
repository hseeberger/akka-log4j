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

import akka.actor.Actor
import akka.event.DummyClassForStringSources
import akka.event.Logging._
import akka.util.Helpers
import org.apache.logging.log4j.{ LogManager, ThreadContext }

object Log4jLogger {

  private final val MdcThread = "sourceThread"

  private final val MdcAkkaSource = "akkaSource"

  private final val MdcAkkaTimestamp = "akkaTimestamp"

  @inline
  private[akkalog4j] def apply(logClass: Class[_], logSource: String) =
    if (logClass == classOf[DummyClassForStringSources])
      LogManager.getLogger(logSource)
    else
      LogManager.getLogger(logClass)

  @inline
  private def withThreadContext(logSource: String, logEvent: LogEvent)(logStatement: => Unit) {
    ThreadContext.put(MdcAkkaSource, logSource)
    ThreadContext.put(MdcThread, logEvent.thread.getName)
    ThreadContext.put(MdcAkkaTimestamp, formatTimestamp(logEvent.timestamp))
    for ((k, v) <- logEvent.mdc) ThreadContext.put(k, String.valueOf(v))
    try
      logStatement
    finally
      ThreadContext.clearMap()
  }

  @inline
  private def formatTimestamp(timestamp: Long) = Helpers.currentTimeMillisToUTCString(timestamp)
}

final class Log4jLogger extends Actor {
  import Log4jLogger._

  override def receive = {
    case event @ Error(cause, logSource, logClass, message) =>
      withThreadContext(logSource, event) {
        cause match {
          case Error.NoCause | null => Log4jLogger(logClass, logSource).error(if (message != null) message.toString else null)
          case _                    => Log4jLogger(logClass, logSource).error(if (message != null) message.toString else cause.getLocalizedMessage, cause)
        }
      }

    case event @ Warning(logSource, logClass, message) =>
      withThreadContext(logSource, event) {
        Log4jLogger(logClass, logSource).warn("{}", message.asInstanceOf[AnyRef])
      }

    case event @ Info(logSource, logClass, message) =>
      withThreadContext(logSource, event) {
        Log4jLogger(logClass, logSource).info("{}", message.asInstanceOf[AnyRef])
      }

    case event @ Debug(logSource, logClass, message) =>
      withThreadContext(logSource, event) {
        Log4jLogger(logClass, logSource).debug("{}", message.asInstanceOf[AnyRef])
      }

    case InitializeLogger(_) =>
      sender() ! LoggerInitialized
  }
}
