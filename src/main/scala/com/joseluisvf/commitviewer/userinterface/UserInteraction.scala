package com.joseluisvf.commitviewer.userinterface

import org.apache.logging.log4j.scala.Logging

object UserInteraction extends Logging {
  def show(message: String): Unit = logger.info(message)
}
