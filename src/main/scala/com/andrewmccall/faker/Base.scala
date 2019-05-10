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
    val fetched = Faker.data.fetch(key) match {
      case Right(sample) => sample(Faker.random.nextInt(sample.size))
      case Left(string) => string
    }
    if (isRegex(fetched))
      regexify(fetched)
    else
    fetched
  }

  /**
    * Checks if the string has the anchors. if so, assume it's a regex.
    * @param s the string to check
    * @return true if the string appears to be a regex.
    */
  private[faker]  def isRegex(s: String) : Boolean = {
    if ("^/".r.findFirstIn(s).isDefined &&
      "/$".r.findFirstIn(s).isDefined)
      true
    else
      false
  }

  /**  Given a regular expression, attempt to generate a string
  * that would match it.  This is a rather simple implementation,
  * so don't be shocked if it blows up on you in a spectacular fashion.
  *
  * It does not handle ., *, unbounded ranges such as {1,},
  * extensions such as (?=), character classes, some abbreviations
  * for character classes, and nested parentheses.
  *
  * I told you it was simple. :) It's also probably dog-slow,
  * so you shouldn't use it.
  *
  * It will take a regex like this:
  *
  * /^[A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {1,2}[0-9][ABD-HJLN-UW-Z]{2}$/
  *
  * and generate a string like this:
  *
  * "U3V  3TP"
  */


  def regexify(reg: String) : String = {
    return reg;
  }
  /*
    reg.r

    reg
      .gsub(%r{^\/?\^?}, '').gsub(%r{\$?\/?$}, '') # Ditch the anchors
    .gsub(/\{(\d+)\}/, '{\1,\1}').gsub(/\?/, '{0,1}') # All {2} become {2,2} and ? become {0,1}
  .gsub(/(\[[^\]]+\])\{(\d+),(\d+)\}/) { |_match| Regexp.last_match(1) * sample(Array(Range.new(Regexp.last_match(2).to_i, Regexp.last_match(3).to_i))) }                # [12]{1,2} becomes [12] or [12][12]
  .gsub(/(\([^\)]+\))\{(\d+),(\d+)\}/) { |_match| Regexp.last_match(1) * sample(Array(Range.new(Regexp.last_match(2).to_i, Regexp.last_match(3).to_i))) }                # (12|34){1,2} becomes (12|34) or (12|34)(12|34)
  .gsub(/(\\?.)\{(\d+),(\d+)\}/) { |_match| Regexp.last_match(1) * sample(Array(Range.new(Regexp.last_match(2).to_i, Regexp.last_match(3).to_i))) }                      # A{1,2} becomes A or AA or \d{3} becomes \d\d\d
  .gsub(/\((.*?)\)/) { |match| sample(match.gsub(/[\(\)]/, '').split('|')) } # (this|that) becomes 'this' or 'that'
  .gsub(/\[([^\]]+)\]/) { |match| match.gsub(/(\w\-\w)/) { |range| sample(Array(Range.new(*range.split('-')))) } } # All A-Z inside of [] become C (or X, or whatever)
  .gsub(/\[([^\]]+)\]/) { |_match| sample(Regexp.last_match(1).split('')) } # All [ABC] become B (or A or C)
  .gsub('\d') { |_match| sample(Numbers) }
  .gsub('\w') { |_match| sample(Letters) }
  }

  # Helper for the common approach of grabbing a translation
  # with an array of values and selecting one of them.
  def fetch(key)
  fetched = sample(translate("faker.#{key}"))
  if fetched&.match(%r{^\/}) && fetched&.match(%r{\/$}) # A regex
  regexify(fetched)
  else
  fetched
  end
  end
  */



}
