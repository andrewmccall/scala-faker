package com.andrewmccall.faker.module.blockchain

import java.security.MessageDigest

import com.andrewmccall.faker.{Config, Faker}
import com.andrewmccall.faker.helpers.Base58
import com.andrewmccall.faker.module.faker


class Bitcoin(faker: Faker, config: Config) {

  private val MAIN = Array[Byte](0)
  private val TESTNET = Array[Byte](111)

  @faker
  def address() : String = {
    address_for(MAIN)
  }

  @faker
  def testnet_address : String = {
    address_for(TESTNET)
  }

  private def address_for(network: Array[Byte]): String = {
    val array = Array.fill[Byte](20)(0)
    this.config.random.nextBytes(array)
    val checksum = MessageDigest.getInstance("SHA-256").digest(array)
    Base58(network ++ array ++ checksum.take(3))
  }
}
