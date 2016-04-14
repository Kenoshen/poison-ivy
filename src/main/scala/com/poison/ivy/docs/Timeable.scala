package com.poison.ivy.docs

import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration

trait Timeable {
  private var _startTime:Long = 0
  private var _endTime:Long = 0
  private var _isTiming:Boolean = false
  def beginTiming: Unit = {
    _startTime = System.currentTimeMillis()
    _isTiming = true
  }
  def stopTiming: Unit = {
    _endTime = System.currentTimeMillis()
    _isTiming = false
  }
  def isTiming: Boolean = _isTiming
  def timing:Long = if (_isTiming) System.currentTimeMillis() - _startTime else _endTime - _startTime
  def timingAsDuration:Duration = Duration(timing, TimeUnit.MILLISECONDS)
  def endTimeAsPrettyString: String = new SimpleDateFormat().format(new Date(_endTime))
}
