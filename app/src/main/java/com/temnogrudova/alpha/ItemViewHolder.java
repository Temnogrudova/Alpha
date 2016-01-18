package com.temnogrudova.alpha;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    private ImageView imgIcon;
    private TextView txtText;
    private TextView txtTime;
    private TextView txtName;
    private Context context;


    public ItemViewHolder(final View parent, ImageView imgIcon, TextView txtText, TextView txtTime, TextView txtName, Context context) {
        super(parent);
        this.imgIcon = imgIcon;
        this.txtText = txtText;
        this.txtTime = txtTime;
        this.txtName = txtName;
        this.context = context;
    }

    public static ItemViewHolder newInstance(View parent, Context context) {
        ImageView imgIcon = (ImageView)parent.findViewById(R.id.icon);
        TextView txtName = (TextView)parent.findViewById(R.id.name);
        TextView txtText = (TextView)parent.findViewById(R.id.text);
        TextView txtTime = (TextView)parent.findViewById(R.id.time);
        return new ItemViewHolder(parent,imgIcon, txtText, txtTime, txtName, context);
    }

    public void setItemText(final String url, String text, String time, String name) {

        AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
            protected Bitmap doInBackground(Void... p) {
                Bitmap bm = null;
                try {
                    URL aURL = new URL(url);
                    URLConnection conn = aURL.openConnection();
                    conn.setUseCaches(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    bm = BitmapFactory.decodeStream(bis);
                    bis.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bm;
            }

            protected void onPostExecute(final Bitmap bm) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    Drawable drawable = new BitmapDrawable(context.getResources(),bm);
                    imgIcon.setImageDrawable(drawable);
                }catch (NullPointerException e){
                    imgIcon.setImageResource(R.drawable.ic_launcher);
                }

            }
        };
        t.execute();
        txtText.setText(text);
        txtTime.setText(time);
        txtName.setText(name);
    }
}

