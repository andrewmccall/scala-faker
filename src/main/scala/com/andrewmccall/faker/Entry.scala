package com.andrewmccall.faker

import org.apache.logging.log4j.scala.Logging

sealed trait Entry {
  def get(faker: Faker): String
}

final case class SeqEntry(seq: Seq[String]) extends Entry {
  override def get(faker: Faker): String = seq(faker.config.random.nextInt(seq.size))

  def +:(entry: String): SeqEntry = {
    SeqEntry((seq :+ entry).distinct)
  }

  def ++:(values: Seq[String]): SeqEntry = {
    SeqEntry(seq.union(values).distinct)
  }
}

case class SimpleEntry(value: String) extends Entry {
  override def get(faker: Faker): String = value
  def unapply(s: String): Option[SimpleEntry] = {
    Some(SimpleEntry(s))
  }
}

final case class RegexEntry(pattern: String) extends Entry with Logging {
  override def get(faker: Faker): String = ???

  def unapply(arg: RegexEntry): Option[String] = {
    // It's a regex if it's longer than 4 chars, the first two chars are /^ and the last two are $/
    if (
      arg.pattern.length > 4 &&
        arg.pattern.charAt(0) == '/' &&
        arg.pattern.charAt(1) == '^' &&
        arg.pattern.charAt(arg.pattern.length - 2) == '$' &&
        arg.pattern.charAt(arg.pattern.length - 1) == '/')
      Some(arg.pattern)
    else None
  }

  def unapply(s: String): Option[RegexEntry] = {
    if (
      s.length > 4 &&
        s.charAt(0) == '/' &&
        s.charAt(1) == '^' &&
        s.charAt(s.length - 2) == '$' &&
        s.charAt(s.length - 1) == '/')
      Some(RegexEntry(s))
    else
      None
  }

}

final case class ReplacementEntry(pattern: String) extends Entry {
  val parsedPattern = pattern

  override def get(faker: Faker): String = ???

  def unapply(arg: ReplacementEntry): Option[String] = {
    if (arg.pattern.charAt(0) == '/' && arg.pattern.charAt(arg.pattern.length - 1) == '/') Some(arg.pattern)
    else None
  }

}

final case class Namespace(data: Map[String, Entry]) extends Entry {
  override def get(faker: Faker): String = throw new InvalidKeyException("Calls to get not valid for namespaces.")

  def get(key: String, faker: Faker, locale: Option[String] = None): Entry = {
    data(key)
  }

  def contains(key: String): Boolean = {
    data.contains(key)
  }

  def ++:(values: Map[String, Entry]): Namespace = {
    Namespace(data ++ values)
  }
}

object Entry {
  def getEntry(value: Any): Entry = {
    value match {
      //case map: Map[String, Any] => Namespace(map.map((s, e) => {(s, getEntry(e)})))
      //case seq : Seq[String] => SeqEntry(seq)
      //case e: SeqEntry => e
      case e: RegexEntry => e
      //case e: ReplacementEntry => e
      case e: SimpleEntry => e
    }
  }
}
