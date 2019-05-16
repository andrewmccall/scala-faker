package com.andrewmccall.faker

import java.util.Random
import java.util.concurrent.ThreadLocalRandom

import com.andrewmccall.faker.util.YamlData

class Config (val locale: String = "en", val random: Random = ThreadLocalRandom.current(), val data: Data = new YamlData()) {
}
