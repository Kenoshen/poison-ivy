package com.poison.ivy.phase

import com.poison.ivy.flag.Flag

import scala.collection.mutable
import scala.concurrent.Future

class Help extends Phase {
  override lazy val description: String = ""

  override protected def run(input: mutable.Seq[Flag]): Future[Unit] = {
    println(s"${Console.CYAN}$logo${Console.WHITE}$helpMessage")
    Future.successful()
  }

  lazy val logo =
    """
      |  ██▓███   ▒█████   ██▓  ██████  ▒█████   ███▄    █     ██▓ ██▒   █▓▓██   ██▓
      | ▓██░  ██▒▒██▒  ██▒▓██▒▒██    ▒ ▒██▒  ██▒ ██ ▀█   █    ▓██▒▓██░   █▒ ▒██  ██▒
      | ▓██░ ██▓▒▒██░  ██▒▒██▒░ ▓██▄   ▒██░  ██▒▓██  ▀█ ██▒   ▒██▒ ▓██  █▒░  ▒██ ██░
      | ▒██▄█▓▒ ▒▒██   ██░░██░  ▒   ██▒▒██   ██░▓██▒  ▐▌██▒   ░██░  ▒██ █░░  ░ ▐██▓░
      | ▒██▒ ░  ░░ ████▓▒░░██░▒██████▒▒░ ████▓▒░▒██░   ▓██░   ░██░   ▒▀█░    ░ ██▒▓░
      | ▒▓▒░ ░  ░░ ▒░▒░▒░ ░▓  ▒ ▒▓▒ ▒ ░░ ▒░▒░▒░ ░ ▒░   ▒ ▒    ░▓     ░ ▐░     ██▒▒▒
      | ░▒ ░       ░ ▒ ▒░  ▒ ░░ ░▒  ░ ░  ░ ▒ ▒░ ░ ░░   ░ ▒░    ▒ ░   ░ ░░   ▓██ ░▒░
      | ░░       ░ ░ ░ ▒   ▒ ░░  ░  ░  ░ ░ ░ ▒     ░   ░ ░     ▒ ░     ░░   ▒ ▒ ░░
      |              ░ ░   ░        ░      ░ ░           ░     ░        ░   ░ ░
      |                                                                ░    ░ ░
      |
    """.stripMargin

  lazy val helpMessage =
    """
      | This is where the man page goes
    """.stripMargin
}
