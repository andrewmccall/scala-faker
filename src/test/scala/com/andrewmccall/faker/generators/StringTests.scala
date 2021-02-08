package com.andrewmccall.faker.generators

import org.scalatest._
import flatspec._
import matchers._

trait GeneratorBuilder {
  type Generato = (Storage, String) => String
  def build(): Generato
}

class Storage {

  def apply(key: String, locale:String): String = {
    if (key == "first.name")
      return "Drew"
    else
      return "McCall"
  }

}



class Replace(string: String) extends GeneratorBuilder {

  private val pattern = "#\\{([A-Za-z0-9\\._]+)(:(.*))?\\}".r

  private val basic: Generato = (storage: Storage, locale: String) => {

    val v = pattern.findAllMatchIn(string)
    var start = 0;
    val ret = new StringBuilder()
    while (v.hasNext) {
      val m = v.next()
      ret.append(string.substring(start, m.start))
      start = m.end
      val key = m.group(1)
      val lookupLocale = if (m.group(3) != null) m.group(3) else locale
      ret.append(storage.apply(key, lookupLocale))

    }
    if (start < string.length) ret.append(string.substring(start))
    ret.toString
  }

  private val deleteAndInsert: Generato = (storage: Storage, locale: String) => {

    val v = pattern.findAllMatchIn(string)
    var start = 0;
    while (v.hasNext) {
      val m = v.next()
    }
    string
  }

  private val loop: Generato = (storage: Storage, locale: String) => {

    val v = pattern.findAllMatchIn(string)
    var start = 0;
    while (v.hasNext) {
      val m = v.next();
    }
    string
  }

  override def build(): Generato = basic
}


abstract class Lookup(string: String) extends GeneratorBuilder {

}

class StringTests extends AnyFlatSpec with should.Matchers {

  "Parsing a string" should "return a generator" in {

    val generators = new Storage()

    val string = "Hello #{first.name:en_GB}, how are you"
    val something = new Replace(string).build()

    assert(something(generators, "") == "Hello Drew, how are you")
  }

}
