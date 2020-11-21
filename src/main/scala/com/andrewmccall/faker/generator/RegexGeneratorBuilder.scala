package com.andrewmccall.faker.generator

import com.andrewmccall.faker.Faker

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class RegexGeneratorBuilder (regex: String) extends GeneratorBuilder {

  private val alpha = Seq.range('A', 'Z')
  private val lower = Seq.range('a', 'z')
  private val letters = alpha ++ lower

  class RegexFunctionString(s: String) {
    def replaceAllIn(regex: Regex, replacer: Match => String): String = {
      regex.replaceAllIn(s, replacer)
    }
  }

  implicit def stringToRegexFunctionString(s: String): RegexFunctionString = new RegexFunctionString(s)


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
    * "U3V 3TP"
    */
  override def generate(): Generator = (faker: Faker, String) => {

    regex.replaceAll("\\$/", "")
      .replaceAll("/\\^", "")
      .replaceAll("\\{(\\d+)\\}", "{$1,$1}")
      .replaceAll("\\?", "{0,1}")
      .replaceAllIn("(\\[[^\\]]+\\])\\{(\\d+),(\\d+)\\}".r, matcher =>
        matcher.group(1) * faker.sample(Seq.range(matcher.group(2).toInt, matcher.group(3).toInt + 1)))
      .replaceAllIn("(\\([^\\)]+\\))\\{(\\d+),(\\d+)\\}".r, matcher =>
        matcher.group(1) * faker.sample(Seq.range(matcher.group(2).toInt, matcher.group(3).toInt + 1)))
      .replaceAllIn("(\\\\?.)\\{(\\d+),(\\d+)\\}".r, matcher => {
        val rep = if (matcher.group(1).length > 1) "\\" + matcher.group(1) else matcher.group(1)
        rep * faker.sample(Seq.range(matcher.group(2).toInt, matcher.group(3).toInt + 1))
      }).replaceAllIn("\\((.*?)\\)".r, matcher => faker.sample(matcher.group(1).split('|').toSeq))
      .replaceAllIn("\\[.*?(\\w-\\w).*?\\]".r, matcher =>
        matcher.subgroups.foldLeft(matcher.group(0))(
          (s, g) => {
            val range = g.charAt(0) to g.charAt(2)
            s.replace(s, faker.sample(range).toString)
          }
        ))
      .replaceAllIn("\\[([^\\]]+)\\]".r, matcher =>  faker.sample(matcher.group(1).split("")))
      .replaceAllIn("\\\\w".r, _ => faker.sample(letters).toString)
      .replaceAllIn("\\\\d".r, _ => faker.sample(0 to 9).toString)
  }
}
