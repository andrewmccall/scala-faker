package com.andrewmccall.faker
import org.scalatest.{FlatSpec, Matchers}

class EntrySpec extends FlatSpec with Matchers{

  "Entry for a simple string" should "return that string" in {
    val string = "Some String"

    val entry = Entry.getEntry(string)
    assert(entry.get(null) == string)
  }

}
