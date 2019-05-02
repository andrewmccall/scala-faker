package com.andrewmccall.faker.util

import java.io.{File, FileReader, Reader}

import com.andrewmccall.faker.Loader
import org.yaml.snakeyaml.Yaml

class YamlLoader extends Loader {

  def loadYaml(): Map[String, Any] = {

    def yaml = new Yaml()
    val files = getFiles()

    files.foreach(println)

    val data = files.map(x => {
      println(s"Loading file: ${x}")
      import scala.collection.JavaConverters._
      yaml.load(new FileReader(x)).asInstanceOf[java.util.Map[String, Any]].asScala.toMap
    })

    data.foreach(println)
    data.reduce( _++_ )
  }

  private[util] def getFiles(caller:Class[_] = null, path: String = null): Array[String] = {

    // load the base stuff
    val basePath = "com/andrewmccall/faker/locales"
    val files: Array[String] = getFilesForClass(this.getClass, basePath)
    if (caller != null) files ++ getFilesForClass(caller, path)
    files
  }

  private[util] def getFilesForClass(caller:Class[_], path:String) : Array[String] = {

    import java.net.URISyntaxException
    import java.util.jar.JarFile

    println(s"Loading files from: ${caller.getProtectionDomain.getCodeSource.getLocation.getPath}")

    val jarFile = new File(caller.getProtectionDomain.getCodeSource.getLocation.getPath)

    if (jarFile.isFile) { // Run with JAR file
      val jar = new JarFile(jarFile)
      val entries = jar.entries //gives ALL entries in jar
      import scala.collection.JavaConversions._
      val newFiles = entries.filter(_.getName.startsWith(path + "/")).filter(_.getName.endsWith(".yml")).map(_.getName)
      jar.close()
      newFiles.toArray
    }
    else { // Run with IDE
      val url = classOf[File].getResource("/" + path)
      if (url != null) try {
        val apps = new File(url.toURI).listFiles()
        apps.filter(f => f.isFile && (f.getName.endsWith(".yml") || f.getName.endsWith("yaml"))).map(_.getPath) ++ apps.filter(_.isDirectory).flatMap(d => getFilesForClass(caller, d.getPath))

      } catch {
        case ex: URISyntaxException =>
        // never happens
          Array.empty[String]
      } else Array.empty[String]
    }


  }

}
