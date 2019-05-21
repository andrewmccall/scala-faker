package com.andrewmccall.faker

import java.util.Random
import java.util.concurrent.ThreadLocalRandom

import com.andrewmccall.faker.util.YamlData

class Config (val locale: String = "en", val random: Random = ThreadLocalRandom.current(), val data: Data = new YamlData()) {

  var modules: Map[String, (_) => String] = _

  def loadClasses(): Unit = {
    import org.reflections.Reflections
    val reflections = new Reflections("com.andrewmccall.faker.modules")
    //val annotated = reflections.getTypesAnnotatedWith(classOf[com.andrewmccall.faker.modules.Module])

  }


}
