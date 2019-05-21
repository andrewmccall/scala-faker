package com.andrewmccall.faker.module

import org.scalatest.{FlatSpec, Matchers}

class ModuleSpec extends FlatSpec with Matchers {

  "snakify" should "properly snakify method names" in {
    Module.snakify("fooBar") shouldBe "foo_bar"
    Module.snakify("FooBar") shouldBe "foo_bar"
    Module.snakify("fooBar2") shouldBe "foo_bar2"
    Module.snakify("fooBarBA") shouldBe "foo_bar_ba"
    Module.snakify("aVeryLongMethodNameShouldAlsoWork") shouldBe "a_very_long_method_name_should_also_work"
  }

  "default arguments" should "get properly returned" in {
    class TestModule
  }

}
