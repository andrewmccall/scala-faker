package com.andrewmccall.faker

import org.scalatest.{FlatSpec, Matchers}

class NamespaceSpec extends FlatSpec with Matchers {

  "A map of strings" should "be converted to a namespace" in {
    val map = Map("one" -> Some(SimpleEntry("1value")), "two" -> Some(SimpleEntry("2value")))
    val ns = Namespace(map)
    assert(ns.contains("one"))
    assert(ns.contains("two"))

    val faker = new Faker()
    assert(ns.get("one", faker).get.get(faker) == "1value")

  }

  "Combining a namespace with a map" should "add the elements to the namespace" in {
    val map = Map("one" -> Some(SimpleEntry("1value")), "two" -> Some(SimpleEntry("2value")))
    val other = Map("three" -> Some(SimpleEntry("3value")), "four" -> Some(SimpleEntry("4value")))
    val ns = Namespace(map) ++ other

    assert(ns.contains("one"))
    assert(ns.contains("two"))
    assert(ns.contains("three"))
    assert(ns.contains("four"))

    val faker = new Faker()
    assert(ns.get("one", faker).get.get(faker) == "1value")
    assert(ns.get("three", faker).get.get(faker) == "3value")
  }

}
