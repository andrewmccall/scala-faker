package com.andrewmccall.faker.module.blockchain

import com.andrewmccall.faker.module.faker
import com.andrewmccall.faker.{Config, Faker}

class Ethereum(faker: Faker, config: Config) {

  @faker
  def address(): String = {
    val hex_alphabet = "0123456789abcdef"
    var add = "0x"
    1 to 40 foreach { _ => add += faker.sample(hex_alphabet.split("")) }
    add
  }

}
