Enkelt projekt för att visa hur man skapar en modul som drar igång flera verticles med en konfigurationsfil.

För att öppna behöver du:
- Maven
- Java 8

För att köra behöver du:
- Vert.x

Bygg med maven och kopiera /src/main/resources/conf.json till target mappen och kör följande kommando i target mappen:

vertx runmod se.consid.reactive~Starter~0.1 -conf conf.json
