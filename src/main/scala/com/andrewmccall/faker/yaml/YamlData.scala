package com.andrewmccall.faker.yaml

import java.io.{File, FileReader}

import com.andrewmccall.faker.{Data, Entry, Faker, SeqEntry, StringEntry}
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.scala.Logging
import org.yaml.snakeyaml.Yaml

import scala.annotation.tailrec
import scala.collection.JavaConverters._

class YamlData(data: Map[String, Any]) extends Data with Logging {

  def this() = {
    this(YamlData.load())
  }

  override def fetch(key: String, locale: Option[String] = None, defaultLocale: String = Faker.defaultLocale): Option[Entry] = {
    logger.debug(s"Fetching key $key")
    Some(fetchIn("\\.".r.split(key), data))
  }

  override def contains(key: String, locale: Option[String] = None, defaultLocale: String = Faker.defaultLocale) : Boolean = {
    containKeys("\\.".r.split(key))
  }

  @tailrec
  private[faker] final def containKeys(key: Seq[String], data: Map[String, Any] = this.data): Boolean = {
    if (!data.contains(key.head)) false
    else if (data.contains(key.head) && key.size == 1) true
    else {
      val value: Any = data(key.head)
      val subMap: Map[String, Any] = value match {
        case j: java.util.Map[String, _] => j.asScala.toMap
        case m: Map[String, Any] => m
        case _ => return false
      }
      containKeys(key.drop(1), subMap)
    }
  }

  @tailrec
  private[yaml] final def fetchIn(key: Array[String], data: Map[String, Any]): Entry = {
    val value: Any = data(key.head)
    if (key.length == 1)
      value match {
        case l: java.util.List[String] => SeqEntry(l.asScala)
        case _ => StringEntry(value.asInstanceOf[String])
      }
    else {
      val values = value match {
        case m: java.util.Map[String, _] => m.asScala.toMap
        case _ => value.asInstanceOf[Map[String, Any]]
      }
      fetchIn(key.drop(1), values)
    }
  }

  /**
    * Fetches any sub-keys for a key in this data.
    *
    * @param key
    * @return
    */
  override def getKeys(): Iterable[String] = data.keys
}

object YamlData {

  private val logger = LogManager.getLogger(getClass)

  def load(): Map[String, Any] = {
    def yaml = new Yaml()

    val files = getFilesForClass(this.getClass, "com/andrewmccall/faker/locales")

    logger.debug(s"Loading keys from ${files.length} files")
    if (logger.isDebugEnabled()) files.foreach(logger.debug)

    val data = files.map(x => {
      yaml.load(new FileReader(x)).asInstanceOf[java.util.Map[String, Any]].asScala.toMap
    })

    data.reduce(merge)
  }

  private[yaml] def merge(one: Map[String, Any], two: Map[String, Any]): Map[String, Any] = {
    val keys = one.keySet ++ two.keySet
    keys.foldLeft(Map.empty[String, Any])((r: Map[String, Any], k: String) => mergeForKey(k, r, one, two))
  }

  private[yaml] def mergeForKey(k: String, r: Map[String, Any], one: Map[String, Any], two: Map[String, Any]): Map[String, Any] = {
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

  private[yaml] def getFilesForClass(caller: Class[_], path: String): Array[String] = {

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
