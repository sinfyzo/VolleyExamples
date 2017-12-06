package info.androidhive.volleyexamples.listners;


import android.widget.ImageView;
public interface DownloadListioner {
//    public void onLoadFailVolleyError(VolleyError error);
void onDownloadFail();
    void onDownloadSuccess(boolean isDownload, int messageType, String loginid, ImageView ivPlay);
    void onDownloadSuccess(boolean isDownloaded, String filePath, int fileType);
}
