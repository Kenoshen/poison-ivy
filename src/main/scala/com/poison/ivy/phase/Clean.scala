package com.poison.ivy.phase

import java.io.File

import com.poison.ivy.flag.Flag

import scala.concurrent.Future
import scala.collection.mutable

class Clean extends Phase {
  override lazy val description: String = "Try to find the build file for parsing"

  override protected def run(input: mutable.Seq[Flag]): Future[Unit] = {
    val file = new File("build")
    if (file.exists) {
      // TODO: MW delete the directory
      Future.successful()
    }
    ???
  }
}
