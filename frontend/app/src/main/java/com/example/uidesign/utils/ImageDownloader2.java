package com.example.uidesign.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader2 extends AsyncTask<String, Void, Bitmap> {
    private ImageView imageView;

    public ImageDownloader2(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String imageUrl = urls[0];
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            if(result.getWidth() < 460 ||  result.getHeight() < 400){
                // 调用自定义的裁剪方法
                Bitmap scaledBitmap = scaleImageToFill(result, 460, 200);
                imageView.setImageBitmap(scaledBitmap);
                return;
            }
            imageView.setImageBitmap(result);
        }
    }

    private Bitmap scaleImageToFill(Bitmap originalBitmap, int targetWidth, int targetHeight) {
        float originalWidth = originalBitmap.getWidth();
        float originalHeight = originalBitmap.getHeight();

        float scaleX = targetWidth / originalWidth;
        float scaleY = targetHeight / originalHeight;
        float scale = Math.max(scaleX, scaleY);

        float scaledWidth = scale * originalWidth;
        float scaledHeight = scale * originalHeight;
        if(originalWidth == 0 || originalHeight == 0 || scale == 0) {
            return null;
        }
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, (int) scaledWidth, (int) scaledHeight, true);
        return scaledBitmap;
    }
}

