package com.andrewmccall.faker

sealed trait Entry {
  def get: String
}
final case class SeqEntry(seq: Seq[String]) extends Entry {
  override def get: String = ???
}
case class StringEntry(value: String) extends Entry {
  override def get: String = ???
}
final case class RegexEntry (pattern: String) extends Entry {
  override def get: String = ???
}
final case class ReplacementEntry(pattern: String) extends Entry {
  val parsedPattern = pattern

  override def get: String = ???

  def unapply(arg: ReplacementEntry): Option[String] = {
    if (arg.pattern.charAt(0) == '/' && arg.pattern.charAt(arg.pattern.length -1) == '/') Some(arg.pattern)
    else None
  }

}
final case class Namespace (data: Map[String, Entry]) extends Entry {
  override def get: String = throw new InvalidKeyException("Namespace, not a ValueEntry")
  def get(key: String): Entry = {
    data.get(key).get
  }
  def ++:(values: Map[String, Entry]): Namespace = {
    Namespace(data ++ values)
  }
}