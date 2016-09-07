package com.dorothy.v2ex.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class V2EXImageGetter implements Html.ImageGetter {


    private Context c;
    private TextView container;

    /***
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     *
     * @param t
     * @param c
     */
    public V2EXImageGetter(TextView t, Context c) {
        this.c = c;
        this.container = t;
    }

    public Drawable getDrawable(String source) {
        URLDrawable urlDrawable = new URLDrawable();
        ImageGetterAsyncTask asyncTask =
                new ImageGetterAsyncTask(urlDrawable);
        asyncTask.execute(source);

        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result == null) {
                return;
            }
            urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0
                    + result.getIntrinsicHeight());
            urlDrawable.drawable = result;
            // redraw the image by invalidating the container
            container.setText(container.getText());
        }

        /***
         * Get the Drawable from URL
         *
         * @param urlString
         * @return
         */
        public Drawable fetchDrawable(String urlString) {
            try {
                InputStream is = fetch(urlString);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Drawable drawable = new BitmapDrawable(c.getResources(), bitmap);
                drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }

        private InputStream fetch(String urlString) throws IOException {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(urlString).build();
            Response response = client.newCall(request).execute();
            return response.body().byteStream();
        }
    }


    public class URLDrawable extends BitmapDrawable {
        // the drawable that you need to set, you could set the initial drawing
        // with the loading image if you need to
        protected Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }
    }

}


