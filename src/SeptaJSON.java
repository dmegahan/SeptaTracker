/**
 * Created by Danny on 8/13/2016.
 */

import com.google.gson.*;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.List;

//object that parses septa json retrieved from the SeptaAPI
public class SeptaJSON {
    Gson gson;
    public SeptaJSON(){
        gson = new Gson();
    }

    //used for getting our train information when searching using a departure location and arrival location
    public Train[] parseNextToArrives(String startLoc, String endLoc, String nextToArriveJson){
        Train[] trainData;
        try {
            trainData = gson.fromJson(nextToArriveJson, Train[].class);
        }catch(com.google.gson.JsonSyntaxException e){
            e.printStackTrace();
            return null;
        }catch(java.lang.IllegalStateException e){
            return null;
        }

        //objects get serialized, but startLoc and endLoc arent a part of that, need to add manually
        for(int i = 0; i < trainData.length; i++){
            if(!startLoc.equals("") && !endLoc.equals("")){
                trainData[i].setDepartLocation(startLoc);
                trainData[i].setArrivalLocation(endLoc);
            }
        }

        return trainData;
    }

    public Stop[] parseRRSchedules(String RRScheduleJson){
        Stop[] stopData;
        try{
            stopData = gson.fromJson(RRScheduleJson, Stop[].class);
        }catch(JsonSyntaxException e){
            e.printStackTrace();
            return null;
        }catch(IllegalStateException e){
            return null;
        }

        return stopData;
    }

    public TrainViewInfo[] parseTrainView(String TrainViewJson){
        TrainViewInfo[] trainViewInfo;
        trainViewInfo = gson.fromJson(TrainViewJson, TrainViewInfo[].class);

        return trainViewInfo;
    }
}
