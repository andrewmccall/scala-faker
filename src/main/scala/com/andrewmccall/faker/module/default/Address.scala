package com.andrewmccall.faker.module.default

import com.andrewmccall.faker.module.faker
import com.andrewmccall.faker.{Config, Faker}

class Address(faker: Faker, config: Config) {

  @faker("address")
  def countryByCode(code: String = "US"): String = {
    faker("address.country_by_code." + code)
  }

  @faker
  def countryNameToCode(name: String = "united_states"): String ={
    faker("address.country_by_name." +name)
  }

  @faker
  def latitude(): Float = {
    (config.random.nextFloat() * 180) - 90
  }

  @faker
  def longitude: Float = {
    (config.random.nextFloat() * 360) - 180
  }
}
