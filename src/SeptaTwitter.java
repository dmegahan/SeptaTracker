import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Danny on 8/28/2016.
 */

public class SeptaTwitter {
    String septaTwitterHandle;
    Twitter twitter;
    public SeptaTwitter(){
        // The factory instance is re-useable and thread safe.
        //set up our oauth tokens
        String CONSUMER_KEY = "tHEgvLJ667nGQdctRBpsY2A7U";
        String CONSUMER_SECRET = "fAZHaCdX2UrgNQpxpWk4vd8EFVl7vR6Ky9mNbRBrEiavub8kAx";

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setApplicationOnlyAuthEnabled(true);
        builder.setOAuthConsumerKey(CONSUMER_KEY);
        builder.setOAuthConsumerSecret(CONSUMER_SECRET);
        Configuration configuration = builder.build();

        TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();
        try {
            twitter.getOAuth2Token();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public List<Status> searchForTweets(){
        List<Status> tweets = new ArrayList<Status>();
        try {
            tweets = twitter.getUserTimeline("SEPTA");
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return tweets;
        /*
        Query query = new Query("source:twitter4j SEPTA");
        QueryResult result = null;
        try {
            result = twitter.search(query);
        } catch (TwitterException e) {
            e.printStackTrace();
            System.out.println("SearchForTweets: something went wrong (Twitter exception)");
        }
        for (Status status : result.getTweets()) {
            System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
        }*/
    }
}
