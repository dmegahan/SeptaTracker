/**
 * Created by Danny on 8/15/2016.
 */
//train class that holds attributes for trains (arival/departure time, location, train num, etc)
public class Train {
    private String orig_train;
    private String orig_line;
    private String orig_departure_time;
    private String orig_arrival_time;

    private String orig_delay;
    private String departLocation;
    private String arrivalLocation;

    public Train(String orig_train, String orig_line, String orig_departure_time, String orig_arrival_time,
                 String orig_delay, String startLoc, String endLoc){
        //constructer for the json object (nextToArrive), add location and arrival later as they arent
        // passed in the json object

        this.orig_train = orig_train;
        this.orig_line = orig_line;
        this.orig_departure_time = orig_departure_time;
        this.orig_arrival_time = orig_arrival_time;
        this.orig_delay = orig_delay;

        this.departLocation = startLoc;
        this.arrivalLocation = endLoc;
    }

    public void setDepartLocation(String departLocation){
        this.departLocation = departLocation;
    }

    public void setArrivalLocation(String arrivalLocation){
        this.arrivalLocation = arrivalLocation;
    }

    public String ToString(){
        return "Train: " + orig_train + ", line: " + orig_line + ", depart Loc: " + departLocation + ", destination: "
                + arrivalLocation + ", depart @: " + orig_departure_time + ", arrive @: " +
                orig_arrival_time + ", delay: " + orig_delay;
    }

    public String getOrig_train(){
        return orig_train;
    }

    public String getOrig_departure_time(){
        return orig_departure_time;
    }

    public String getOrig_arrival_time(){
        return orig_arrival_time;
    }

    public String getOrig_delay(){
        return orig_delay;
    }

    public String getStartLoc(){ return departLocation; }

    public String getEndLoc() { return arrivalLocation; }

    public String getOrig_line(){ return orig_line; }

    public void setOrig_train(String orig_train){ this.orig_train = orig_train; }
    public void setOrig_delay(String orig_delay){ this.orig_delay = orig_delay; }
}
