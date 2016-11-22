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
 * Created by Danny on 8/15/2016.
 */
public class ResultsPanel extends Observable implements Runnable, ActionListener{
    JPanel resultsPanel;
    JPanel summaryPanel;
    JPanel trainsPanel;
    JPanel buttonPanel;

    JLabel summaryLabel;
    DefaultTableModel model;
    JTable trainsTable;

    Vector<Train> trainsInTable;

    DetailsPanel details;
    Thread observerThread;

    public ResultsPanel(){
        trainsInTable = new Vector<Train>();
        resultsPanel = new JPanel(new BorderLayout());

        trainsPanel = new JPanel();
        trainsPanel.setLayout(new BoxLayout(trainsPanel, BoxLayout.Y_AXIS));

        //setting up button panel
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,3));

        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.X_AXIS));
        summaryLabel = new JLabel();

        details = new DetailsPanel(null, resultsPanel);
        details.addObserver(new Observer() {
            public void update(Observable obj, Object arg) {
                //we got something from the results panel, meaning we need to go back to the search panel
                if (arg instanceof JPanel) {
                    //got a panel, send it to the view controller to display
                    run((JPanel) arg);
                }
            }
        });
        observerThread = new Thread(details);
        observerThread.start();
    }

    public void InitializePanel(){
        resultsPanel.removeAll();
        summaryPanel.removeAll();
        trainsPanel.removeAll();
        buttonPanel.removeAll();
        trainsInTable.clear();

        JButton backButton = new JButton("Go Back");
        backButton.setActionCommand("back");
        backButton.addActionListener(this);

        summaryPanel.add(summaryLabel);

        String[] columnNames = {"Train Num", "Depart Time", "Arrival Time", "Delay?"};
        Object[][] data = {
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

        trainsPanel.add(scrollPane);

        JButton detailsButton = new JButton("Details");
        detailsButton.setActionCommand("details");
        detailsButton.addActionListener(this);
        JButton UnTrackButton = new JButton("Track");
        UnTrackButton.setActionCommand("track");
        UnTrackButton.addActionListener(this);

        buttonPanel.add(detailsButton);
        buttonPanel.add(UnTrackButton);
        buttonPanel.add(backButton);

        resultsPanel.add(summaryPanel, BorderLayout.NORTH);
        resultsPanel.add(trainsPanel, BorderLayout.CENTER);
        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public JPanel getResultsPanel(){
        return resultsPanel;
    }

    public void displayResults(String startLoc, String endLoc, Train[] trains){
        //will update the summary label and populate the trains list
        summaryLabel.setText("Results: " + startLoc + " to " + endLoc);

        for(int i = 0; i < trains.length; i++){
            DefaultTableModel model = (DefaultTableModel) trainsTable.getModel();
            model.addRow(new Object[]{trains[i].getOrig_train(),
                                        trains[i].getOrig_departure_time(),
                                        trains[i].getOrig_arrival_time(),
                                        trains[i].getOrig_delay()});
            trainsInTable.add(trains[i]);
        }
    }

    @Override
    public void run() {
        setChanged();
        notifyObservers();
    }

    public void run(Train train){
        setChanged();
        notifyObservers(train);
    }

    public void run(JPanel panel){
        setChanged();
        notifyObservers(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if(cmd.equalsIgnoreCase("details")){
            int selected = trainsTable.getSelectedRow();
            Train train = trainsInTable.get(selected);

            details.setTrain(train);
            details.displayTrainInfo(train);
            run(details.getDetailsPanel());
        }else if(cmd.equalsIgnoreCase("track")){
            Train selected = trainsInTable.get(trainsTable.getSelectedRow());
            run(selected);
        }else if(cmd.equalsIgnoreCase("back")){
            run();
        }
    }
}
