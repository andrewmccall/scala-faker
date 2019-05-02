package com.andrewmccall.faker.util

import org.scalatest.FlatSpec

class YamlLoaderSpec extends FlatSpec{

  "some files" should "get loaded" in {
    val files = new YamlLoader().getFilesForClass(this.getClass, "test/yaml")
    assert(files.length == 2)
    assert(files(0).endsWith("a.yaml"))
    assert(files(1).endsWith("b.yaml"))
  }

  "Loading without a Class or path" should "load the base YAML" in {
    var files = new YamlLoader().getFiles()
    assert(files.length > 10)
    files.foreach(println)
  }

  "Call to loadYaml" should "load all the files and give us a big map" in {
    val data = new YamlLoader().loadYaml()
    data.foreach(i => println(s"key: ${i._1} valueType: ${i._2.getClass.toString} value ${i._2}"))
  }

  "A fetch call" should "get the right key" in {
    val data = new YamlLoader().loadYaml()

  }

}
