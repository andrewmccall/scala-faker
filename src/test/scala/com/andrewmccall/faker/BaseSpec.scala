package com.andrewmccall.faker

import java.util.Random

import org.scalatest.FlatSpec

class BaseSpec extends FlatSpec {

  "A numeric string, allowing leading zeros" should "have it's placeholders replaced, the first digit being zero" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val r = new Random(18);
    Base.setRandom(r)

    val string = "S##-B###"
    val result = Base.numerify(string, leadingZeros = true)

    assert(result.equals("S04-B807"))
  }

  "A numeric string, not allowing leading zeros" should "have it's placeholders replaced, the first digit is not zero" in {
    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val r = new Random(18);
    Base.setRandom(r)

    val string = "S##-B###"
    val result = Base.numerify(string)

    assert(result.equals("S64-B807"))
  }

  "A letter string" should "have it's placeholder replaces" in {
    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val r = new Random(18)
    Base.setRandom(r)

    val string = "01-?234-???"
    val result = Base.letterify(string)
    println(result)

    assert(result.equals("01-W234-NQD"))
  }

  "A string with both letters and numbers" should "have it's placeholders replaced" in {
    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val r = new Random(18)
    Base.setRandom(r)

    val string = "##-?#?-C4"
    val result = Base.bothify(string)
    println(result)

    assert(result.equals("64-D8M-C4"))
  }

}
