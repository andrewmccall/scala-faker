package com.andrewmccall.faker.helpers

object Base58 {

  private val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray
  private val ENCODED_ZERO = ALPHABET(0)
  private val INDEXES = new Array[Int](128)

  def apply(input: Array[Byte]): String = {

    if (input.length == 0) return ""
    // Count leading zeros.
    var zeros = 0
    while ( {
      zeros < input.length && input(zeros) == 0
    }) zeros += 1
    // Convert base-256 digits to base-58 digits (plus conversion to ASCII characters)
    val encoded = new Array[Char](input.length * 2) // upper bound
    var outputStart = encoded.length
    var inputStart = zeros
    while ( {
      inputStart < input.length
    }) {
      encoded({
        outputStart -= 1; outputStart
      }) = ALPHABET(divmod(input, inputStart, 256, 58))
      if (input(inputStart) == 0) inputStart += 1 // optimization - skip leading zeros
    }
    // Preserve exactly as many leading encoded zeros in output as there were leading zeros in input.
    while ( {
      outputStart < encoded.length && encoded(outputStart) == ENCODED_ZERO
    }) outputStart += 1
    while ( {
      {
        zeros -= 1; zeros
      } >= 0
    }) encoded({
      outputStart -= 1; outputStart
    }) = ENCODED_ZERO
    // Return encoded string (including encoded leading zeros).
    new String(encoded, outputStart, encoded.length - outputStart)
  }

  private def divmod(number: Array[Byte], firstDigit: Int, base: Int, divisor: Int) = { // this is just long division which accounts for the base of the input digits
    var remainder = 0
    var i = firstDigit
    while ( {
      i < number.length
    }) {
      val digit = number(i).toInt & 0xFF
      val temp = remainder * base + digit
      number(i) = (temp / divisor).toByte
      remainder = temp % divisor

      {
        i += 1; i - 1
      }
    }
    remainder.toByte
  }

}
