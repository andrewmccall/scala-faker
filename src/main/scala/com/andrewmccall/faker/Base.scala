package com.andrewmccall.faker

import java.io.File
import java.util.concurrent.ThreadLocalRandom

trait Base {

  private def random = new ThreadLocalRandom()

  def main(args: Array[String]): Unit = {

    val path = getClass.getResource("/resources/com/andrewmccall/faker/")
    val folder = new File(path.getPath)
    if (folder.exists && folder.isDirectory)
      folder.listFiles
        .toList
        .foreach(file => println(file.getName))

  }

}
