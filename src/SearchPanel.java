import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * Created by Danny on 8/13/2016.
 */
public class SearchPanel extends Observable implements ActionListener, Runnable{
    //entire panel
    JPanel panel;
    ResultsPanel results;
    //panel inside "panel" variable, holds the text fields and labels
    JPanel fieldsPanel;
    //panel that holds the search button
    JPanel validTrainsPanel;
    JTable validTrainsTable;
    JPanel searchPanel;
    JTextField departField;
    JTextField arriveField;

    Thread observerThread;

    String[] validTrains;
    DefaultTableModel model;

    public SearchPanel(){
        panel = new JPanel();

        results = new ResultsPanel();
        results.addObserver(new Observer() {
            public void update(Observable obj, Object arg){
                //we got something from the results panel, meaning we need to go back to the search panel
                if(arg instanceof Train){
                    //we got a train, so we want to send it to the tracker to add to the list on the HomePanel
                    run((Train)arg);
                }else if(arg instanceof JPanel){
                    //got a panel, send it to the view controller to display
                    run((JPanel)arg);
                }else{
                    System.out.println("got signal for going back");
                    initializePanel();
                    run();
                }
            }
        });
        observerThread = new Thread(results);
        observerThread.start();

        //this.initializePanel();
    }

    public void initializePanel(){
        panel.removeAll();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        fieldsPanel = new JPanel(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setLayout(new GridLayout(2, 2));

        JLabel departLabel = new JLabel("Depart @");
        JLabel arriveLabel = new JLabel("Arrive @");

        departField = new JTextField();
        arriveField = new JTextField();

        fieldsPanel.add(departLabel);
        fieldsPanel.add(departField);
        fieldsPanel.add(arriveLabel);
        fieldsPanel.add(arriveField);

        panel.add(fieldsPanel);

        validTrainsPanel = new JPanel();
        validTrainsPanel.setLayout(new BoxLayout(validTrainsPanel, BoxLayout.Y_AXIS));

        JLabel validTrainsLabel = new JLabel("Valid Trains - Case Sensitive");
        String validTrainsString = "Jenkintown-Wyncote,Elkins Park,Melrose Park,Fern Rock TC," +
                "North Broad St,Temple U,Jefferson Station,Suburban Station,30th Street Station, Overbrook," +
                "Merion,Narbeth,Wynnewood,Ardmore,Haverford,Bryn Mawr,Rosemont,Villanova,Radnor," +
                "St. Davids,Wayne-A,Strafford,Devon,Berwyn,Daylesford,Paoli,Malvern";
        validTrains = validTrainsString.split(",");

        String[] columnNames = {"Train1", "Train2", "Train3"};

        model = new DefaultTableModel();
        for(int i = 0; i < columnNames.length; i++){
            model.addColumn(columnNames[i]);
        }

        validTrainsTable = new JTable(model);
        validTrainsTable.setRowHeight(28);
        //make it so the user cant edit the table
        validTrainsTable.setDefaultEditor(Object.class, null);
        //center the objects in our list in the center of their cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        for(int i = 0; i < validTrainsTable.getColumnCount(); i++){
            validTrainsTable.getColumn(columnNames[i]).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane( validTrainsTable);
        validTrainsTable.setFillsViewportHeight(true);
        validTrainsTable.getTableHeader().setVisible(false);

        validTrainsPanel.add(scrollPane);

        validTrainsPanel.add(validTrainsLabel);
        validTrainsPanel.add(validTrainsTable);

        populateValidTrains();

        //search button setup
        searchPanel = new JPanel();
        JButton searchButton = new JButton("Search");
        searchButton.setActionCommand("search");
        searchButton.addActionListener(this);
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(searchButton, BorderLayout.CENTER);

        panel.add(validTrainsPanel);
        panel.add(searchPanel);
    }

    public JPanel getSearchPanel(){
        return panel;
    }

    public void populateValidTrains(){
        for(int i = 0; i < validTrains.length; i=i+3){
            DefaultTableModel model = (DefaultTableModel) validTrainsTable.getModel();
            //sloppy but we know there are 27 stops, so we divide by 3 and add 3 at a time. Shouldnt indexOutOfRange
            model.addRow(new Object[]{validTrains[i], validTrains[i+1], validTrains[i+2]});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if(cmd.equalsIgnoreCase("search")){
            //get all the text inside the text boxes (date and time not required)
            String departureLoc = departField.getText();
            String arrivalLoc = arriveField.getText();

            System.out.println("departure: " + departureLoc);
            System.out.println("arrival: " + arrivalLoc);

            if(departureLoc.equalsIgnoreCase("") || arrivalLoc.equalsIgnoreCase("")
                                        || departureLoc == null || arrivalLoc == null){
                System.out.println("Missing required departure location or arrival location.");
                return;
            }

            boolean validTo = false;
            boolean validFrom = false;

            for(int i = 0; i < validTrains.length; i++){
                if(departureLoc.equals(validTrains[i])){
                    validFrom = true;
                }
                if(arrivalLoc.equals(validTrains[i])){
                    validTo = true;
                }
            }

            if(validTo == false || validFrom == false){
                System.out.println("Invalid inputs.");
                return;
            }

            SeptaAPI sAPI = new SeptaAPI();

            String arrivals = sAPI.getNextToArrive(departureLoc, arrivalLoc, 50);

            SeptaJSON jsonObject = new SeptaJSON();
            Train[] trains = jsonObject.parseNextToArrives(departureLoc, arrivalLoc, arrivals);

            if(trains == null){
                return;
            }

            results.InitializePanel();
            results.displayResults(departureLoc, arrivalLoc, trains);
            //panel = results.getResultsPanel();
            run(results.getResultsPanel());
        }
    }

    @Override
    public void run() {
        setChanged();
        notifyObservers(panel);
    }

    public void run(JPanel panel){
        setChanged();
        notifyObservers(panel);
    }

    public void run(Train train) {
        setChanged();
        notifyObservers(train);
    }
}
