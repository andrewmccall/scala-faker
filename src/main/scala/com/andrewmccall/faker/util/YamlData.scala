package com.andrewmccall.faker.util

import java.io.{File, FileReader}

import com.andrewmccall.faker.Data
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.scala.Logging
import org.yaml.snakeyaml.Yaml

import scala.annotation.tailrec
import scala.collection.JavaConverters._

class YamlData(data: Map[String, Any]) extends Data with Logging {

  def this() = {
    this(YamlData.load())
  }

  override def fetch(key: String): Either[String, Seq[String]] = {
    logger.debug(s"Fetching key $key")
    fetchIn("\\.".r.split(key), data)
  }

  override def contains(key: String) : Boolean = {
    contains("\\.".r.split(key))
  }

  @tailrec
  private[faker] final def contains(key: Seq[String], data: Map[String, Any] = this.data): Boolean = {
    if (!data.contains(key.head)) false
    else if (data.contains(key.head) && key.size == 1) true
    else {
      val value: Any = data(key.head)
      val subMap: Map[String, Any] = value match {
        case j: java.util.Map[String, _] => j.asScala.toMap
        case _ => value.asInstanceOf[Map[String, Any]]
      }
      contains(key.drop(1), subMap)
    }
  }

  @tailrec
  private[util] final def fetchIn(key: Array[String], data: Map[String, Any]): Either[String, Seq[String]] = {

    logger.trace(s"Fetching in: $key")
    key.foreach(logger.trace(_))

    val value: Any = data(key.head)
    if (key.length == 1)
      value match {
        case l: java.util.List[String] => Right(l.asScala)
        case _ => Left(value.asInstanceOf[String])
      }
    else {
      val values = value match {
        case m: java.util.Map[String, _] => m.asScala.toMap
        case _ => value.asInstanceOf[Map[String, Any]]
      }
      logger.trace(s"Has keys: ${values.keys}")
      fetchIn(key.drop(1), values)
    }
  }

}

object YamlData {

  private val logger = LogManager.getLogger(getClass)

  def load(): Map[String, Any] = {
    def yaml = new Yaml()

    val files = getFilesForClass(this.getClass, "com/andrewmccall/faker/locales")

    logger.info(s"Loading keys from ${files.length} files")
    if (logger.isDebugEnabled()) files.foreach(logger.debug)

    val data = files.map(x => {
      yaml.load(new FileReader(x)).asInstanceOf[java.util.Map[String, Any]].asScala.toMap
    })

    logger.debug(s"Data contains ${data.length} Maps.")

    data.reduce(merge)
  }

  private[util] def merge(one: Map[String, Any], two: Map[String, Any]): Map[String, Any] = {
    val keys = one.keySet ++ two.keySet
    keys.foldLeft(Map.empty[String, Any])((r: Map[String, Any], k: String) => mergeForKey(k, r, one, two))
  }

  private[util] def mergeForKey(k: String, r: Map[String, Any], one: Map[String, Any], two: Map[String, Any]): Map[String, Any] = {
    // if the key isn't in one, just use two.

    if (!one.contains(k)) r + (k -> two(k))
    else if (!two.contains(k)) r + (k -> one(k))
    else {
      one(k) match {
        case m: Map[String, _] => two(k) match {
          case m: Map[String, _] => r + (k -> merge(one(k).asInstanceOf[Map[String, Any]], two(k).asInstanceOf[Map[String, Any]]))
          case u: java.util.Map[String, _] =>
            r + (k -> merge(one(k).asInstanceOf[Map[String, Any]], two(k).asInstanceOf[java.util.Map[String, Any]].asScala.toMap))
          case _ => r + (k -> one(k))
        }
        case m: java.util.Map[_, _] => two(k) match {
          case m: Map[String, _] => r + (k -> merge(one(k).asInstanceOf[java.util.Map[String, Any]].asScala.toMap, two(k).asInstanceOf[Map[String, Any]]))
          case u: java.util.Map[String, _] =>
            r + (k -> merge(one(k).asInstanceOf[java.util.Map[String, Any]].asScala.toMap, two(k).asInstanceOf[java.util.Map[String, Any]].asScala.toMap))

        }
        case _ => two(k) match {
          case m: Map[String, _] => r + (k -> two(k))
          case _ => r + (k -> one(k))
        }
      }
    }
  }

  private[util] def getFilesForClass(caller: Class[_], path: String): Array[String] = {

    import java.net.URISyntaxException
    import java.util.jar.JarFile

    val jarFile = new File(caller.getProtectionDomain.getCodeSource.getLocation.getPath)

    if (jarFile.isFile) { // Run with JAR file
      val jar = new JarFile(jarFile)
      val entries = jar.entries //gives ALL entries in jar
      import scala.collection.JavaConverters._
      val newFiles = entries.asScala.filter(_.getName.startsWith(path + "/")).filter(_.getName.endsWith(".yml")).map(_.getName)
      jar.close()
      newFiles.toArray
    }
    else { // Run with IDE
      val url = classOf[YamlData].getResource("/" + path)
      if (url != null) try {
        val apps = new File(url.toURI).listFiles()
        apps.filter(f => f.isFile && (f.getName.endsWith(".yml") || f.getName.endsWith("yaml"))).map(_.getPath) ++ apps.filter(_.isDirectory).flatMap(d => getFilesForClass(caller, path + "/" + d.getName))

      }
      catch {
        case ex: URISyntaxException =>
          // never happens
          Array.empty[String]
      } else Array.empty[String]
    }

  }

}
