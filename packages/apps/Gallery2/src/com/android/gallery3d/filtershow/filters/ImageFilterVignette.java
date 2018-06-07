/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;

import android.util.Log;

public class ImageFilterVignette extends ImageFilter {

    public ImageFilterVignette() {
        setFilterType(TYPE_VIGNETTE);
        mName = "Vignette";
    }

    native protected void nativeApplyFilter(Bitmap bitmap, int w, int h, float strength);

    @Override
    public Bitmap apply(Bitmap bitmap, float scaleFactor, boolean highQuality) {
        if (null == bitmap) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float p = mParameter;
        float value = p / 100.0f;

        Bitmap applyBitmap = bitmap;

        if (Bitmap.Config.ARGB_8888 != applyBitmap.getConfig()) {
            Log.i("ImageFilterVignette", "bitmap.getConfig() is " + applyBitmap.getConfig());
            applyBitmap = applyBitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
        //nativeApplyFilter(bitmap, w, h, value);
        nativeApplyFilter(applyBitmap, w, h, value);

        //return bitmap;
        return applyBitmap;
    }
}
