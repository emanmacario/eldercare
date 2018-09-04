package au.edu.unimelb.eldercare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Utilities {
    static public ArrayList<Integer> jsonGetIntArrayList(JSONObject jsonObject, String name)
            throws JSONException{
        JSONArray jsonArray = jsonObject.getJSONArray(name);
        ArrayList<Integer> intArrayList = new ArrayList<>(jsonArray.length());
        for(int i=0; i < jsonArray.length(); i++){
            intArrayList.add(jsonArray.getInt(i));
        }
        return intArrayList;
    }
}
