package com.poison.ivy.docs

trait Describable {
  lazy val name: String = this.getClass.getSimpleName
  def description: String
}
