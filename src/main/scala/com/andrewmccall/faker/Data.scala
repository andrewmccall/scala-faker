package com.andrewmccall.faker

trait Data {

  def fetch(key: String) : Either[String, Seq[String]]

  def contains(key: String) : Boolean = false

}
