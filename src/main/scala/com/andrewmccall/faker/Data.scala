package com.andrewmccall.faker

trait Data {

  def fetch(key: String) : Entry

  def contains(key: String) : Boolean = false

}
