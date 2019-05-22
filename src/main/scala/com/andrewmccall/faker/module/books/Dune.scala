package com.andrewmccall.faker.module.books

import com.andrewmccall.faker.{Config, Faker}

class Dune (faker: Faker, config: Config) {

  def quote(character: String = null) {
    val searchCharacter = if (character == null) {
      faker.sample(faker.fetchKeys("dune.quotes").toSeq)
    } else character.toLowerCase
    faker("dune.quotes" + searchCharacter)
  }

  def saying(source: String = null) {

    val searchSource = if (character == null) {
      faker.sample(faker.fetchKeys("dune.sayings").toSeq)
    } else source.toLowerCase
    faker("dune.quotes" + searchSource)
    
  }

}
