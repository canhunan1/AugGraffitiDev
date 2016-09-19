package com.example.jianan.auggraffiti;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jianan on 9/17/2016.
 */
public class Tag {
        int tagId;
        LatLng ll;
        Tag(){

        };
        Tag(int tagId, LatLng ll){
            this.tagId = tagId;
            this.ll = ll;
        }
}
