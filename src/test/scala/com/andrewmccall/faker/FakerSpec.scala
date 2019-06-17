package com.andrewmccall.faker

import java.util.Random

import org.mockito.{ArgumentMatchersSugar, IdiomaticMockito}
import org.scalatest.{AppendedClues, FlatSpec, Matchers}

class FakerSpec extends FlatSpec with Matchers with AppendedClues with IdiomaticMockito with ArgumentMatchersSugar {

  private class EmptyData extends Data {
    override def fetch(key: String, locale: Option[String], defaultLocale: String): Option[Entry] = ???

    override def getKeys(): Iterable[String] = ???
  }

  private val data = new EmptyData

  "A key with the full path" should "be returned as is, with the locale changed if required." in {

    val mockData = mock[Data]
    mockData.contains(eqTo("en-CA.faker.name.name.other"), any, any) shouldReturn false
    mockData.contains(eqTo("en-GB.faker.name.name.other"), any, any) shouldReturn true
    val faker = new Faker(new Config(data = mockData))

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
    mockData.contains("en-GB.faker.name", any, any) shouldReturn true

    val faker = new Faker(new Config(data = mockData))
    val ret = faker.getKey(key, locale = "en-GB")
    assert(ret == "en-GB.faker.name")
  }

  "A multiple field key and a locale" should "return the value if the data contains the key and locale" in {
    val key = "name.name"
    val mockData = mock[Data]
    mockData.contains("en-GB.faker.name.name", any, any) shouldReturn true

    val faker = new Faker(new Config(data = mockData))
    val ret = faker.getKey(key, locale = "en-GB")
    assert(ret == "en-GB.faker.name.name")
  }

  "A single field key and a locale" should "return en if the data doesn't contain the key for that locale" in {
    val key = "name"
    val mockData = mock[Data]
    mockData.contains("en-GB.faker.name", any, any) shouldReturn false

    val faker = new Faker(new Config(data = mockData))
    val ret = faker.getKey(key, locale = "en-GB")
    assert(ret == "en.faker.name")
  }

  "A multiple field key and a locale" should "return en if the data doesn't contain the key for that locale" in {
    val key = "name"
    val mockData = mock[Data]
    mockData.contains("en-GB.faker.name.name", any, any) shouldReturn false

    val faker = new Faker(new Config(data = mockData))
    val ret = faker.getKey(key, locale = "en-GB")
    assert(ret == "en.faker.name")
  }

  "A key with a parent key" should "have the parent key added." in {

    val mockData = mock[Data]
    val faker = new Faker(new Config(data = mockData))

    var key = "name.other"

    mockData.contains("en-GB.faker.name.other", any, any) shouldReturn false
    mockData.contains("en.faker.name.other", any, any) shouldReturn false
    mockData.contains("en-GB.faker.name.name.other", any, any) shouldReturn true

    assert(faker.getKey(key, parentKey = "name", locale = "en-GB") == "en-GB.faker.name.name.other")

    mockData.contains("en-GB.faker.name.other", any, any) shouldReturn false
    mockData.contains("en.faker.name.other", any, any) shouldReturn false
    mockData.contains("en-GB.faker.name.name.other", any, any) shouldReturn false
    key = "name.other"
    assert(faker.getKey(key, parentKey = "name", locale = "en-GB") == "en.faker.name.name.other")

  }

  "Parsing a string that doesn't need changes" should "get returned as is" in {
    val string = "Some String!"
    val faker = new Faker(new Config(random = new Random(18), data = data))
    assert(string == faker(string))
  }

  "Parsing a string that is a key" should "return that key" in {
    val string = "Name.name"

    val mockData = mock[Data]

    val faker = new Faker(new Config(data = mockData))
    mockData.contains("en.faker.name.name", any, any) shouldReturn true
    mockData.fetch("en.faker.name.name", any, any) shouldReturn Some(StringEntry("World"))

    assert("World" == faker(string))

  }

  "Parsing a string with a key" should "replace the key" in {
    val string = s"Hello #{Name.name}!"

    val mockData = mock[Data]

    val faker = new Faker(new Config(data = mockData))
    mockData.contains("en.faker.name.name", any, any) shouldReturn true
    mockData.fetch("en.faker.name.name", any, any) shouldReturn Some(StringEntry("World"))

    assert("Hello World!" == faker(string))

    mockData.contains("en.faker.world", any, any) shouldReturn true
    mockData.fetch("en.faker.world", any, any) shouldReturn Some(StringEntry("World"))
    assert("Hello World!!" == faker("Hello #{world}!!"))
  }

  "A numeric string, allowing leading zeros" should "have it's placeholders replaced, the first digit being zero" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val faker = new Faker(new Config(random = new Random(18), data = data))

    val string = "S##-B###"
    val result = faker.numerify(string, leadingZeros = true)

    assert(result.equals("S04-B807"))
  }

  "A numeric string, not allowing leading zeros" should "have it's placeholders replaced, the first digit is not zero" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val faker = new Faker(new Config(random = new Random(18), data = data))

    val string = "S##-B###"
    val result = faker.numerify(string)

    assert(result.equals("S64-B807"))
  }

  "A letter string" should "have it's placeholder replaces" in {

    val faker = new Faker(new Config(random = new Random(18), data = data))

    val string = "01-?234-???"
    val result = faker.letterify(string)
    println(result)

    assert(result.equals("01-A234-ENU"))
  }

  "A string with both letters and numbers" should "have it's placeholders replaced" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val faker = new Faker(new Config(random = new Random(18), data = data))

    val string = "##-?#?-C4"
    val result = faker.bothify(string)

    assert(result.equals("64-U8C-C4"))
  }

  "A string that starts with / and ends with /" should "be treated a simple replacement" in {
    val faker = new Faker(new Config(random = new Random(18), data = data))
    val string = "/###-???/"
    assert(!faker.isRegex(string))
    assert(faker.isReplacement(string))
    assert(faker.bothify(string) == "648-UCR")
  }

  "A string that starts with /^ and ends with $/" should "be treated as a regex" in {

    // A see that will return a 0 as the first value then 4, 8, 0, 7
    val faker = new Faker(new Config(random = new Random(18), data = data))


    val not = "[ABC]"
    assert(!faker.isRegex(not), s"'$not' should not be identified as a regex")
    val yep = "/^" + not + "$/"
    assert(faker.isRegex(yep), s"'$yep' should be identified as a regex.")
    val example = "/^[A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {1,2}[0-9][ABD-HJLN-UW-Z]{2}$/"
    assert(faker.isRegex(example), "The example from the regexify method should be true.")

    val missingSlashes = "^" + not + "$"
    assert(!faker.isRegex(missingSlashes), "Missing the slashes isn't the correct anchor.")
  }

  "regexify" should "generate the correct regex string" in {
    val faker = new Faker(new Config(random = new Random(18), data = data))

    faker.regexify("ABC") shouldBe "ABC" withClue "Non regex should just return."
    faker.regexify("/^ABC$/") shouldBe "ABC" withClue "Anchors should have been removed"
    faker.regexify("ASD{2} asd") shouldBe "ASDD asd" withClue "Digits of the form {1} should be replaced with {1,1} which should be repeated by a later rule"
    faker.regexify("Some a? test") should (be("Some a test") or be("Some test")) withClue "All ? should become {0,1}, and a one or none of the prev item"
    faker.regexify("A{2,2}") shouldBe "AA"
    faker.regexify("Some digits: \\d{3}") should fullyMatch regex "Some digits: \\d\\d\\d"
    faker.regexify("Test (this|that) string") should (be("Test this string") or be("Test that string"))
    faker.regexify("Some test a[b-da]c string") should fullyMatch regex "Some test a[b-da]c string"
    faker.regexify("Some a[ABcd]d string") should fullyMatch regex "Some a[ABcd]d string"
    faker.regexify("\\w\\w\\d\\d-[ABC]") should fullyMatch regex "\\w\\w\\d\\d-[ABC]"
  }

}
