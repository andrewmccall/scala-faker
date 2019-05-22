package com.andrewmccall.faker.module.blockchain

import java.security.MessageDigest

import com.andrewmccall.faker.helpers.Base58
import com.andrewmccall.faker.module.faker
import com.andrewmccall.faker.{Config, Faker}

class Tezos(faker: Faker, config: Config) {


  private val tz1 = Array[Int](6, 161, 159).map(_.toByte)
  private val KT1 = Array[Int](2, 90, 121).map(_.toByte)
  private val edpk = Array[Int](13, 15, 37, 217).map(_.toByte)
  private val edsk = Array[Int](13, 15, 58, 7).map(_.toByte)
  private val edsig = Array[Int](9, 245, 205, 134, 18).map(_.toByte)
  private val o = Array[Int](5, 116).map(_.toByte)


  @faker
  def account: String = {
    encode_tz(tz1, 20)
  }

  @faker
  def contract: String = {
    encode_tz(KT1, 20)
  }

  @faker
  def operation: String = {
    encode_tz(o, 32)
  }

  @faker
  def signature: String = {
    encode_tz(edsig, 64)
  }


  private def encode_tz(prefix: Array[Byte], payload_size: Int): String = {
    val array = Array.fill[Byte](payload_size)(0)
    config.random.nextBytes(array)
    val checksum = MessageDigest.getInstance("SHA-256").digest(array)
    Base58(prefix ++ array ++ checksum.take(3))
  }

}
