package com.andrewmccall.faker.generators

import com.andrewmccall.faker.Faker

abstract class Generator {
  type faker = (Faker, String, Map[String, String]) => String
  def build(): faker
}

class SimpleGenerator(value: String) extends Generator {

  override def build() : faker = (_, _, _) => value

}

