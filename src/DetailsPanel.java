import com.teamdev.jxmaps.MapViewOptions;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Vector;

/**
 * Created by Danny on 8/13/2016.
 */
public class DetailsPanel extends Observable implements Runnable, ActionListener {
    JPanel detailsPanel;
    JPanel infoPanel;
    JPanel lastStopPanel;
    JPanel buttonsPanel;
    JPanel stopsPanel;

    JLabel trainNumLabel;
    JLabel lineLabel;
    JLabel lastStoppedAt;

    DefaultTableModel model;
    JTable stopsTable;
    Vector<Stop> stopsInTable;

    SeptaAPI septaAPI;
    SeptaJSON septaJson;

    Train train;
    JPanel lastPanel;

    MapPanel mapPanel;

    public DetailsPanel(Train train, JPanel lastPanel){
        //initialize our panels
        stopsInTable = new Vector<Stop>();

        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        stopsPanel = new JPanel();
        stopsPanel.setLayout(new BoxLayout(stopsPanel, BoxLayout.Y_AXIS));

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1,2));

        infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout());

        lastStopPanel = new JPanel();
        lastStopPanel.setLayout(new FlowLayout());

        this.train = train;
        this.lastPanel = lastPanel;

        InitializePanel();
    }

    public void InitializePanel(){

        trainNumLabel = new JLabel("1111");
        lineLabel = new JLabel("Regional");
        lastStoppedAt = new JLabel("Latest Stop: Paoli @ 2:45");

        infoPanel.add(trainNumLabel);
        infoPanel.add(lineLabel);

        lastStopPanel.add(lastStoppedAt);

        JButton backButton = new JButton("Back");
        backButton.setActionCommand("back");
        backButton.addActionListener(this);
        JButton mapButton = new JButton("Display Map");
        mapButton.setActionCommand("map");
        mapButton.addActionListener(this);
        buttonsPanel.add(mapButton);
        buttonsPanel.add(backButton);

        JLabel remainingStopLabel = new JLabel("Remaining Stops");

        String[] columnNames = {"Stop", "Scheduled", "Estimated"};

        model = new DefaultTableModel();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        //populate the table with columns and column names
        for(int i = 0; i < columnNames.length; i++){
            model.addColumn(columnNames[i]);
        }

        stopsTable = new JTable(model);
        stopsTable.setRowHeight(30);
        //make it so the user cant edit the table
        stopsTable.setDefaultEditor(Object.class, null);

        //set the cells to render with centered alignment
        for(int i = 0; i < stopsTable.getColumnCount(); i++){
            stopsTable.getColumn(columnNames[i]).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(stopsTable);
        stopsTable.setFillsViewportHeight(true);

        stopsPanel.add(remainingStopLabel);
        stopsPanel.add(scrollPane, BorderLayout.CENTER);

        septaAPI = new SeptaAPI();
        septaJson = new SeptaJSON();

        detailsPanel.add(infoPanel);
        detailsPanel.add(lastStopPanel);
        detailsPanel.add(stopsPanel);
        detailsPanel.add(buttonsPanel);

        if(train != null) {
            displayTrainInfo(train);
        }
    }

    public void displayStops(Stop[] stops){
        //will update the summary label and populate the trains list
        if (model.getRowCount() > 0) {
            for (int i = model.getRowCount() - 1; i > -1; i--) {
                model.removeRow(i);
            }
        }
        for(int i = 0; i < stops.length; i++){
            DefaultTableModel model = (DefaultTableModel) stopsTable.getModel();
            model.addRow(new Object[]{stops[i].station,
                        stops[i].sched_tm,
                        stops[i].est_tm});
            stopsInTable.add(stops[i]);
        }
    }

    public JPanel getDetailsPanel(){
        return detailsPanel;
    }

    public void setTrain(Train train){
        this.train = train;
    }

    public void displayTrainInfo(Train train){
        String trainID = train.getOrig_train();

        //
        String stopsJson = septaAPI.getTrainInfo(trainID);
        System.out.println("stopsjson: " + stopsJson);
        Stop[] stops;
        if(stopsJson != null){
            stops = septaJson.parseRRSchedules(stopsJson);

            trainNumLabel.setText("Train Num: " + trainID);
            lineLabel.setText("Line: " + train.getOrig_line());
            lastStoppedAt.setText(getLastStoppedAt(stops));
        }else{
            stops = new Stop[0];
            //if the stopsJson returns an error (null), the train isnt in service.
            trainNumLabel.setText("Train no longer in service.");
            lineLabel.setText("Line: " + train.getOrig_line());
            lastStoppedAt.setText("Will update when Septa schedules a new train.");
        }
        displayStops(stops);
    }

    public String getLastStoppedAt(Stop[] stops){
        Stop last = null;
        for(int i = 0; i < stops.length; i++){
            //get the last stop, meaning we need to look for a "na" in act_tm and
            //check the stop before that
            if(stops[i].act_tm.equalsIgnoreCase("na")){
                if(i-1 >= 0){
                    last = stops[i-1];
                    break;
                }
            }
        }
        if(last != null){
            String lastStop = "Latest Stop: " + last.station + " @ " + last.act_tm;
            return lastStop;
        }else{
            return "Finished.";
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if(cmd.equalsIgnoreCase("back")){
            run(lastPanel);
        }else if(cmd.equalsIgnoreCase("map")){
            //create a new JFrame, display it and populate it with a map
            MapViewOptions options = new MapViewOptions();
            options.importPlaces();
            mapPanel = new MapPanel(options, train);
            JFrame frame = new JFrame("Map - Click to Update");
            frame.add(mapPanel, BorderLayout.CENTER);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
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
