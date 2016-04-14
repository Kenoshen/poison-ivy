package com.poison.ivy.phase

import com.poison.ivy.flag.Flag
import com.poison.ivy.task.Task

trait Phase extends Task[scala.collection.mutable.Seq[Flag], Unit]
