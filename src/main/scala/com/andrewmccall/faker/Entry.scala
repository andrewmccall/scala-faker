package com.andrewmccall.faker

sealed trait Entry
final case class SeqEntry(seq: Seq[String]) extends Entry
final case class StringEntry(value: String) extends Entry
final case class Namespace (data: Data) extends Entry