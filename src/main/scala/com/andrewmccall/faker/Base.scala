package com.andrewmccall.faker

import scala.annotation.tailrec

object Base {

  val alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

  /**
    * Helper method that takes a string and replaces any # placeholders it finds with random digits.
    * @param numberString the string that may or may not container placeholders
    * @param leadingZeros are zeros allowed to start the string default is false.
    * @param pos the current position, default is 0
    * @return a string with the placeholders replaced.
    */
  @tailrec
  def numerify(numberString: String, leadingZeros: Boolean = false, pos: Int = 0): String = {

    def randomInt(allowZeros: Boolean = true): Int = {
      if (allowZeros) Faker.random.nextInt(10) else Faker.random.nextInt(9) + 1
    }

    if (pos == numberString.length) numberString
    else {
      if (numberString.charAt(pos).equals('#'))
        numerify(
          numberString.substring(0, pos) + randomInt(leadingZeros) + numberString.substring(pos + 1),
          leadingZeros = true,
          pos + 1)
      else numerify(numberString, leadingZeros, pos + 1)
    }
  }

  def letterify(str: String) : String = {
    str.map(c => if (c.equals('?')) alpha(Faker.random.nextInt.abs % alpha.size) else c)
  }

  def bothify(str: String) : String = {
    letterify(numerify(str))
  }

  def fetch(key: String) : String = {
    Faker.data.fetch(key) match {
      case Right(sample) => sample(Faker.random.nextInt(sample.size))
      case Left(string) => string
    }
  }


}
