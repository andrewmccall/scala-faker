package com.andrewmccall.faker.generator

import com.andrewmccall.faker.Faker

case class LookupGeneratorBuilder(key:String) extends GeneratorBuilder {

  private val generator = (faker: Faker, locale: String) => faker.lookup(key, locale)(faker, locale)
  override def generate(): Generator = generator
}
