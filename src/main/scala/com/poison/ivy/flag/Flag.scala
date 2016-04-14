package com.poison.ivy.flag

case class Flag(name:String, value:Option[String] = None)

object Flag {
  private val FLAG_REGEX = """(--?)(\w.*?\b)((:)((['"])([^\s].*?)(['"])|([^\s].*)))?""".r
  def parse(str:String):Option[Flag] = FLAG_REGEX.findAllMatchIn(str).toSeq.headOption.map(m => Flag(m.group(2), Option(m.group(7)) orElse Option(m.group(9))))
}