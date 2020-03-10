package com.tistory.black_jin0427.myandroidarchitecture.api;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class JSONUtil {
    public Context context = null;
    public String TAG = "JSONUTIL";

    public JSONUtil(Context context) {
        this.context = context;
    }

    public JSONArray jsonParsing(String json) {
        JSONArray resultArray = null;

        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject newObject = null;
            resultArray = new JSONArray();
            JSONObject branchDataObject = jsonObject.getJSONObject("data");
            JSONObject buildingObject = branchDataObject.getJSONObject("6831");
            JSONObject floorsObject = buildingObject.getJSONObject("floors");
            Log.d(TAG, "" + floorsObject);

            Iterator i = floorsObject.keys();
            while (i.hasNext()) {
                String floor = i.next().toString();

                Log.d(TAG, "key " + floor + "  value : " + floorsObject.get(floor));

                Iterator j = floorsObject.getJSONObject(floor).getJSONObject("customPointData").keys();
                while (j.hasNext()) {
                    String poiId = j.next().toString();
                    Log.d(TAG, "poiId " + poiId + "  value : " + floorsObject.getJSONObject(floor).getJSONObject("customPointData").get(poiId));

                    newObject = new JSONObject();

                    newObject.put("poiId", poiId);
                    newObject.put("floorId", floor); // floorId -> floorCode?
                    newObject.put("floorName", floorsObject.getJSONObject(floor).getJSONObject("name"));
                    newObject.put("floorOrder", floorsObject.getJSONObject(floor).getInt("order"));
                    newObject.put("floorIndex", floorsObject.getJSONObject(floor).getDouble("index"));
                    newObject.put("attributes", floorsObject.getJSONObject(floor).getJSONObject("customPointData").getJSONObject(poiId).getJSONObject("attributes"));
                    newObject.put("radius", floorsObject.getJSONObject(floor).getJSONObject("customPointData").getJSONObject(poiId).getInt("radius"));
                    newObject.put("isRestricted", floorsObject.getJSONObject(floor).getJSONObject("customPointData").getJSONObject(poiId).getInt("isRestricted"));
                    newObject.put("name", floorsObject.getJSONObject(floor).getJSONObject("customPointData").getJSONObject(poiId).getJSONObject("name"));
                    newObject.put("pos", floorsObject.getJSONObject(floor).getJSONObject("customPointData").getJSONObject(poiId).getJSONObject("pos"));
                    newObject.put("theta", floorsObject.getJSONObject(floor).getJSONObject("customPointData").getJSONObject(poiId).getInt("theta"));
                    newObject.put("type", floorsObject.getJSONObject(floor).getJSONObject("customPointData").getJSONObject(poiId).getInt("type"));
                    newObject.put("isHome", false); // default false
                    newObject.put("isCharger", false); // default false
                    newObject.put("isInPOIList", true); // default true
                    Log.d(TAG, "transfer " + newObject);
                    resultArray.put(newObject);
                }
            }
            Log.d(TAG, "To JSONArray " + resultArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultArray;
    }

    public String getJsonFromStorage() {
        String json = "";
        try {
            File poiFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "poi_object_sample_data.json");
            if (poiFile.exists()) {
                InputStream is = null;
                try {
                    is = new FileInputStream(poiFile);
                    int fileSize = is.available();

                    byte[] buffer = new byte[fileSize];
                    is.read(buffer);

                    json = new String(buffer, "UTF-8");
                    Log.d(TAG, "getJsonFromStorage Success from poi_object_sample_data.json in DIRECTORY_DOWNLOADS");

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    is.close();
                    //poiFile.delete();
                }
            } else {
                Log.d(TAG, "getJsonFromStorage Fail from poi_object_sample_data.json in DIRECTORY_DOWNLOADS. There is no such file");
                return getJsonString();

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Log.d(TAG, "getJsonFromStorage : " + json);
        return json;
    }

    public String getJsonString() {
        String json = "";

        try {
            InputStream is = context.getAssets().open("poi_object_sample_data.json");
            int fileSize = is.available();
            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Log.d(TAG, "getJsonString Success from poi_object_sample_data.json in assets (RobotPlatformLibrary)");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Log.d(TAG, "getJsonString : " + json);
        return json;
    }
}
