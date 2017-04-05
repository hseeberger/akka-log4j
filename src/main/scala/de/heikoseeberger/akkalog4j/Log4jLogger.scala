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
import akka.dispatch.RequiresMessageQueue
import akka.event.{ DummyClassForStringSources, LoggerMessageQueueSemantics }
import akka.event.Logging.{
  Debug,
  Error,
  Info,
  InitializeLogger,
  LogEvent,
  LoggerInitialized,
  Warning
}
import akka.util.Helpers
import org.apache.logging.log4j.{ LogManager, ThreadContext }

object Log4jLogger {

  private final val MdcThread        = "sourceThread"
  private final val MdcAkkaSource    = "akkaSource"
  private final val MdcAkkaTimestamp = "akkaTimestamp"

  @inline
  private[akkalog4j] def apply(logClass: Class[_], source: String) =
    if (logClass == classOf[DummyClassForStringSources])
      LogManager.getLogger(source)
    else
      LogManager.getLogger(logClass)

  @inline
  private def withThreadContext(source: String, event: LogEvent)(statement: => Unit) {
    ThreadContext.put(MdcAkkaSource, source)
    ThreadContext.put(MdcThread, event.thread.getName)
    ThreadContext.put(MdcAkkaTimestamp, formatTimestamp(event.timestamp))
    for ((k, v) <- event.mdc) ThreadContext.put(k, String.valueOf(v))
    try statement
    finally ThreadContext.clearMap()
  }

  @inline
  private def formatTimestamp(timestamp: Long) = Helpers.currentTimeMillisToUTCString(timestamp)
}

final class Log4jLogger extends Actor with RequiresMessageQueue[LoggerMessageQueueSemantics] {
  import Log4jLogger._

  override def receive = {
    case event @ Error(Error.NoCause | null, source, logClass, message) =>
      val msg = if (message != null) message.toString else null
      withThreadContext(source, event)(Log4jLogger(logClass, source).error(msg))

    case event @ Error(cause, source, logClass, message) =>
      val msg = if (message != null) message.toString else cause.getLocalizedMessage
      withThreadContext(source, event)(Log4jLogger(logClass, source).error(msg, cause))

    case event @ Warning(source, logClass, message) =>
      withThreadContext(source, event)(Log4jLogger(logClass, source).warn("{}", message))

    case event @ Info(source, logClass, message) =>
      withThreadContext(source, event)(Log4jLogger(logClass, source).info("{}", message))

    case event @ Debug(source, logClass, message) =>
      withThreadContext(source, event)(Log4jLogger(logClass, source).debug("{}", message))

    case InitializeLogger(_) =>
      sender() ! LoggerInitialized
  }
}
