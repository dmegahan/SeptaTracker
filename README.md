
#Septa Tracker  

This was a project for CS338 - Graphical User Interfaces. 

Septa tracker uses the Septa API to track and display Septa Regional Rail trains in real time. Septa stores their data as JSON and I use gson to unwrap this data. The data is queried from Septa through REST calls. I also use [twitter4j](http://twitter4j.org/en/) to grab tweets from Septa's twitter and display them. The project uses an evaluation copy of [jxmaps](https://www.teamdev.com/jxmaps) to display trains on a Google map in realtime, using the longitude and latitude coordinates that Septa gives us.

Trains can be searched and then tracked, and those tracked trains are presistently tracked, even if the program is exited and restarted. 

Some screenshots: 
