package com.poison.ivy

import com.poison.ivy.docs.{Describable, Timeable}
import com.poison.ivy.flag.Flag
import com.poison.ivy.phase.{Help, Phase, Compile, Clean}

object Main extends App with Timeable with Describable {
  override lazy val description: String = args.mkString(" ")

  beginTiming

  // I *could* make this sequence using reflection... but I don't want to...
  val availablePhases = Seq(
    new Help(),
    new Clean(),
    new Compile()
  )

  val flags = scala.collection.mutable.Seq[Flag]()
  val phases = scala.collection.mutable.Seq[Phase]()

  args.foreach(arg => {
    val phase = availablePhases.find(_.name.equalsIgnoreCase(arg))
    phase.foreach(p => phases :+ p)
    val flag = Flag.parse(arg)
    flag.foreach(f => flags :+ f)
  })

  var failed = false
  try {
    if (phases.isEmpty) availablePhases.head(flags)
    else phases.foreach(_(flags))
  } catch {
    case e: Exception =>
      failed = true
      throw e
  } finally {
    stopTiming
    lastOutput(failed)
  }


  private def lastOutput(failed:Boolean) = println(s" ${
    if (failed) s"${Console.RED}~failed: '$description'~"
    else s"${Console.GREEN}~success: '$description'~${Console.WHITE}"
  } took $timingAsDuration, finished at $endTimeAsPrettyString")
}
