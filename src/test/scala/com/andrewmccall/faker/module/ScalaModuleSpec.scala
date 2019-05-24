package com.andrewmccall.faker.module

import org.scalatest.{FlatSpec, Matchers}

class ScalaModuleSpec extends FlatSpec with Matchers {

  "snakify" should "properly snakify method names" in {
    ScalaModule.snakify("fooBar") shouldBe "foo_bar"
    ScalaModule.snakify("FooBar") shouldBe "foo_bar"
    ScalaModule.snakify("fooBar2") shouldBe "foo_bar2"
    ScalaModule.snakify("fooBarBA") shouldBe "foo_bar_ba"
    ScalaModule.snakify("aVeryLongMethodNameShouldAlsoWork") shouldBe "a_very_long_method_name_should_also_work"
  }

  "default arguments" should "get properly returned" in {
    class TestModule
  }

}
