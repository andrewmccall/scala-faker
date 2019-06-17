package com.andrewmccall.faker.module

import com.andrewmccall.faker.{Config, Data, Entry, Faker, Namespace, StringEntry}
import org.apache.logging.log4j.scala.Logging
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.{ClasspathHelper, ConfigurationBuilder}

import scala.reflect.runtime.universe._

class ScalaModule(clazz: Class[_], faker: Faker, config: Config) extends Logging {

  private val rm = scala.reflect.runtime.currentMirror
  private val t = rm.classSymbol(this.clazz).toType
  private val a = typeOf[faker]
  private val instance = clazz.getConstructor(classOf[Faker], classOf[Config]).newInstance(faker, config)

  def getName: String = {
    this.clazz.getSimpleName.toLowerCase
  }

  def getKeys: Iterable[String] = {
    t.members.filter(_.isMethod).filter(_.annotations.map(_.tree.tpe).contains(a)).map(snakify)
  }

  def contains(method: String): Boolean = {
    val ms = t.member(TermName(method))
    ms.annotations.foreach(i => logger.info(s"Annotations: $i ${i.getClass}"))
    ms != NoSymbol && ms.annotations.map(_.tree.tpe).contains(a)
  }

  def apply(method: String, args: Map[String, Any]): Any = {
    val ms = t.member(TermName(method)).asMethod
    ms.annotations.foreach(i => logger.info(s"Annotations: $i type- ${i.tpe} ${i.getClass}"))

    if (ms != NoSymbol && ms.annotations.map(_.tree.tpe).contains(a)) {

      // construct the args.
      val fullArgs = getDefaultArguments(ms) ++ args
      val argList = ms.paramLists.head.map(_.asTerm).zipWithIndex.map({case (p, i) => fullArgs.get(p.name.toString)})
      rm.reflect(instance).reflectMethod(ms).apply(argList)
    }
  }

  def getDefaultArguments(method: String): Map[String, Any] = {
    val ms = t.member(TermName(method)).asMethod
    getDefaultArguments(ms)
  }

  def getDefaultArguments(ms: MethodSymbol): Map[String, Any] = {

    logger.trace(s"Getting default arguments for Method ${ms.name}")

    val args = ms.paramLists.head.map(_.asTerm).filter(_.isParamWithDefault).zipWithIndex.map({ case (p, i) => {

      logger.trace(s"Finding paramenter ${p} for index ${i}")
      val term = TermName(s"${ms.name.toString}$$default$$${i + 1}")
      logger.trece(s"Method name will be: ${term.toString}")
      val method = t.member(term).asMethod
      logger.trace(s"Got method ${method}")
      (p.name.toString, rm.reflect(instance).reflectMethod(method).apply())
    }
    }).toMap

    logger.debug(s"${ms.name} has arguments ${args}")
    args
  }

  private def snakify(symbol: Symbol): String = {
    ScalaModule.snakify(symbol.fullName)
  }

}

object ScalaModule extends Logging {

  def loadModules(faker: Faker, config: Config): Data = {

    logger.info("Loading Modules.")

    import org.reflections.Reflections
    val reflections = new Reflections(
      new ConfigurationBuilder()
        .setUrls(ClasspathHelper.forPackage("com.andrewmccall.faker.module"))
        .setScanners(new MethodAnnotationsScanner())
    )

    val modules = reflections.getMethodsAnnotatedWith(classOf[com.andrewmccall.faker.module.faker])

    import scala.collection.JavaConverters._

    for (elem <- modules.asScala.toSeq) {
      logger.trace(s"Found $elem ${elem.getDeclaringClass}")
    }


    new ModuleData(modules.asScala.map(_.getDeclaringClass).toSeq.distinct.map(c => {
        logger.debug(s"Creating new ${c.getName} as ${c.getSimpleName}")
        val mod = new ScalaModule(c, faker, config)
        mod.getName -> mod
    }).toMap)
  }

  /**
    * Turn a string of format "FooBar" or "fooBar" into snake case "foo_bar"
    *
    * @return the snake cased string
    */
  private[module] def snakify(name: String) = name.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2").replaceAll("([a-z\\d])([A-Z])", "$1_$2").toLowerCase

  private class ModuleData(modules: Map[String, ScalaModule]) extends Data {


    private def parseKey(key: String): (String, String) = {
      val parts = "\\.".r.split(key).takeRight(2)
      if (parts.length == 2) (parts(0).toLowerCase, parts(1).toLowerCase)
      else ("","")
    }

    override def fetch(key: String, locale: Option[String] = None, defaultLocale: String = Faker.defaultLocale): Option[Entry] = {
      val parsedKey = parseKey(key)
      Some(StringEntry(modules(parsedKey._1).apply(parsedKey._2, Map.empty).toString))
    }
    override def contains(key: String, locale: Option[String] = None, defaultLocale: String = Faker.defaultLocale): Boolean = {
      val parsedKey = parseKey(key)
      modules.contains(parsedKey._1) && modules(parsedKey._1).contains(parsedKey._2)
    }

    /**
      * Fetches any sub-keys for a key in this data.
      *
      * @param key
      * @return
      */
    override def getKeys(): Iterable[String] = modules.keys
  }



}
