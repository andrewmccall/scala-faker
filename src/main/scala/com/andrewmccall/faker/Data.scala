package com.andrewmccall.faker

trait Data {

  /**
    * Fetches an Entry for a key with the given locale.
    * @param key
    * @param locale the locale for the key to use, if the key is None or unavailable the default local will be used.
    * @return the value at this key or None
    */
  def fetch(key: String, locale: Option[String] = None, defaultLocale: String = Faker.defaultLocale) : Option[Entry]

  /**
    * Fetches any sub-keys for a key in this data.
    * @param key
    * @return
    */
  def getKeys() : Iterable[String]

  /**
    * Checks if this key can be resolved by this Data. The locale
    * @param key the key to resolve,
    * @return true if the key can be resolved via
    */
  def contains(key: String, locale: Option[String] = None, defaultLocale: String = Faker.defaultLocale) : Boolean = false

}
