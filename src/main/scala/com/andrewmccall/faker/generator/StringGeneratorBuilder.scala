package com.andrewmccall.faker.generator

import com.andrewmccall.faker.Faker

/**
  * A StringGenerator creates a Generator that works on Strings. It will parse the string and properly construct a
  * Generator that will lookup keys in the provided Faker
  *
  * @param value
  */
case class StringGeneratorBuilder(value:String) extends GeneratorBuilder {
  private val generator =  (faker: Faker, locale: String) => value
  override def generate(): Generator = generator
}
