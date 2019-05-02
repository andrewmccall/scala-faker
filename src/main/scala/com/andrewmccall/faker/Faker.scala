package com.andrewmccall.faker

import java.util.Random
import java.util.concurrent.ThreadLocalRandom

object Faker {

  var locale = "en"

  var random : Random = ThreadLocalRandom.current

  def setLocale(locale: String): Unit = {
    this.locale = locale
  }

  def setRandom(random: Random): Unit = {
    this.random = random
  }
}
