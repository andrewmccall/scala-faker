# Scala Faker
An implementation fo the Ruby faker library in scala. The intial implementation is a direct port of the ruby code to 
scala, updates have tried to make it more scala like. 

This code uses the YAML files from the ruby faker project and implements all the methods supported there.

#Usage

Configuration follows the existing faker standard. 

    val faker = new Faker()
    val someString = faker("Name.name")

##Keys in strings
{} is the standard notation for embedding a key. 

    val key = "Hi! My name is ${Name.name}"
   

## Using a different locale. 
Individual calls to faker() can provided a locale for individual keys. 

    val key = "Hi! My name is ${Name.name:de}"
  


##Configuration

### Locales
    val config = new Config(locale="en-GB")

### Random implementation
The default random implementation uses a ThreadlocalRandom.current() to generate random numbers. This can be overriden 
if for example you need to provide a seed for reproducibility by setting another subclass of java.util.Random in the 
config.

    val myRandom = new Random(1)
    val faker = new Faker(random=myRandom) 

### Custom data sources
By default 

    val config = new Config(locale="en-GB")
    Faker.setLocale()
    Faker.setRandom()
    Faker.setData()
    
