# Scala Faker
An implementation fo the [Ruby faker library](https://github.com/stympy/faker) in scala. The intial implementation is a direct port of the ruby code to 
scala, updates have tried to make it more scala like. 

This code uses the YAML files from the ruby faker project and implements all the methods supported there.

# Usage

Configuration follows the existing faker standard. 

    val faker = new Faker()
    val someString = faker("name.name")

## Keys in strings
{} is the standard notation for embedding a key. 

    val string = "Hi! My name is #{Name.name}"
    val result = faker(string)
   

## Configuration

### Locales
A faker can be configured for a different locale, allowing different languages and formats of keys to be returned. eg. UK Addresses.

    val config = new Config(locale="en-GB")
    val faker = new Faker(config)
    
Keys that are not found in the selected locale are automatically looked up in the `en` locale.

### Random implementation
The default random implementation uses a ThreadlocalRandom.current() to generate random numbers. This can be overriden 
if for example you need to provide a seed for reproducibility by setting another subclass of java.util.Random in the 
config.

    val myRandom = new Random(1)
    val config = new Config(random=myRandom)
    val faker = new Faker(config) 
