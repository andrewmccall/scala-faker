package com.andrewmccall.faker.module.books

import com.andrewmccall.faker.module.{faker, namespace}
import com.andrewmccall.faker.{Config, Faker}

@namespace("book")
class Dune (faker: Faker, config: Config) {

  @faker
  def quote(character: String = null) {
    val searchCharacter = if (character == null) {
      faker.sample(faker.fetchKeys("dune.quotes").toSeq)
    } else character.toLowerCase
    faker("dune.quotes" + searchCharacter)
  }

  @faker
  def saying(source: String = null) {

    val searchSource = if (source == null) {
      faker.sample(faker.fetchKeys("dune.sayings").toSeq)
    } else source.toLowerCase
    faker("dune.quotes" + searchSource)

  }

}
