package info.androidhive.volleyexamples.webapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import info.androidhive.volleyexamples.R;
import info.androidhive.volleyexamples.utils.AppController;
import info.androidhive.volleyexamples.listners.CommonListioner;
import info.androidhive.volleyexamples.utils.Const;
import info.androidhive.volleyexamples.utils.InternetReachability;
import info.androidhive.volleyexamples.utils.Util;

public class GetWebservice{

    private static final String KEY_DATA = "data" ;
    public static GetWebservice getWebservice;
    public static Activity mactivity;
    ProgressDialog PD;
    JSONObject jDataRequest;
    private RequestQueue mRequestQueue;
    public static final String TAG = AppController.class
            .getSimpleName();

    public static GetWebservice getInstance(Activity activity) {
        mactivity = activity;
        if (getWebservice == null)
            getWebservice = new GetWebservice();
        return getWebservice;
    }

    private JsonObjectRequest mJsObjRequest;
    private String mTag;


    public void getClientRequest(int methodname, String url, final JSONObject sRequest, final String requestqueue, final CommonListioner listner) {

        mTag = requestqueue;

        try {
            //jDataRequest = new JSONObject();
            //jDataRequest.put(KEY_DATA, sRequest);
            Util.getInstance(mactivity).showLog(url);
            //Util.getInstance(mactivity).showLog(jDataRequest.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (InternetReachability.hasConnection(mactivity)) {
            //splashUser connectionActions
            if (mTag.equalsIgnoreCase("getMyMatchPagination")
                    || mTag.equalsIgnoreCase("connectionLike")
                    || mTag.equalsIgnoreCase("splashUser")
                    || mTag.equalsIgnoreCase("getChatGift")
                    || mTag.equalsIgnoreCase("connectionActions")
                    || mTag.equalsIgnoreCase("getUpdateLocation")
                    || mTag.equalsIgnoreCase("updateGCMToken")) {
            } else {
                if (PD == null || !PD.isShowing()) {
                    PD = new ProgressDialog(mactivity);
                    PD.setCancelable(false);
                    PD.show();
                    PD.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    PD.setContentView(R.layout.progressdialog);
                }
            }
            JsonObjectRequest stringRequest = new JsonObjectRequest(methodname, url, jDataRequest,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Util.getInstance(mactivity).showLog(response.toString());
                            setRequestQueryDismiss();
                            listner.onLoadSuccess(response);
                            /*try {
                                if (response.getInt(RESPONSE_CODE) == RESPONSE_CODE_VALUE) {
                                    listner.onLoadSuccess(response);
                                } else if (response.getInt(RESPONSE_CODE) == RESPONSE_CODE_204
                                        || response.getInt(RESPONSE_CODE) == RESPONSE_CODE_999
                                        || response.getInt(RESPONSE_CODE) == RESPONSE_CODE_997
                                        || response.getInt(RESPONSE_CODE) == RESPONSE_CODE_996) {
                                    listner.onLoadFail(response);
                                } else if (response.getInt(RESPONSE_CODE) == RESPONSE_CODE_401
                                        || response.getInt(RESPONSE_CODE) == RESPONSE_CODE_422
                                        || response.getInt(RESPONSE_CODE) == RESPONSE_CODE_498
                                        || response.getInt(RESPONSE_CODE) == RESPONSE_CODE_500) {
                                    if (response.has(KEY_RESPONSE_MSG)) {
                                        Util.getInstance(mactivity).ShowToast(response.getString(KEY_RESPONSE_MSG));
                                    }
                                } else if (response.getInt(RESPONSE_CODE) == RESPONSE_CODE_403) {
                                    Util.getInstance(mactivity).showErrorAlertDialog("Error!", response.getString(KEY_RESPONSE_MSG));
                                } else if (response.getInt(RESPONSE_CODE) == RESPONSE_CODE_998) {
                                *//*    Util.getInstance(mactivity).showErrorAlertDialog("Error!", response.getString(KEY_RESPONSE_MSG));*//*
                                }
                              *//*  else {
                                    errorMessage(response);
                                }*//*

                            } catch (Exception e) {
                                listner.onLoadFail(response);
                                setRequestQueryDismiss();
                            }*/
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            setRequestQueryDismiss();
                            SetVolleyError(error, requestqueue, listner);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    /*if (PrefsManager.getAccessTokenLogin().length() > 0) {
                        Util.getInstance(mactivity).showLog("getHeaders..." + PrefsManager.getAccessTokenLogin());
                        params.put(KEY_X_ACCESS_TOKEN, PrefsManager.getAccessTokenLogin());
                    }*/
                    return headers;
                }
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(KEY_DATA,sRequest.toString());
                    return params;
                }
            };
            int socketTimeout = 60000;//60 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            addToRequestQueue(stringRequest, requestqueue);
        } else {
            Util.getInstance(mactivity).ShowToast(mactivity.getResources().getString(R.string.alert_no_internet));
        }

    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mactivity);
        }
//        RequestQueue queue = new RequestQueue(new DiskBasedCache(new File(getCacheDir(), "volley")), network);
        return mRequestQueue;
    }

    public void setRequestQueryDismiss() {
        if (mTag.equalsIgnoreCase("getMyMatchPagination") || mTag.equalsIgnoreCase("getSwipeAction") || mTag.equalsIgnoreCase("splashUser")) {
        } else {
            isDismiss();
        }
    }


    public void isDismiss() {
        if (PD != null) {
            if (PD.isShowing()) {
                PD.dismiss();
            }
        }
    }

    public void SetVolleyError(VolleyError error, String methodType, CommonListioner listner) {
        if (error != null) {
            if (error.networkResponse != null) {
                if (error.getClass().equals(com.android.volley.TimeoutError.class)) {
                    Util.getInstance(mactivity).ShowToast("Timeout error");
                }
                if (error.networkResponse.data != null) {
                    byte[] bytes = error.networkResponse.data;
                    String sResponse = new String(bytes);
                    try {
                        JSONObject jResponse = new JSONObject(sResponse);
                    } catch (Exception e) {
                        setError(error);
                    }
                }
            } else {
                setError(error);
            }
        }
    }

    public void setError(VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Util.getInstance(mactivity).ShowToast("Sorry, there was a problem communicating with Datecoy. Please check network connectivity");
        } else if (error instanceof AuthFailureError) {
        } else if (error instanceof ServerError) {
            Util.getInstance(mactivity).ShowToast("The server is temporarily unable to service your request due to maintenance downtime or capacity problems. Please try again later.");
        } else if (error instanceof NetworkError) {
            //We can not proceed due to slow internet connection. Please try again later
            Util.getInstance(mactivity).ShowToast("The server is temporarily unable to service your request due to maintenance downtime or capacity problems. Please try again later.");
        } else if (error instanceof ParseError) {
        }
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public static String multipartRequest2(String urlTo, String parmas, String filepath[], String filefield[], String fileMimeType) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        try {
            //int i=0;
            //File upload start
            for (int i = 0; i < filepath.length; i++) {
                if (!filepath[i].equals("0")) {
                    File file = new File(filepath[i]);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    URL url = new URL(urlTo);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("POST");
                    //connection.setRequestProperty(KEY_X_ACCESS_TOKEN, PrefsManager.getAccessTokenLogin());
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    connection.setRequestProperty(filefield[i], filepath[i]);
                    outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield[i] + "\";filename=\"" + filepath[i] + "\"" + lineEnd);
                    outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                    outputStream.writeBytes(lineEnd);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        outputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    String issue_details_key = "data";
                    String issue_details_value = parmas;
                    try {
                        outputStream.writeBytes("Content-Disposition: form-data; name=\""
                                + issue_details_key + "\"" + lineEnd
                                + "Content-Type: application/json" + lineEnd);
                        outputStream.writeBytes(lineEnd);
                        outputStream.writeBytes(issue_details_value);
                        outputStream.writeBytes(lineEnd);
                        outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    } catch (IOException ioe) {
                        Util.getInstance(mactivity).showLog("Debug" + "error: " + ioe.getMessage() + ioe);
                    }

                    // Responses from the server (code and message)
                    int serverResponseCode = connection.getResponseCode();
                    String serverResponseMessage = connection.getResponseMessage();

                    Log.i("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);
                    inputStream = connection.getInputStream();
                    result = convertStreamToString(inputStream);
                    fileInputStream.close();
                    inputStream.close();
                    outputStream.flush();
                    outputStream.close();
                    // 400 422  401
                    //500
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject getCommonPara(JSONObject jBasic) {
        /*try {
            String deviceId = Util.getInstance(mactivity).getDeviceId();
            jBasic.put(KEY_UUID, deviceId);
            if (PrefsManager.getUid().length() > 0 && !PrefsManager.getUid().equalsIgnoreCase("0")) {
                jBasic.put(KEY_UID, PrefsManager.getUid());
            }
        } catch (Exception e) {
        }*/
        return jBasic;
    }

    /* Forgot Password API call*/
   /* public void resendOTP(JSONObject jForgotPwdPara, CommonListioner commonListioner) {
        getClientRequest(Request.Method.POST, Const.URL_JSON_OBJECT, getCommonPara(jForgotPwdPara), "ResendOTP", commonListioner);
    }*/

    /* Forgot Password API call*/
    /*public void forgotpasswordUser(JSONObject jForgotPwdPara, CommonListioner commonListioner) {
        getClientRequest(Request.Method.POST, Const.FORGOTPASSWORD, getCommonPara(jForgotPwdPara), "ForgotPassword", commonListioner);
    }*/

    /* Change Password or Forgot Change Password API call*/
    /*public void changepasswordUser(JSONObject jChangePWdPara, CommonListioner commonListioner) {
        getClientRequest(Request.Method.POST, Const.CHANGEPASSWORD, getCommonPara(jChangePWdPara), "ChangePassword", commonListioner);
    }*/


    /* Login API call*/
    /*public void loginUser(JSONObject jLoginUserPara, CommonListioner commonListioner) {
        getClientRequest(Request.Method.POST, Const.LOGIN, getCommonPara(jLoginUserPara), "Login", commonListioner);
    }*/

    //Signout
   /* public void signOutUser(CommonListioner commonListioner) {
        JSONObject request = new JSONObject();
        getClientRequest(Request.Method.POST, Const.USER_SIGNOUT, getCommonPara(request), "signOutUser", commonListioner);
    }*/
    public void jsonRequest(JSONObject request, CommonListioner commonListioner) {
        getClientRequest(Request.Method.POST, Const.URL_JSON_OBJECT, request, "jsonRequest", commonListioner);
    }

}
