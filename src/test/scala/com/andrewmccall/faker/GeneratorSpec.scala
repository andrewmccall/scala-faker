package com.andrewmccall.faker

import java.util.Locale

import com.andrewmccall.faker.generator.StringGeneratorBuilder
import org.scalatest.{FlatSpec, Matchers}

class GeneratorSpec extends FlatSpec with Matchers {

  "Generator for a simple string" should "return that string" in {
    val string = "Some String"

    val generator = new StringGeneratorBuilder(string).generate(null);
    assert(generator.apply(null, null) == string)
  }
}
