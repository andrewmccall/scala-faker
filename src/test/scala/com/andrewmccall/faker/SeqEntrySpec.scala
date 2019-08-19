package com.andrewmccall.faker

import org.scalatest.{FlatSpec, Matchers}

class SeqEntrySpec extends FlatSpec with Matchers {

  "Adding a string to a SeqEntry" should "return a new SeqEntry with the string" in {
    val entries = SeqEntry(Seq("one", "two"))
    val res = entries + "three"
    assert(res.seq.length == 3)
  }

  "Adding an existing string to a SewEntry" should "not add the string" in {
    val entries = SeqEntry(Seq("one", "two"))
    val res = entries + "two"
    assert(res.seq.length == 2)
  }

  "Adding a Seq of values to a SeqEntry" should "merge them" in {
    val entries = SeqEntry(Seq("one", "two"))
    val res = entries ++ Seq("three", "four")
    assert(res.seq.length == 4)
  }

  "Adding a Seq of values with some overlap" should "return the union of distinct values" in {
    val entries = SeqEntry(Seq("one", "two"))
    val res = entries ++ Seq("two", "three")
    assert(res.seq.length == 3)
  }

}
