package cloudcam.cognitiveclouds.space.helpers;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by ROOK on 3/28/2016.
 */
public class UpPic {
    public void uppics(Bitmap bitmap) throws IOException {


// Creates Byte Array from picture
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Not sure whether this should be jpeg or png, try both and see which works best
        URL url = null;
        try {
            url = new URL("https://api.imgur.com/3/upload");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //encodes picture with Base64 and inserts api key
        String data = null;
        try {
            data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(Base64.encode(baos.toByteArray(), Base64.DEFAULT).toString(), "UTF-8");
            data += "&" + URLEncoder.encode("Authorization", "UTF-8") + "=" + URLEncoder.encode("Client-ID 70a225e48cb701c", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
// opens connection and sends data
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();
        Log.e("wr.write", "Writing data: " + data);
        // Get the response
        Log.e("GET THE RESPONSE", "Debug 1");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        conn.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            Log.e("Uploadpath is: ", inputLine);
        in.close();

    }
}
