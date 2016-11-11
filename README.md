## Project description
One more example plugin for [marvin](https://github.com/beolnix/marvin/) bot.
Plugin pushes statistics of the chat to the [marvin-statistics](https://github.com/beolnix/marvin-statistics) RESTful web-service.

### Supported commands
!stat - shows link to the statistics of a current chat

## Requirements
#### To run
* JDK 8 only

#### To build
* JDK 8
* Gradle 2.8
* Groovy 2.4.4

## Build from source
Just execute the following command and may the force be with you:
```
gradle clean build
```

If everything is fine, you find **marvin-statistics-plugin-0.1.jar** in **build/libs/** 

## Usage 
To deploy the plugin simply copy it to the plugins directory of [marvin](https://github.com/beolnix/marvin/) bot.
No restart is required, marvin will pick it up on the fly and tell you about it in his **logs/application-main.log**.
Once it is deployed simply send a message directly to the bot or to the conference with it.

## Troubleshooting
* :collision: Be careful with content in **MANIFEST.MF**. Most likely plugin can't be deployed because of format violations in it. In the gradle script of this example special magic happens with version before it is saved in **MANIFEST.MF**. It is better to do just the same in your plugin.
* :collision: Another possible reason is in classpath specified in **MANIFEST.MF**. It is better to keep it constructed automatically as it is implemented in gradle script of this plugin.