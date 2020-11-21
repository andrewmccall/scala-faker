package com.andrewmccall.faker.generator

/**
  * A SeqGenerator contains a Seq of Generators and returns a sampled Generator from the sequence using a call to
  * faker.sample() to obtain the random Generator.
  *
  * @param values the Seq of Generators to use
  */
case class SeqGeneratorBuilder(values: Seq[GeneratorBuilder]) extends GeneratorBuilder {

  val generators = {
    values.map(g => g.generate())
  }

  override def generate(): Generator = {
    (faker, locale) => {
      faker.sample(generators)(faker, locale)
    }
  }
}
