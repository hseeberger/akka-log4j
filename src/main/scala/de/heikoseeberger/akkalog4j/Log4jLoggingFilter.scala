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

import akka.actor.ActorSystem
import akka.event.{ EventStream, Logging, LoggingFilter }

class Log4jLoggingFilter(settings: ActorSystem.Settings, eventStream: EventStream) extends LoggingFilter {

  override def isErrorEnabled(logClass: Class[_], logSource: String): Boolean =
    eventStream.logLevel >= Logging.ErrorLevel && Log4jLogger(logClass, logSource).isErrorEnabled

  override def isWarningEnabled(logClass: Class[_], logSource: String): Boolean =
    eventStream.logLevel >= Logging.WarningLevel && Log4jLogger(logClass, logSource).isWarnEnabled

  override def isInfoEnabled(logClass: Class[_], logSource: String): Boolean =
    eventStream.logLevel >= Logging.InfoLevel && Log4jLogger(logClass, logSource).isInfoEnabled

  override def isDebugEnabled(logClass: Class[_], logSource: String): Boolean =
    eventStream.logLevel >= Logging.DebugLevel && Log4jLogger(logClass, logSource).isDebugEnabled
}
