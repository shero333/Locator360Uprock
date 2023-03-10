package com.care360.findmyfamilyandfriends.cloud_notifications;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.care360.findmyfamilyandfriends.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MyNotificationSender {

    private final String userNotificationToken;
    private final String title;
    private final String body;
    private final Activity mActivity;

    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "AAAA_WhMbh0:APA91bEDUUKIq6yVYl8QxS9YuAoitfC0gGVd8xyXG0AmtVhUvWGtfmmvNfhPiUKshL1jnyytn0Z3Gcb5R-QScvFLNNdVatctFTKjVr9cxE21sKbc9VPmsphnHrwmdrIyqPgPvD8AGVL3";

    public MyNotificationSender(String userNotificationToken, String title, String body, Context mContext, Activity mActivity) {
        this.userNotificationToken = userNotificationToken;
        this.title = title;
        this.body = body;
        this.mActivity = mActivity;
    }

    public void send_notification(){

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject object = new JSONObject();

        try{

            object.put("to",userNotificationToken);

            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title",title);
            notificationObject.put("body",body);
            notificationObject.put("icon", R.mipmap.ic_launcher_round);

            object.put("notification",notificationObject);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, object, response -> {

            }, error -> {

            }){
                @Override
                public Map<String, String> getHeaders() {

                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=" + fcmServerKey);
                    return header;
                }
            };

            requestQueue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
