import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.teamdev.jxmaps.*;
import com.teamdev.jxmaps.swing.MapView;

/**
 * Created by Danny on 8/29/2016.
 */
public class MapPanel extends MapView {
    InfoWindow window;
    Train train = null;
    ScheduledExecutorService executor;
    Map map;
    Marker marker;
    public MapPanel(MapViewOptions options, final Train train) {
        this.train = train;
        setOnMapReadyHandler(new MapReadyHandler() {
            @Override
            public void onMapReady(MapStatus status) {
                if (status == MapStatus.MAP_STATUS_OK) {
                    map = getMap();
                    map.setZoom(10);
                    GeocoderRequest request = new GeocoderRequest(map);
                    //request.set
                    LatLng trainCoords = getTrainLongAndLat(train);
                    if(trainCoords == null){
                        return;
                    }
                    request.setLocation(trainCoords);
                    map.setCenter(trainCoords);

                    getServices().getGeocoder().geocode(request, new GeocoderCallback(map) {
                        @Override
                        public void onComplete(GeocoderResult[] result, GeocoderStatus status) {
                            if (status == GeocoderStatus.OK) {
                                map.setCenter(result[0].getGeometry().getLocation());
                                marker = new Marker(map);
                                marker.setPosition(result[0].getGeometry().getLocation());

                                window = new InfoWindow(map);
                                window.setContent("Your Train");
                                window.open(map, marker);
                            }
                        }
                    });
                }
                map.addEventListener("click", new MapMouseEvent() {
                    @Override
                    public void onEvent(MouseEvent mouseEvent) {
                        LatLng trainCoords = getTrainLongAndLat(train);
                        if(trainCoords == null){
                            return;
                        }
                        if (!marker.getPosition().equals(trainCoords)){
                            marker.setPosition(trainCoords);
                            window = new InfoWindow(map);
                            window.setContent("Your Train");
                            window.open(map, marker);
                            System.out.println("Updated?");
                        }
                    }
                });
            }
        });
        /*
        //update position every few minutes
        Runnable updatePos = new Runnable(){
            public void run(){
                updatePosition();
            }
        };

        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(updatePos, 0, 2, TimeUnit.SECONDS);
        */
    }

    public LatLng getTrainLongAndLat(Train train){
        //Using the train number, and getting information from septa (trainView) we can obtain the
        //current longitude and latitude of the train we're tracking/looking at

        SeptaAPI septaApi = new SeptaAPI();
        String trainViewJson = septaApi.getTrainView();
        SeptaJSON septaJson = new SeptaJSON();
        TrainViewInfo[] trainViews = septaJson.parseTrainView(trainViewJson);

        String lat = "";
        String lon = "";
        TrainViewInfo ourTrainView = null;

        for(int i = 0; i < trainViews.length; i++){
            if(trainViews[i].trainno.equalsIgnoreCase(train.getOrig_train())){
                //got the right train
                ourTrainView = trainViews[i];
                break;
            }
        }
        if(ourTrainView != null){
            return new LatLng(Double.parseDouble(ourTrainView.lat), Double.parseDouble(ourTrainView.lon));
        }

        return null;
    }
}
