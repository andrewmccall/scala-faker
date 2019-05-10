package com.andrewmccall.faker

import java.util.Random

import org.scalatest.FlatSpec

class BaseSpec extends FlatSpec {

  "A numeric string, allowing leading zeros" should "have it's placeholders replaced, the first digit being zero" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val r = new Random(18);
    Faker.setRandom(r)

    val string = "S##-B###"
    val result = Base.numerify(string, leadingZeros = true)

    assert(result.equals("S04-B807"))
  }

  "A numeric string, not allowing leading zeros" should "have it's placeholders replaced, the first digit is not zero" in {
    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val r = new Random(18);
    Faker.setRandom(r)

    val string = "S##-B###"
    val result = Base.numerify(string)

    assert(result.equals("S64-B807"))
  }

  "A letter string" should "have it's placeholder replaces" in {
    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val r = new Random(18)
    Faker.setRandom(r)

    val string = "01-?234-???"
    val result = Base.letterify(string)
    println(result)

    assert(result.equals("01-W234-NQD"))
  }

  "A string with both letters and numbers" should "have it's placeholders replaced" in {
    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val r = new Random(18)
    Faker.setRandom(r)

    val string = "##-?#?-C4"
    val result = Base.bothify(string)
    println(result)

    assert(result.equals("64-D8M-C4"))
  }

  "A string that starts with /^ and ends with $/" should "be treated as a regex" in {
    val not = "[ABC]"
    assert(!Base.isRegex(not), s"'${not}' should not be identified as a regex")
    val yep = "/^" + not + "$/"
    assert(Base.isRegex(yep), s"'${yep}' should be identified as a regex.")
    val example = "/^[A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {1,2}[0-9][ABD-HJLN-UW-Z]{2}$/"
    assert(Base.isRegex(example), "The example from the regexify method should be true.")

    val missingSlashes = "^" + not + "$"
    assert(!Base.isRegex(missingSlashes), "Missing the slashes isn't the correct anchor.")
  }

}
