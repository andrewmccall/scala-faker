package com.andrewmccall.faker.yaml

import org.apache.logging.log4j.scala.Logging
import org.scalatest.FlatSpec

class YamlDataSpec extends FlatSpec with Logging {

  "some files" should "get loaded" in {
    val files = YamlData.getFilesForClass(this.getClass, "test/yaml")

    assert(files.length == 2)

    files.foreach(f => logger.info(s"File $f"))

    assert(files(0).endsWith("a.yaml"))
    assert(files(1).endsWith("b.yaml"))
  }

  "Loading without a Class or path" should "load the base YAML" in {
    val files = YamlData.load()
    assert(files.contains("en"))
    assert(files.contains("en-GB"))
  }

  "A known key" should "be contained and be returned" in {
    val data = new YamlData()
    assert(data.contains("en.faker.name.name"))

    val result = data.fetch("en.faker.name.name")
    assert(result != null)
  }

  "merging a map and a string" should "Return the map." in {
    val map = Map ( "a" -> Map( "a" -> "this is an A"))
    val string = Map( "a" -> "taco")

    val result = YamlData.merge(map, string)
    assert(result == map)

  }

  "merging a map and a map" should "union the maps" in {
    val a = Map ("foo" -> Map("bar" -> "foo_bar_value", "biz" -> "foo_biz_value"))
    val b = Map ("foo2" -> Map("bar2" -> "foo2_bar2_value", "biz1" -> "foo2_biz2_value"))


    val expected = Map (
      "foo" -> Map("bar" -> "foo_bar_value", "biz" -> "foo_biz_value"),
      "foo2" -> Map("bar2" -> "foo2_bar2_value", "biz1" -> "foo2_biz2_value"))

    assert(YamlData.merge(a, b) == expected)

  }

  "merging a map and a map with common keys" should " merge the common keys" in {
    var a :Map[String, Any] = Map ("foo" -> Map("bar" -> "foo_bar_value", "biz" -> "foo_biz_value"))
    var b :Map[String, Any] = Map ("foo" -> Map("bar2" -> "foo2_bar2_value", "biz1" -> "foo2_biz2_value"))

    var expected : Map[String, Any] = Map (
      "foo" -> Map(
        "bar" -> "foo_bar_value", "biz" -> "foo_biz_value",
        "bar2" -> "foo2_bar2_value", "biz1" -> "foo2_biz2_value"))

    assert(YamlData.merge(a, b) == expected)

    a = Map ("foo" -> Map("bar" -> Map( "val1" -> "foo_bar_value", "biz" -> "foo_biz_value")))
    b = Map (
      "foo" -> Map(
        "bar" -> Map( "val1" -> "foo2_bar2_value", "biz1" -> "foo2_biz2_value"),
        "baz" -> Map( "val1" -> "foo2_bar2_value", "biz1" -> "foo2_biz2_value")
      )
    )

    expected = Map (
      "foo" -> Map(
        "bar" -> Map("val1" -> "foo_bar_value", "biz" -> "foo_biz_value", "biz1" -> "foo2_biz2_value"),
        "baz" -> Map( "val1" -> "foo2_bar2_value", "biz1" -> "foo2_biz2_value")
        )
      )

    assert(YamlData.merge(a, b) == expected)


  }

  "merging two strings" should "return the first" in {
    val a = Map("foo" -> "bar")
    val b = Map("foo" -> "bar2")

    assert(YamlData.merge(a, b) == a)

  }

  "merging anything and null" should "return the anything" in {
    val a = Map("foo" -> "bar")
    val b = Map("bar" -> "foo")

    val result = YamlData.merge(a, b)
    assert(result.contains("foo"))
    assert(result.contains("bar"))

    assert(result == Map("foo" -> "bar", "bar" -> "foo"))

  }

}
