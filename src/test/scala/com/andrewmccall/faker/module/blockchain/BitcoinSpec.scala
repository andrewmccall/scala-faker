package com.andrewmccall.faker.module.blockchain

import com.andrewmccall.faker.{Config, Faker}
import org.scalatest.{FlatSpec, Matchers}

class BitcoinSpec extends FlatSpec with Matchers {

  val config = new Config
  val faker = new Faker(config)

  "calls" should "return valid addresses" in {
    val bitcoin = new Bitcoin(faker, config)
    val address = bitcoin.address()
    address.length should (be  < (36) and be > (25))
    address should startWith ("1")

    val testAddress = bitcoin.testnet_address
    testAddress.length should (be  < (36) and be > (25))
    // TODO: This doesn't work?
    // testAddress should (startWith("m") or startWith("n"))
  }

}
