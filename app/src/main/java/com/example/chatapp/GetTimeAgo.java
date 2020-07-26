package com.example.chatapp;

import android.app.Application;
import android.content.Context;

public class GetTimeAgo extends Application {
    private  final int SECOND_MILLIS = 1000;
    private  final int MINUTE_MILLIS = 60*SECOND_MILLIS;
    private  final int HOUR_MILLIS = 60*MINUTE_MILLIS;
    private  final int DAY_MILLIS = 24*HOUR_MILLIS;

    public  String getTimeAgo(long time ,Context ctx){
        if(time<1000000000000L){
            time*=1000;
        }
        long now = System.currentTimeMillis();
        if(time >now || time<=0){
            return null;
        }

        final long diff = now - time;
        if(diff <MINUTE_MILLIS){
            return "just now";

        }
        else if(diff<2*MINUTE_MILLIS){
            return "a minute ago";
        }
        else if(diff<50*MINUTE_MILLIS){
            return diff/MINUTE_MILLIS + " minutes ago";
        }
        else if(diff<90*MINUTE_MILLIS){
            return "a hour ago";
        }
        else if(diff<24*HOUR_MILLIS){
            return diff/HOUR_MILLIS+ " hours ago";
        }
        else if(diff<48*MINUTE_MILLIS){
            return "yesterday";
        }
        else{
            return (diff/DAY_MILLIS) + "days ago";
        }
    }


}
