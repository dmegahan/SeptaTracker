import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * Created by Danny on 8/13/2016.
 */
public class HomePanel extends Observable implements Runnable, ActionListener {
    JPanel homePanel;
    JPanel trackingPanel;
    JPanel buttonPanel;

    DefaultTableModel model;
    JTable trainsTable;

    Vector<Train> trainsInTable;

    DetailsPanel details;

    Thread observerThread;

    public HomePanel() {
        trainsInTable = new Vector<Train>();
        homePanel = new JPanel(new BorderLayout());

        trackingPanel = new JPanel();
        trackingPanel.setLayout(new BoxLayout(trackingPanel, BoxLayout.Y_AXIS));

        //setting up button panel
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,2));

        details = new DetailsPanel(null, homePanel);
        details.addObserver(new Observer() {
            public void update(Observable obj, Object arg){
                //we got something from the results panel, meaning we need to go back to the search panel
                if(arg instanceof JPanel){
                    //got a panel, send it to the view controller to display
                    run((JPanel)arg);
                }
            }
        });
        observerThread = new Thread(details);
        observerThread.start();

        InitializePanel();
    }

    public void InitializePanel(){
        JLabel trackingLabel = new JLabel("Tracked Trains (Scheduled Times)");

        String[] columnNames = {"From", "To", "Depart Time", "Arrival Time", "Delay?"};
        Object[][] data = {
                //{"Malvern", "30th Street", "12:00", "12:45", "N"},
                //{"Malvern", "30th Street", "12:00", "12:45", "N"},
                //{"Malvern", "30th Street", "12:00", "12:45", "N"},
                //{"Malvern", "30th Street", "12:00", "12:45", "N"},
                //{"Malvern", "30th Street", "12:00", "12:45", "N"},
                //{"Malvern", "30th Street", "12:00", "12:45", "N"},
                //{"Malvern", "30th Street", "12:00", "12:45", "N"},
                //{"Malvern", "30th Street", "12:00", "12:45", "N"},
        };


        model = new DefaultTableModel();
        for(int i = 0; i < columnNames.length; i++){
            model.addColumn(columnNames[i]);
        }
        trainsTable = new JTable(model);
        trainsTable.setRowHeight(30);
        //make it so the user cant edit the table
        trainsTable.setDefaultEditor(Object.class, null);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        for(int i = 0; i < trainsTable.getColumnCount(); i++){
            trainsTable.getColumn(columnNames[i]).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(trainsTable);
        trainsTable.setFillsViewportHeight(true);

        trackingPanel.add(trackingLabel);
        trackingPanel.add(scrollPane);

        JButton detailsButton = new JButton("Details");
        detailsButton.setActionCommand("details");
        detailsButton.addActionListener(this);
        JButton UnTrackButton = new JButton("Untrack");
        UnTrackButton.setActionCommand("untrack");
        UnTrackButton.addActionListener(this);
        JButton updateButton = new JButton("Refresh");
        updateButton.setActionCommand("refresh");
        updateButton.addActionListener(this);

        buttonPanel.add(detailsButton, BorderLayout.EAST);
        buttonPanel.add(UnTrackButton, BorderLayout.WEST);
        buttonPanel.add(updateButton, BorderLayout.CENTER);

        homePanel.add(trackingPanel, BorderLayout.CENTER);
        homePanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public JPanel getHomePanel(){
        return homePanel;
    }

    public void trackTrain(Train train, boolean verify){
        //if verify = true, that is because it came from a value not directing grabbed from septa
        //this would/will occur when the tracked trains are imported from the file
        //we need to verify because these trains could be finished, so we need to scrub some info and indicate that
        if(verify == true){
            updateTrainInfo(train);
        }
        model.addRow(new Object[] {train.getStartLoc(),
                train.getEndLoc(), train.getOrig_departure_time(), train.getOrig_arrival_time(),
                train.getOrig_delay()});
        trainsInTable.add(train);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if(cmd.equalsIgnoreCase("untrack")){
            //if untrack, remove row from table and remove train from the list
            int selected = trainsTable.getSelectedRow();
            if(selected != -1){
                model.removeRow(selected);
                trainsInTable.remove(selected);
            }else{
                return;
            }
        }else if(cmd.equalsIgnoreCase("details")){
            int selected = trainsTable.getSelectedRow();
            Train train;
            if(selected != -1){
                train = trainsInTable.get(selected);
            }else{
                return;
            }
            updateTrainInfo(train);
            details.setTrain(train);
            details.displayTrainInfo(train);
            run(details.getDetailsPanel());
        }else if(cmd.equalsIgnoreCase("refresh")){
            for(int i = 0; i < trainsInTable.size(); i++){
                updateTrainInfo(trainsInTable.get(i));
            }
        }
    }

    public void serializeTrackedTrains(){
        Gson gson = new Gson();
        String json = gson.toJson(trainsInTable);

        FileReadWriter writer = new FileReadWriter("./tracked.txt");
        if(trainsInTable.isEmpty()){
            writer.writeToFile("");
        }else{
            //clear it first just in case
            writer.writeToFile("");
            writer.writeToFile(json);
        }
    }

    public void updateTrainInfo(Train train){
        //update the info
        SeptaAPI sAPI = new SeptaAPI();

        String arrivals = sAPI.getNextToArrive(train.getStartLoc(), train.getEndLoc(), 50);

        SeptaJSON jsonObject = new SeptaJSON();
        Train[] trains = jsonObject.parseNextToArrives(train.getStartLoc(), train.getEndLoc(), arrivals);

        boolean foundTrain = false;
        for(int i = 0; i < trains.length; i++){
            //find our train and update our train objects info (delay, train num)
            if(trains[i].getOrig_arrival_time().equalsIgnoreCase(train.getOrig_arrival_time()) &&
                    trains[i].getOrig_departure_time().equalsIgnoreCase(train.getOrig_departure_time())){
                //times and places are the same? effectively the same train from a user perspective
                System.out.println("Updated info");
                train.setOrig_delay(trains[i].getOrig_delay());
                train.setOrig_train(trains[i].getOrig_train());
                foundTrain = true;
            }
        }

        if(foundTrain == false){
            //train no longer in service
            //lets update the info on the tracker
            System.out.println("Inactive train found");
            train.setOrig_train("NA");
            train.setOrig_delay("NA");
        }

        refreshTable();
    }

    public void refreshTable(){
        //clear all rows first
        if (model.getRowCount() > 0) {
            for (int i = model.getRowCount() - 1; i > -1; i--) {
                model.removeRow(i);
            }
        }
        for(int i = 0; i < trainsInTable.size(); i++){
            Train train = trainsInTable.get(i);
            model.addRow(new Object[] {train.getStartLoc(),
                    train.getEndLoc(), train.getOrig_departure_time(), train.getOrig_arrival_time(),
                    train.getOrig_delay()});
        }
    }

    @Override
    public void run() {
        setChanged();
        notifyObservers();
    }

    public void run(JPanel panel){
        setChanged();
        notifyObservers(panel);
    }
}
