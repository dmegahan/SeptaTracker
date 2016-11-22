import twitter4j.Status;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danny on 8/13/2016.
 */
public class NewsPanel implements ActionListener {
    JPanel newsPanel;
    JPanel septaNewsPanel;
    JPanel septaTwitterPanel;
    JPanel buttonPanel;

    SeptaTwitter twitter;
    JScrollPane scroller;

    JTextArea twitterArea;
    public NewsPanel(){
        newsPanel = new JPanel();
        newsPanel.setLayout(new BoxLayout(newsPanel, BoxLayout.Y_AXIS));
        buttonPanel = new JPanel(new BorderLayout());

        this.initializePanel();
    }

    public void initializePanel(){
        septaTwitterPanel = new JPanel();
        septaTwitterPanel.setLayout(new BoxLayout(septaTwitterPanel, BoxLayout.Y_AXIS));
        JLabel twitterHeader = new JLabel("Most Recent Tweets from Septa");
        twitterArea = new JTextArea("<html> This is a test label for Septas twitter.<br> Once the twitter API is working," +
                "tweets will be automatically taken from Septas twitter. Originally, the plan was to display the latest" +
                "tweet, but this area may retrive and display more tweets than that. </html>");

        Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
        twitterArea.setBorder(border);
        twitterArea.setLineWrap(true);
        twitterArea.setEditable(false);
        twitterArea.setFocusable(true);
        twitterArea.setWrapStyleWord(true);

        septaTwitterPanel.add(twitterHeader);
        septaTwitterPanel.add(twitterArea);

        scroller = new JScrollPane(twitterArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        septaTwitterPanel.add(scroller);

        JButton getMore = new JButton("Refresh Tweets");
        getMore.setActionCommand("refresh");
        getMore.addActionListener(this);
        buttonPanel.add(getMore);

        newsPanel.add(septaTwitterPanel);
        newsPanel.add(buttonPanel);

        twitter = new SeptaTwitter();
        populateTweets(twitter.searchForTweets());
    }

    public void populateTweets(List<Status> tweets){
        int maxDisplayedTweets = 100;
        //initialize with begining hhtml tag, will need to close it at the end
        twitterArea.setText("");

        if(tweets.size() < maxDisplayedTweets){
            maxDisplayedTweets = tweets.size();
        }
        for(int i = 0; i < maxDisplayedTweets; i++){
            Status tweet = tweets.get(i);
            String url = "https://twitter.com/SEPTA/status/" + String.valueOf(tweet.getId());
            String strTweet = tweet.getCreatedAt() + ", " + tweet.getText() + ", " + url;
            twitterArea.setText(twitterArea.getText() + strTweet + "\n\n");
        }
        twitterArea.setText(twitterArea.getText());
    }

    public JPanel getNewsPanel(){
        return newsPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if(cmd.equalsIgnoreCase("refresh")){
            twitter = new SeptaTwitter();
            populateTweets(twitter.searchForTweets());
        }
    }
}
