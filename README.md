
#Septa Tracker  

This was a project for CS338 - Graphical User Interfaces. 

Septa tracker uses the Septa API to track and display Septa Regional Rail trains in real time. Septa stores their data as JSON and I use gson to unwrap this data. The data is queried from Septa through REST calls. I also use [twitter4j](http://twitter4j.org/en/) to grab tweets from Septa's twitter and display them. The project uses an evaluation copy of [jxmaps](https://www.teamdev.com/jxmaps) to display trains on a Google map in realtime, using the longitude and latitude coordinates that Septa gives us.

Trains can be searched and then tracked, and those tracked trains are presistently tracked, even if the program is exited and restarted. 

Some screenshots:

The home screen, it shows which trains you are currently tracked and shows the up-to-date information on their status. Trains with a delay of NA are not active and are not currently running. 

![tracked](https://cloud.githubusercontent.com/assets/3003191/20538169/04775b5e-b0be-11e6-8aaa-ff4620ea9135.png)

The details screen. This shows more detailed information about the trains, like latest stop and arrival information for each consecutive stop on its schedule. 

![details](https://cloud.githubusercontent.com/assets/3003191/20538142/e6bb5156-b0bd-11e6-943f-52f09a3419f4.png)

The search screen, where you search trains.

![search](https://cloud.githubusercontent.com/assets/3003191/20538180/1480067c-b0be-11e6-8b48-4a554c2ee0e8.png)

And the results, which shows all trains matching your input beginning location and ending location. The details screen can be accessed from here as well. 

![results](https://cloud.githubusercontent.com/assets/3003191/20538175/0bfbab82-b0be-11e6-9762-c17fbb1fcf25.png)

A tweets screen, which takes tweets from Septas Twitter and displays them.
![tweets](https://cloud.githubusercontent.com/assets/3003191/20538187/1a5aea6c-b0be-11e6-9160-cabe0c35762e.png)
