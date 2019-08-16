package com.andrewmccall.faker

import java.util.Random
import java.util.concurrent.ThreadLocalRandom

import com.andrewmccall.faker.yaml.YamlData

class Config(val locale: String = Faker.defaultLocale, val random: Random = ThreadLocalRandom.current(), val data: Data = new YamlData()) {

  //val rootNamespace: Namespace = _

  def withRandom(random: Random): Unit = {

  }

  def withYaml(resource: String): Config = {
    this
  }

  def withModule(): Config = {
    this
  }

}

object Config {

  def withDefaultConfig() = {
    new Config()
      .withYaml("Some string")
  }

}
