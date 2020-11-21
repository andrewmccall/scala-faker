package com.andrewmccall.faker.generator

import com.andrewmccall.faker.Faker

/**
  * A Generator produces some generated data.
  */
trait GeneratorBuilder {

  type Generator = (Faker, String) => String

  def generate(): Generator
}
