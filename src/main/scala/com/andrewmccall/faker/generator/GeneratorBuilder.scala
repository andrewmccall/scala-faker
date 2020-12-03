package com.andrewmccall.faker.generator

import com.andrewmccall.faker.Faker
import scala.collection.JavaConverters._

/**
  * A Generator produces some generated data.
  */
trait GeneratorBuilder {
  type Generator = (Faker, String) => String
  def generate(): Generator
}

object GeneratorBuilder {

  def toEntry(value: Any): Option[GeneratorBuilder] = {

    value match {

      case seq : Seq[String] => Some(SeqGeneratorBuilder(seq))
      case jul : java.util.List[String] => Some(SeqGeneratorBuilder(jul.asScala))
      case str: String => Some(stringToEntry(str))
      case _ => None
    }
  }

  def stringToEntry(str: String): GeneratorBuilder = {
    if (Faker.isRegex(str)) new RegexGeneratorBuilder(str)
    else if (Faker.isReplacement(str)) new ReplacementBuilder(str)
    else new StringGeneratorBuilder(str)
  }
}