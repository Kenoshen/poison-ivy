package com.poison.ivy.task

import java.io.File

object Clean extends TaskGroup[Unit] {
  override lazy val description: String = "Try to find the build file for parsing"

  override def run(input: Unit): Future[Unit] = {
    val file = new File("build")
    if (file.exists) {
      // TODO: MW delete the directory
      Future.successful()
    }
  }
}
