package com.example.jianan.auggraffiti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/*
*  This activity is used to show the tag image from the gallery
*  @author Jianan Yang
* */

public class ImageActivity extends AppCompatActivity {
    private ImageView imageView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = (ImageView) findViewById(R.id.imageViewGallery);
        Intent intent = getIntent();
        String imgUrl = intent.getStringExtra(GalleryActivity.EXTRA_MESSAGE);
        loadImg(imgUrl);
    }
    private void loadImg(String url){
        Picasso.with(this).load(url).into(imageView);
    }
}
