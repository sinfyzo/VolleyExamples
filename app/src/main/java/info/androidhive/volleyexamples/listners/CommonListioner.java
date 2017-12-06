package info.androidhive.volleyexamples.listners;


import org.json.JSONObject;

public interface CommonListioner {
//    public void onLoadFailVolleyError(VolleyError error);
void onLoadFail(JSONObject error);
    void onLoadSuccess(JSONObject success);
}
