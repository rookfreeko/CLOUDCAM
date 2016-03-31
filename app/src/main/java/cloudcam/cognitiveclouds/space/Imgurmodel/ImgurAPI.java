package cloudcam.cognitiveclouds.space.Imgurmodel;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.mime.TypedFile;


public interface ImgurAPI {
    String server = "https://api.imgur.com";




    /**
     * @param auth        #Type of authorization for upload
     * @param file        image
     * @param cb          Callback used for success/failures
     */

    @POST("/3/image")
    void postImage(
            @Header("Authorization") String auth,
            @Query("title") String title,
            @Query("description") String description,
            @Query("album") String albumId,
            @Query("account_url") String username,
            @Body TypedFile file,
            Callback<ImageResponse> cb
    );
}
