/* Copyright 2015 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.gisluq.runtimeviewer.BMOD.MapModule.PartView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


import com.esri.arcgisruntime.mapping.view.MapRotationChangedEvent;
import com.esri.arcgisruntime.mapping.view.MapRotationChangedListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.gisluq.runtimeviewer.R;

/**
 * 地图旋转控制组件，当地图旋转时显示
 */
public class Compass extends View {

    float mAngle = 0;
    Paint mPaint;
    Bitmap mBitmap;
    Matrix mMatrix;

    MapView mMapView;

    // Called when the Compass view is inflated from XML. In this case, no attributes are initialized from XML.
    public Compass(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setVisibility(GONE);//默认不显示

        // Create a Paint, Matrix and Bitmap that will be re-used together to draw the
        // compass image each time the onDraw method is called.
        mPaint = new Paint();
        mMatrix = new Matrix();

        // Create the bitmap of the compass from a resource.
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.compass2);
    }

    /** Overloaded constructor that takes a MapView, from which the compass rotation angle will be set. */
    public Compass(Context context, AttributeSet attrs, MapView mapView) {
        this(context, attrs);

        this.setVisibility(GONE);//默认不显示


        // Save reference to the MapView passed in to this compass.
        mMapView = mapView;
        if (mMapView != null) {

            // Set an OnPinchListener on the map to listen for the pinch gesture which may change the map rotation.
            mMapView.addMapRotationChangedListener (new MapRotationChangedListener() {

                @Override
                public void mapRotationChanged(MapRotationChangedEvent mapRotationChangedEvent) {
                    setRotationAngle(mMapView.getMapRotation());
                }
            });
        }
    }

    /** Updates the angle, in degrees, at which the compass is draw within this view. */
    public void setRotationAngle(double angle) {
        // Save the new rotation angle.
        mAngle = (float) angle;

        this.setVisibility(VISIBLE);//只要一旋转就显示出来

        // Force the compass to re-paint itself.
        postInvalidate();
    }

    /** Draws the compass image at the current angle of rotation on the canvas. */
    @Override
    protected void onDraw(Canvas canvas) {

        // Reset the matrix to default values.
        mMatrix.reset();

        // Pass the current rotation angle to the matrix. The center of rotation is set to be the center of the bitmap.
        mMatrix.postRotate(-this.mAngle, mBitmap.getHeight() / 2, mBitmap.getWidth() / 2);

        // Use the matrix to draw the bitmap image of the compass.
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        super.onDraw(canvas);

    }

}
