package com.andrewmccall.faker

import org.apache.logging.log4j.scala.Logging

import scala.collection.JavaConverters._


/**
  * An Entry is needs to either provide a Generator or another Entry. At a point it can provide the key or value.
  */
sealed trait Entry {
  def get(faker: Faker): String
}

final case class SeqEntry(seq: Seq[String]) extends Entry {
  override def get(faker: Faker): String = seq(faker.config.random.nextInt(seq.size))

  def + (entry: String): SeqEntry = {
    SeqEntry((this.seq :+ entry).distinct)
  }

  def ++ (values: Seq[String]): SeqEntry = {
    SeqEntry(seq.union(values).distinct)
  }
}

case class SimpleEntry(value: String) extends Entry {
  override def get(faker: Faker): String = value

}

final case class RegexEntry(pattern: String) extends Entry with Logging {
  override def get(faker: Faker): String = ???

}

final case class ReplacementEntry(pattern: String) extends Entry {
  val parsedPattern = pattern

  override def get(faker: Faker): String = ???

}

final case class Namespace(data: Map[String, Option[Entry]]) extends Entry {
  override def get(faker: Faker): String = throw new InvalidKeyException("Calls to get not valid for namespaces.")

  def get(key: String, faker: Faker, locale: Option[String] = None): Option[Entry] = {
    data(key)
  }

  def contains(key: String): Boolean = {
    data.contains(key)
  }

  def +(entry: (String,  Entry)) : Namespace = {

    val newData = data + (entry._1 -> Some(entry._2))
    Namespace(newData)
  }

  def ++(values: Map[String, Option[Entry]]): Namespace = {
    Namespace(data ++ values)
  }
}

object Entry {


}
