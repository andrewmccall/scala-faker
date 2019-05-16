package com.andrewmccall.faker

import org.apache.logging.log4j.scala.Logging

import scala.annotation.tailrec
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class Faker(config: Config) extends Logging {

  private val alpha = Seq.range('A', 'Z')
  private val lower = Seq.range('a', 'z')

  def apply(string: String): String = {
    parse(string)
  }

  private[faker] def parse(string: String, parentKey: String = null, locale: String = this.config.locale): String = {

    logger.info(s"Parsing $string with parent $parentKey")
    // Start by trying to fetch the shorthand for a single key
    val value = {
      val key = getKey(string, parentKey, locale)
      if (this.config.data.contains(key)) fetch(key) else string
    }

    "#\\{(([A-Za-z]+\\.)?([^\\}]+))(:.*)?\\}?".r.replaceAllIn(value, m => {

      val key = m.group(1).toLowerCase
      val cls = if (m.group(2) != null) m.group(2).dropRight(1) else parentKey
      val meth = m.group(3)
      val locale = m.group(4)

      logger.trace(s"Parsing $key - class ($cls)  method ($meth) locale ($locale)")

      // see if we have a module to handle this key.
      // otherwise grab it from the data.
      if (config.data.contains(key))
        parse(fetch(key), cls)
      else {
        logger.info(s"Key not found $key")
        ""
      }
    })
  }

  /**
    * Helper method that takes a string and replaces any # placeholders it finds with random digits.
    *
    * @param numberString the string that may or may not container placeholders
    * @param leadingZeros are zeros allowed to start the string default is false.
    * @param pos          the current position, default is 0
    * @return a string with the placeholders replaced.
    */
  @tailrec
  private[faker] final def numerify(numberString: String, leadingZeros: Boolean = false, pos: Int = 0): String = {

    def randomInt(allowZeros: Boolean = true): Int = {
      if (allowZeros) config.random.nextInt(10) else config.random.nextInt(9) + 1
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

  /**
    * Helper method that takes a string and replaces any ? characters with a random letter.
    *
    * @param str the source string which may or may not contain placeholders.
    * @return a string with the placeholders replaced.
    */
  private[faker] def letterify(str: String): String = {
    str.map(c => if (c.equals('?')) sample(alpha) else c)
  }

  /**
    * Replaces both letters and numbers in a string with random values.
    *
    * @param str the source string
    * @return a new string with values replaces.
    */
  private[faker] def bothify(str: String): String = {
    val trimmed = if (str.head == '/' && str.last == '/') str.dropRight(1).drop(1) else str
    letterify(numerify(trimmed))
  }

  private[faker] def getKey(key: String, parentKey: String = null, locale: String = this.config.locale): String = {
    val parts = key.split("\\.")

    // if the key is long enough and looks well constructed, we'll just return it.
    if (parts.length >= 3 && parts(1).equals("faker")) {
      if (parts(0) == "en" || config.data.contains(key)) key
      else parts.drop(1).+:("en").mkString(".")
    } else {
      // check if we can get based just on what we have with additional en.faker
      val localekey = (Seq(locale, "faker") ++ parts).mkString(".")
      if (config.data.contains(localekey)) localekey
      else if (parentKey != null && parentKey.nonEmpty){

        val localekey = (Seq(locale, "faker", parentKey) ++ parts).mkString(".")
        if (config.data.contains(localekey)) localekey
        else
          (Seq("en", "faker", parentKey) ++ parts).mkString(".")
      }
      else (Seq("en", "faker") ++ parts).mkString(".")

    }
  }

  /**
    * Helper function that grabs a key and returns the value or randomly selects one element of an array and returns
    * that if the return type is an array.
    *
    * @param key the key
    * @return a single value or a single entry from an array
    */
  private[faker] def fetch(key: String): String = {

    val fetched = config.data.fetch(key) match {
      case Right(s) => sample(s)
      case Left(string) => string
    }
    if (isRegex(fetched))
      regexify(fetched)
    else if (isReplacement(fetched))
      bothify(fetched)
    fetched
  }

  /**
    * Checks if the string has the anchors. if so, assume it's a regex.
    *
    * @param s the string to check
    * @return true if the string appears to be a regex.
    */
  private[faker] def isRegex(s: String): Boolean = {
    if ("/\\^.*\\$/".r.findAllIn(s).nonEmpty)
      true
    else
      false
  }

  /**
    * Checks if the string has the anchors. if so, assume it's a regex.
    *
    * @param s the string to check
    * @return true if the string appears to be a regex.
    */
  private[faker] def isReplacement(s: String): Boolean = {
    if ("/.*/".r.findAllIn(s).nonEmpty)
      true
    else
      false
  }

  /**
    * Returns a single random entry from a Seq.
    *
    * @param s the Seq
    * @tparam T the type of elements of this Seq.
    * @return a single random entry.
    */
  private[faker] def sample[T](s: Seq[T]): T = {
    s(config.random.nextInt(s.size))
  }

  /** Given a regular expression, attempt to generate a string
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
  private[faker] def regexify(reg: String): String = {

    reg.replaceAll("\\$/", "")
      .replaceAll("/\\^", "")
      .replaceAll("\\{(\\d+)\\}", "{$1,$1}")
      .replaceAll("\\?", "{0,1}")
      .replaceAllIn("(\\[[^\\]]+\\])\\{(\\d+),(\\d+)\\}".r, matcher =>
        matcher.group(1) * sample(Seq.range(matcher.group(2).toInt, matcher.group(3).toInt + 1)))
      .replaceAllIn("(\\([^\\)]+\\))\\{(\\d+),(\\d+)\\}".r, matcher =>
        matcher.group(1) * sample(Seq.range(matcher.group(2).toInt, matcher.group(3).toInt + 1)))
  }


  class RegexFunctionString(s: String) {
    def replaceAllIn(regex: Regex, replacer: Match => String): String = {
      regex.replaceAllIn(s, replacer)
    }
  }

  implicit def stringToRegexFunctionString(s: String): RegexFunctionString = new RegexFunctionString(s)

  /*

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
