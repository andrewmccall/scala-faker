package com.andrewmccall.faker

import java.util.Random

import org.mockito.IdiomaticMockito
import org.scalatest.FlatSpec

class FakerSpec extends FlatSpec with IdiomaticMockito {

  private class EmptyData extends Data {
    override def fetch(key: String): Either[String, Seq[String]] = ???
  }

  private val data = new EmptyData

  "A key with the full path" should "be returned as is, with the locale changed if required." in {

    val mockData = mock[Data]
    mockData.contains("en-CA.faker.name.name.other") shouldReturn false
    mockData.contains("en-GB.faker.name.name.other") shouldReturn true
    val faker = new Faker(new Config(data=mockData))

    var key = "en.faker.name.name.other"
    assert(faker.getKey(key) == "en.faker.name.name.other")

    key = "en-CA.faker.name.name.other"
    assert(faker.getKey(key) == "en.faker.name.name.other")

    key = "en-GB.faker.name.name.other"
    assert(faker.getKey(key) == "en-GB.faker.name.name.other")

  }

  "A single field key and a locale" should "return the value if the data contains the key and locale" in {
    val key = "name"
    val mockData = mock[Data]
    mockData.contains("en-GB.faker.name") shouldReturn true

    val faker = new Faker(new Config(data=mockData))
    val ret = faker.getKey(key, locale="en-GB")
    assert(ret == "en-GB.faker.name")
  }

  "A multiple field key and a locale" should "return the value if the data contains the key and locale" in {
    val key = "name.name"
    val mockData = mock[Data]
    mockData.contains("en-GB.faker.name.name") shouldReturn true

    val faker = new Faker(new Config(data=mockData))
    val ret = faker.getKey(key, locale="en-GB")
    assert(ret == "en-GB.faker.name.name")
  }

  "A single field key and a locale" should "return en if the data doesn't contain the key for that locale" in {
    val key = "name"
    val mockData = mock[Data]
    mockData.contains("en-GB.faker.name") shouldReturn false

    val faker = new Faker(new Config(data=mockData))
    val ret = faker.getKey(key, locale="en-GB")
    assert(ret == "en.faker.name")
  }

  "A multiple field key and a locale" should "return en if the data doesn't contain the key for that locale" in {
    val key = "name"
    val mockData = mock[Data]
    mockData.contains("en-GB.faker.name.name") shouldReturn false

    val faker = new Faker(new Config(data=mockData))
    val ret = faker.getKey(key, locale="en-GB")
    assert(ret == "en.faker.name")
  }

  "A key with a parent key" should "have the parent key added." in {

    val mockData = mock[Data]
    val faker = new Faker(new Config(data=mockData))

    var key = "name.other"
    mockData.contains("en-GB.faker.name.other") shouldReturn false
    mockData.contains("en.faker.name.other") shouldReturn false
    mockData.contains("en-GB.faker.name.name.other") shouldReturn true

    assert(faker.getKey(key, parentKey="name", locale = "en-GB") == "en-GB.faker.name.name.other")

    mockData.contains("en-GB.faker.name.other") shouldReturn false
    mockData.contains("en.faker.name.other") shouldReturn false
    mockData.contains("en-GB.faker.name.name.other") shouldReturn false
    key = "name.other"
    assert(faker.getKey(key, parentKey="name", locale="en-GB") == "en.faker.name.name.other")

  }

  "Parsing a string that doesn't need changes" should "get returned as is" in {
    val string = "Some String!"
    val faker = new Faker(new Config(random = new Random(18), data=data))
    assert(string == faker(string))
  }

  "Parsing a string with a key" should "replace the key" in {
    val string = s"Hello #{Name.name}!"

    val mockData = mock[Data]

    val faker = new Faker(new Config(data=mockData))
    mockData.contains("name.name") shouldReturn true
    mockData.fetch("name.name") shouldReturn Left("World")

    assert("Hello World!" == faker(string))

    mockData.contains("world") shouldReturn true
    mockData.fetch("world") shouldReturn Left("World")
    assert("Hello World!!" == faker("Hello #{world}!!" ))
  }

  "A numeric string, allowing leading zeros" should "have it's placeholders replaced, the first digit being zero" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val faker = new Faker(new Config(random = new Random(18), data=data))

    val string = "S##-B###"
    val result = faker.numerify(string, leadingZeros = true)

    assert(result.equals("S04-B807"))
  }

  "A numeric string, not allowing leading zeros" should "have it's placeholders replaced, the first digit is not zero" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val faker = new Faker(new Config(random = new Random(18), data=data))

    val string = "S##-B###"
    val result = faker.numerify(string)

    assert(result.equals("S64-B807"))
  }

  "A letter string" should "have it's placeholder replaces" in {

    val faker = new Faker(new Config(random = new Random(18), data=data))

    val string = "01-?234-???"
    val result = faker.letterify(string)
    println(result)

    assert(result.equals("01-A234-ENU"))
  }

  "A string with both letters and numbers" should "have it's placeholders replaced" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val faker = new Faker(new Config(random = new Random(18), data=data))

    val string = "##-?#?-C4"
    val result = faker.bothify(string)

    assert(result.equals("64-U8C-C4"))
  }

  "A string that starts with / and ends with /" should "be treated a simple replacement" in {
    val faker = new Faker(new Config(random = new Random(18), data = data))
    val string = "/###-???/"
    assert(faker.bothify(string) == "648-UCR")
  }

  "A string that starts with /^ and ends with $/" should "be treated as a regex" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val faker = new Faker(new Config(random = new Random(18), data=data))


    val not = "[ABC]"
    assert(!faker.isRegex(not), s"'$not' should not be identified as a regex")
    val yep = "/^" + not + "$/"
    assert(faker.isRegex(yep), s"'$yep' should be identified as a regex.")
    val example = "/^[A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {1,2}[0-9][ABD-HJLN-UW-Z]{2}$/"
    assert(faker.isRegex(example), "The example from the regexify method should be true.")

    val missingSlashes = "^" + not + "$"
    assert(!faker.isRegex(missingSlashes), "Missing the slashes isn't the correct anchor.")
  }

  "A string with nothing to change" should "just get returned" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val faker = new Faker(new Config(random = new Random(18), data=data))

    val result = faker.regexify( "ABC")
    assert(result.equals("ABC"), s"The string 'ABC' should have been returned unmodified got '$result'")
  }

  "The anchors" should "get removed from a regex string" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val faker = new Faker(new Config(random = new Random(18), data=data))

    val string = "/^ABC$/"
    val result = faker.regexify(string)
    assert(result.equals("ABC"), s"Anchors should have been removed, was $result")
  }

  "Digits of the form {1}" should "be replaced with {1,1}" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val faker = new Faker(new Config(random = new Random(18), data=data))


    val string = "ASD {2} asd"
    val result = faker.regexify(string)
    assert(result == "ASD {2,2} asd")
  }

  "All ?" should "become {0,1}" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val faker = new Faker(new Config(random = new Random(18), data=data))


    val string = "Some ? number"
    val result = faker.regexify(string)
    assert(result == "Some {0,1} number")
  }

  "I don't know what this" should "be doing" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val faker = new Faker(new Config(random = new Random(18), data=data))


    val string = "/^[A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {1,2}[0-9][ABD-HJLN-UW-Z]{2}$/"
    val result = faker.regexify(string)
    fail
  }

}
