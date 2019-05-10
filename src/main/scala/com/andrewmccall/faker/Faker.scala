package com.andrewmccall.faker

import java.util.Random
import java.util.concurrent.ThreadLocalRandom

import com.andrewmccall.faker.util.YamlData
class Faker {}
object Faker {

  var locale = "en"

  var random : Random = ThreadLocalRandom.current

  var data : Data = new YamlData()

  def setLocale(locale: String): Unit = {
    this.locale = locale
  }

  def setRandom(random: Random): Unit = {
    this.random = random
  }

  def setData( data: Data) : Unit = {
    this.data = data
  }
}
