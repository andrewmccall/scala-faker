package com.andrewmccall.faker

import org.apache.logging.log4j.scala.Logging

import scala.annotation.tailrec
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

import com.andrewmccall.faker.module.ScalaModule

class Faker(private[faker] val config: Config = new Config()) extends Logging {

  private val alpha = Seq.range('A', 'Z')
  private val lower = Seq.range('a', 'z')
  private val letters = alpha ++ lower
  private val modules = ScalaModule.loadModules(this, this.config)

  //private val root: Namespace = _

  def apply(string: String): String = {
    parse(string)
  }

  private[faker] def parse(string: String, parentKey: String = null, locale: String = this.config.locale): String = {

    val templateRegex = "#\\{(([A-Za-z]+\\.)?([^\\}]+))(:.*)?\\}?"
    val templateMatch = (".*" + templateRegex + ".*").r
    val keyRegex = "^([A-Za-z_]+\\.([A-Za-z_]+\\.?)+)$".r

    logger.info(s"Parsing $string with parent $parentKey")
    // Start by trying to fetch the shorthand for a single key

    // if we have a template, there is no need to try to find the key.
    val value = string match {
      case templateMatch(_*) => {
        string
      }
      case keyRegex(_*) => {
        val key = getKey(string, parentKey, locale)
        if (this.modules.contains(key)) {
          parse(fetch(key, modules), if(isNamespace(key)) getNamespace(key) else parentKey)
        }
        else if (this.config.data.contains(key)) parse(fetch(key, config.data), if(isNamespace(key)) getNamespace(key) else parentKey)
        else string
      }
      case _ => {
        string
      }
    }


    templateRegex.r.replaceAllIn(value, m => {

      val key = m.group(1).toLowerCase
      val cls = if (m.group(2) != null) {
        m.group(2).dropRight(1).toLowerCase
      } else if (parentKey != null) {
        parentKey.toLowerCase
      } else null

      val meth = m.group(3)
      val parsedLocale = if (m.group(4) != null) m.group(4) else locale

      // see if we have a module to handle this key.
      // otherwise grab it from the data.
      val parsedKey = getKey(key, cls, locale)

      if (this.modules.contains(key))
        parse(fetch(parsedKey, modules), cls, parsedLocale)
      else if (this.config.data.contains(parsedKey))
        parse(fetch(parsedKey, config.data), cls, parsedLocale)
      else {
        logger.info(s"Key not found $key")
        ""
      }
    })
  }

  private[faker] def isNamespace(s: String) : Boolean = {
    s.contains('.')
  }

  private[faker] def getNamespace(str: String): String = {
    str.toLowerCase().split("\\.").dropRight(1).mkString(".")
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
    val parts = key.toLowerCase().split("\\.")

    val parsedParent = if (parentKey != null && parentKey.startsWith("en.faker.")) parentKey.substring(9) else parentKey

    // if the key is long enough and looks well constructed, we'll just return it.
    if (parts.length >= 3 && parts(1).equals("faker")) {
      if (parts(0) == "en" || config.data.contains(key)) key
      else parts.drop(1).+:("en").mkString(".")
    } else {
      // check if we can get based just on what we have with additional en.faker
      val localekey = (Seq(locale, "faker") ++ parts).mkString(".")
      if (config.data.contains(localekey)) localekey
      else if (parsedParent != null && parsedParent.nonEmpty){

        val localekey = (Seq(locale, "faker", parsedParent) ++ parts).mkString(".")
        if (config.data.contains(localekey)) localekey
        else {
          val localekey2 = (Seq("en", "faker", parsedParent) ++ parts).mkString(".")
          if (config.data.contains(localekey2)) localekey2
          else (Seq("en", "faker") ++ parts).mkString(".")
        }
      }
      else (Seq("en", "faker") ++ parts).mkString(".")

    }
  }

  /**
    * Helper function that grabs a key and returns the value or randomly selects one element of an array and returns
    * that if the return type is an array.
    *
    * If the returned key contains the regex anchors it will be regexified. If it contains the replacement anchors it
    * will be replaced.
    *
    * @param key the key
    * @return a single value or a single entry from an array
    */
  private[faker] def fetch(key: String, data: Data): String = {



    val fetched = data.fetch(key).get match {
      case SeqEntry(s) => sample(s)
      case SimpleEntry(s) => s
    }
    if (isRegex(fetched))
      regexify(fetched)
    else if (isReplacement(fetched))
      bothify(fetched)
    else fetched
  }

  /**
    * Fetches any subkeys contained in a key
    * :TODO: Does this belong in data? I'm searching here because I know it wont happen in a module, at least not so far.
    *
    * @param key the parent key
    * @return an Iterable[String] of subkeys
    */
  private[faker] def fetchKeys(key: String) : Iterable[String] = {
    val parsedKey = getKey(key)
    import scala.collection.JavaConverters._
    if (!config.data.contains(parsedKey)) Iterable.empty[String]
    else config.data.fetch(parsedKey) match {
      case j: java.util.Map[String, _] => j.asScala.toMap.keys
      case o => o.asInstanceOf[Map[String, Any]].keys
    }
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
      .replaceAllIn("(\\\\?.)\\{(\\d+),(\\d+)\\}".r, matcher => {
        val rep = if (matcher.group(1).length > 1) "\\" + matcher.group(1) else matcher.group(1)
        rep * sample(Seq.range(matcher.group(2).toInt, matcher.group(3).toInt + 1))
      }).replaceAllIn("\\((.*?)\\)".r, matcher => sample(matcher.group(1).split('|').toSeq))
      .replaceAllIn("\\[.*?(\\w-\\w).*?\\]".r, matcher =>
        matcher.subgroups.foldLeft(matcher.group(0))(
          (s, g) => {
            val range = g.charAt(0) to g.charAt(2)
            s.replace(s, sample(range).toString)
          }
        ))
      .replaceAllIn("\\[([^\\]]+)\\]".r, matcher =>  sample(matcher.group(1).split("")))
      .replaceAllIn("\\\\w".r, _ => sample(letters).toString)
      .replaceAllIn("\\\\d".r, _ => sample(0 to 9).toString)
  }



  class RegexFunctionString(s: String) {
    def replaceAllIn(regex: Regex, replacer: Match => String): String = {
      regex.replaceAllIn(s, replacer)
    }
  }

  implicit def stringToRegexFunctionString(s: String): RegexFunctionString = new RegexFunctionString(s)

}

object Faker {

  val defaultLocale : String = "en"

}
