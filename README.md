# search_engine
A java search engine that supports crawling, indexing, partial search and exact search.
![Animated Demo](search.gif)


 * =============================================================================
 * Project:     =        SearchEngine
 * Production:  =        http://youtalky.com
 * Source:      =        https://github.com/usf-cs212-2016/project-tcsiwula
 * Created:      =        11/6/16
 * Author:       =        Tim Siwula <tcsiwula@gmail.com>
 * University:   =        University of San Francisco
 * Class:        =        CS 212: Software Development
 * License:      =        GPLv2
 * Version:      =        0.001
 * ==============================================================================

## Run locally



```

mvn clean package

java -jar target/SearchEngine-0.0.1-SNAPSHOT.jar



# maven clean rebuild

rm -rf target

mvn clean compile -U
mvn -U clean install

mvn -U clean package
java -jar target/SearchEngine-0.0.1-SNAPSHOT.jar


```


```

javac src/*.java
java src/ViewWebServer

```


Open browser to:
```
http://localhost:8080/
```





