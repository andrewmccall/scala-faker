# Scala Faker

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.andrewmccall.faker/scala-faker_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.andrewmccall.fakerl/scala-faker_2.12)[ ![Download](https://api.bintray.com/packages/andrewmccall/maven/scala-faker/images/download.svg) ](https://bintray.com/andrewmccall/maven/scala-faker/_latestVersion) [![BuildStatus](https://travis-ci.com/andrewmccall/scala-faker.svg?branch=master)](https://travis-ci.com/andrewmccall/scala-faker.svg?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/a2a7e12bd5aa48109ded58c3675fdf42)](https://www.codacy.com/app/andrewmccall/scala-faker?utm_source=github.com&utm_medium=referral&utm_content=andrewmccall/scala-faker&utm_campaign=Badge_Coverage) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/a2a7e12bd5aa48109ded58c3675fdf42)](https://www.codacy.com/app/andrewmccall/scala-faker?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=andrewmccall/scala-faker&amp;utm_campaign=Badge_Grade)

An implementation fo the [Ruby faker library](https://github.com/stympy/faker) in scala. The intial implementation is a direct port of the ruby code to 
scala, updates have tried to make it more scala like. 

This code uses modified YAML files from the ruby faker project and implements all the methods supported there.

## Usage

Using the faker is as simple as creating a new faker and passing in keys. 

    val faker = new Faker()
    val someString = faker("name.name")

### Keys in strings
{} is the standard notation for embedding a key. 

    val string = "Hi! My name is #{Name.name}"
    val result = faker(string)

### Configuration

#### Locales
A faker can be configured for a different locale, allowing different languages and formats of keys to be returned. eg. UK Addresses.

    val config = new Config(locale="en-GB")
    val faker = new Faker(config)
    
Keys that are not found in the selected locale are automatically looked up in the `en` locale.

#### Random implementation
The default random implementation uses a ThreadlocalRandom.current() to generate random numbers. This can be overriden 
if for example you need to provide a seed for reproducibility by setting another subclass of java.util.Random in the 
config.

    val myRandom = new Random(1)
    val config = new Config(random=myRandom)
    val faker = new Faker(config) 
    
    
## Adding additional keys

_This section is a work in progress_
