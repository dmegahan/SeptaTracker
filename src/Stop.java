/**
 * Created by Danny on 8/28/2016.
 */
public class Stop {
    String station;
    String sched_tm;
    String est_tm;
    //we dont really care about the actual time it got there (because that means its already
    //arrived and left) but it helps us weed out stops that already happened and are thus useless
    String act_tm;

    public Stop(String station, String sched_tm, String est_tm, String act_tm){
        this.station = station;
        this.sched_tm = sched_tm;
        this.est_tm = est_tm;
        this.act_tm = act_tm;
    }
}
