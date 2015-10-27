# Raycasting

## Building and Running

This project uses [Maven](https://maven.apache.org/) to manage dependencies and the build process. 

To build an executable JAR, run ```mvn package``` in the project directory. The resulting JAR will be located at 
```target/Raycasting-1.0-SNAPSHOT-jar-with-dependencies.jar``` which can be run via 
```java -jar target/Raycasting-1.0-SNAPSHOT-jar-with-dependencies.jar``` or by double clicking on it (if your JRE is 
properly configured to do so).

To run the project directly from Maven, run ```mvn exec:java```.

Most IDEs have built-in or plugin support for Maven projects, please consult your IDE documentation on how to use it.

## Libaries Used

- [Lightweight Java Game Library 3.0 (LWJGL)](http://www.lwjgl.org/)
- [Log4j2](http://logging.apache.org/log4j/2.x/)
- [LWJGL3 edu.purdue.ieee.csociety.raycasting.util.SharedLibraryLoader.java](https://github.com/badlogic/lwjgl3-maven-gradle)

Check [pom.xml](pom.xml) for details.
