package com.andrewmccall.faker.module.helpers

import org.scalatest.{FlatSpec, Matchers}

class Base58Spec extends FlatSpec with Matchers {

  val hex = "0077BFF20C60E522DFAA3350C39B030A5D004E839AF415766B"
  val base58 = "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2"

  "hex string" should "get Base58 encoed" in {
    Base58(hexStringToByteArray(hex)) shouldBe base58
  }

  private def hexStringToByteArray(s: String): Array[Byte] = {
    val len = s.length
    val data = new Array[Byte](len / 2)
    var i = 0
    while (i < len) {
      data(i / 2) = ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16)).toByte
      i += 2
    }
    data
  }

}
