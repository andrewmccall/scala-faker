# Scala Faker
An implementation fo the Ruby faker library in scala. The intial implementation is a direct port of the ruby code to 
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
    
## Modules
Modules define the namespaces for the differetn fakers and are a combination of YAML 
configuration files and functions. 
    
## Adding YAML files. 

    faker.load(yaml)

    en:
        faker:
            <module>:
                key: value
    

## Custom functions

Custom functions should be annotated with the @faker annotation and provide a module. Classes are registered any any 
functions annotated with @faker will be automatically registered.

    class MyModule {
    
        @faker("module-name")
        def something() : String = {
            "#{name.name}"
        }
    }

    faker.register(class) 