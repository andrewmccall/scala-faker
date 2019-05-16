package com.andrewmccall.faker.modules.default

import com.andrewmccall.faker.modules.Base
import com.andrewmccall.faker.{Config, Faker}

class Address(faker: Faker, config: Config) extends Base {

  def country_by_code(code: String = "US"): String = {
    faker("address.country_by_code." + code)
  }

  def country_name_to_code(name: String = "united_states"): String ={
    faker("address.country_by_name." +name)
  }

  def latitude(): Float = {
    (config.random.nextFloat() * 180) - 90
  }

  def longitude: Float = {
    (config.random.nextFloat() * 360) - 180
  }
}
