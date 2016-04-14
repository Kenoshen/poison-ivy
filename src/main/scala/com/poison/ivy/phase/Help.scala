package com.poison.ivy.phase

import com.poison.ivy.flag.Flag

import scala.collection.mutable
import scala.concurrent.Future

class Help extends Phase {
  override lazy val description: String = ""

  override protected def run(input: mutable.Seq[Flag]): Future[Unit] = {
    println(helpMessage)
    Future.successful()
  }

  lazy val helpMessage =
    """
      | ██▓███   ▒█████   ██▓  ██████  ▒█████   ███▄    █     ██▓ ██▒   █▓▓██   ██▓
      |▓██░  ██▒▒██▒  ██▒▓██▒▒██    ▒ ▒██▒  ██▒ ██ ▀█   █    ▓██▒▓██░   █▒ ▒██  ██▒
      |▓██░ ██▓▒▒██░  ██▒▒██▒░ ▓██▄   ▒██░  ██▒▓██  ▀█ ██▒   ▒██▒ ▓██  █▒░  ▒██ ██░
      |▒██▄█▓▒ ▒▒██   ██░░██░  ▒   ██▒▒██   ██░▓██▒  ▐▌██▒   ░██░  ▒██ █░░  ░ ▐██▓░
      |▒██▒ ░  ░░ ████▓▒░░██░▒██████▒▒░ ████▓▒░▒██░   ▓██░   ░██░   ▒▀█░    ░ ██▒▓░
      |▒▓▒░ ░  ░░ ▒░▒░▒░ ░▓  ▒ ▒▓▒ ▒ ░░ ▒░▒░▒░ ░ ▒░   ▒ ▒    ░▓     ░ ▐░     ██▒▒▒
      |░▒ ░       ░ ▒ ▒░  ▒ ░░ ░▒  ░ ░  ░ ▒ ▒░ ░ ░░   ░ ▒░    ▒ ░   ░ ░░   ▓██ ░▒░
      |░░       ░ ░ ░ ▒   ▒ ░░  ░  ░  ░ ░ ░ ▒     ░   ░ ░     ▒ ░     ░░   ▒ ▒ ░░
      |             ░ ░   ░        ░      ░ ░           ░     ░        ░   ░ ░
      |                                                               ░    ░ ░
    """.stripMargin
}
