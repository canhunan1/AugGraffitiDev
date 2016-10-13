package com.example.jianan.auggraffiti;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jianan on 9/17/2016.
 * Tag is used to save the information of tags including the tagId, latitude and longitude
 */
public class Tag {
        int tagId;
        LatLng ll;

        Tag(int tagId, LatLng ll){
            this.tagId = tagId;
            this.ll = ll;
        }
}
