import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Danny on 8/11/2016.
 */
public class SeptaTracker extends JFrame implements ActionListener {
    JFrame frame;
    JButton testButton;
    JPanel mainPanel;

    SearchPanel searchPanel;
    HomePanel homePanel;

    Thread observerThread;
    Thread homeObserverThread;

    JButton HomeButton;
    JButton NewsButton;
    JButton SearchButton;

    public SeptaTracker(){
        frame = new JFrame("SeptaTracker");
        frame.setPreferredSize(new Dimension(400,400));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(new GridLayout(1,3));

        HomeButton = new JButton("Home");
        HomeButton.setActionCommand("home");
        HomeButton.addActionListener(this);
        NewsButton = new JButton("News");
        NewsButton.setActionCommand("news");
        NewsButton.addActionListener(this);
        SearchButton = new JButton("Search");
        SearchButton.setActionCommand("search");
        SearchButton.addActionListener(this);

        tabPanel.add(HomeButton);
        tabPanel.add(NewsButton);
        tabPanel.add(SearchButton);

        mainPanel = new JPanel();

        frame.add(tabPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);

        SeptaAPI septa = new SeptaAPI();

        //add observers
        homePanel = new HomePanel();

        searchPanel = new SearchPanel();
        searchPanel.addObserver(new Observer() {
            public void update(Observable obj, Object arg){
                if(arg instanceof JPanel){
                    //if its a panel, display it
                    displayPanel((JPanel)arg);
                }else if(arg instanceof Train){
                    homePanel.trackTrain((Train)arg, false);
                }else{
                    //no arguement, assume its asking to go back to search panel
                    searchPanel.initializePanel();
                    displaySearchPanel();
                }
            }
        });
        observerThread = new Thread(searchPanel);
        observerThread.start();

        homePanel.addObserver(new Observer() {
            public void update(Observable obj, Object arg) {
                if (arg instanceof JPanel) {
                    //if its a panel, display it
                    displayPanel((JPanel) arg);
                } else if (arg instanceof String) {
                    //if its a string, then check which command it is
                    String sArg = (String) arg;
                    if (sArg.equalsIgnoreCase("back")) {
                        //go back sent from resultsPanel, reinitialize SearchPanel and display it
                        homePanel.InitializePanel();
                        displayHomePanel();
                    }
                }
            }
        });

        homeObserverThread = new Thread(searchPanel);
        homeObserverThread.start();

        displaySearchPanel();
        frame.repaint();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                homePanel.serializeTrackedTrains();
            }
        });

        ImportTrackedTrains();
    }

    public void ImportTrackedTrains(){
        //takes the json in the file, creates Train objects from it and sends them to be tracked
        FileReadWriter reader = new FileReadWriter("./tracked.txt");
        String contents = reader.readFromFile();

        if(!contents.equalsIgnoreCase("") || !contents.isEmpty()){
            System.out.println(contents);
            SeptaJSON septaJSON = new SeptaJSON();
            Train[] trains = septaJSON.parseNextToArrives("", "", contents);
            for(int i = 0; i < trains.length; i++){
                homePanel.trackTrain(trains[i], true);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if(cmd.equalsIgnoreCase("home")){
            displayHomePanel();
        }else if(cmd.equalsIgnoreCase("news")){
            displayNewsPanel();
        }else if(cmd.equalsIgnoreCase("search")){
            displaySearchPanel();
        }
    }

    public void displayHomePanel(){
        //reset our buttons and make it so the pressed button appears pressed from now on
        HomeButton.setEnabled(false);
        SearchButton.setEnabled(true);
        NewsButton.setEnabled(true);

        JPanel home = homePanel.getHomePanel();
        frame.remove(mainPanel);
        mainPanel = home;
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.repaint();
        frame.pack();
    }

    public void displayNewsPanel(){
        //reset our buttons and make it so the pressed button appears pressed from now on
        HomeButton.setEnabled(true);
        SearchButton.setEnabled(true);
        NewsButton.setEnabled(false);

        NewsPanel newsPanel = new NewsPanel();
        JPanel news = newsPanel.getNewsPanel();
        frame.remove(mainPanel);
        mainPanel = news;
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.repaint();
        frame.pack();
    }

    public void displaySearchPanel(){
        //reset our buttons and make it so the pressed button appears pressed from now on
        HomeButton.setEnabled(true);
        SearchButton.setEnabled(false);
        NewsButton.setEnabled(true);

        JPanel search = searchPanel.getSearchPanel();
        frame.remove(mainPanel);
        mainPanel = search;
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.repaint();
        frame.pack();
    }

    public void displayPanel(JPanel panel){
        frame.remove(mainPanel);
        mainPanel = panel;
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.repaint();
        frame.pack();
    }
}
