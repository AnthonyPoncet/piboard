#PiBoard

##Purpose
Dashboard for raspberry Pi but could be use everywhere.

It contain a back end wrote in Kotlin using ktor and a 
front end in ReactJS.

##Build
Project build with gradle 5.2.1. Never test with prior version.

At root folder, run
```
gradlew jar
```
It will generate a jar file under <root>/back_end/build/libs.

This one will contain both client and server.

##Back end
Back end contain a server wrote in Kotlin using ktor. It could 
be start using 
```
java -jar back_end-1.0-SNAPSHOT.jar <-p port> <-c calendarIdsFile>
```
By default, the port is 8080 and the file is *calendar_ids.csv*
located just near the jar file. The purpose of the csv file
will be explain later.

All data in the server are updated each 5 minutes.

###end points
#####/meteo
######GET
Will return the current meteo for Paris.
#####/calendar
######GET
Will return the events of all calendar configured.
######POST
Will add some calendarIds as interest. Will return for each
one if successfully added or not. In case of success, it will 
append there ids to the provided csv file.

###Google calendar loader specification
While starting the server for the first time, it will ask to 
provide a connection to an google account using a browser.

Currently, only one account is allowed in the app, so please
use an account having access to all wanted agenda.

A CSV could be passed as parameter of the application. This one
should contain all calendar (ids) you are interested on. Please
note that the server will not start if one of this calendar is 
not accessible (log in standard console).

##Front end
Page could be access at root http address of the server.