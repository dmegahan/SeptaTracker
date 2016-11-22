/**
 * Created by Danny on 8/11/2016.
 */

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("http://www3.septa.org/hackathon")
public class SeptaAPI {
    String SeptaEndpoint;
    Client client;

    public SeptaAPI(){
        SeptaEndpoint = "http://www3.septa.org/hackathon";
        client = ClientBuilder.newClient();
    }

    public String getArrivals(String station, int numResults){
        return getResponseFromEndpoint(SeptaEndpoint + "/Arrivals/Paoli/5");
    }

    public String getAlerts(){
        return getResponseFromEndpoint(SeptaEndpoint + "/Alerts/?callback=?");
    }

    //septa TrainView gives specific information for trains currently running, including
    //next stop, train number, latitude and longitude, and basic trip info
    public String getTrainView(){
        return getResponseFromEndpoint(SeptaEndpoint + "/TrainView/");
    }

    //next to arrive takes in a start and end station, and returns the trains that will pass
    //through both those stations
    public String getNextToArrive(String start, String end, int numResults){
        return getResponseFromEndpoint(SeptaEndpoint + "/NextToArrive/" + start + "/" +
                                                                          end + "/" + numResults);
    }

    public String getTrainInfo(String trainNum){
       String response = getResponseFromEndpoint(SeptaEndpoint + "/RRSchedules/" + trainNum);
        if(response.contains("error")){
            return null;
        }
       return response;
    }

    private String getResponseFromEndpoint(String endpoint){
        WebTarget target = client.target(endpoint);

        Invocation.Builder invocationBuilder = target.request(MediaType.TEXT_PLAIN_TYPE);

        Response response = invocationBuilder.get();
        String theResponse = response.readEntity(String.class);

        return theResponse;
    }
}
