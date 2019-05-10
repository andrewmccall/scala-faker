package com.andrewmccall.faker

trait Data {

  def fetch(key: String) : Either[String, Seq[String]]

}
