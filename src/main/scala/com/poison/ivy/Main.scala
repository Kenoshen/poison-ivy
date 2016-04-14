package com.poison.ivy

import com.poison.ivy.flag.Flag
import com.poison.ivy.phase.{Phase, Compile, Clean}

class Main extends App {

  // I *could* make this sequence using reflection... but I don't want to...
  val availablePhases = Seq(
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

  phases.foreach(_(flags))
}
