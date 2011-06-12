package com.android.mdw.demo;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class Main extends MapActivity implements LocationListener {	
    class MyOverlay extends Overlay {
    	GeoPoint point;
    	
    	public MyOverlay(GeoPoint point) {
    		super();
    		this.point = point;
    	}
    	
        @Override
        public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
            super.draw(canvas, mapView, shadow);                   
 
            Point scrnPoint = new Point();
            mapView.getProjection().toPixels(this.point, scrnPoint);
 
            Bitmap marker = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
            canvas.drawBitmap(marker,
            		scrnPoint.x - marker.getWidth() / 2,
            		scrnPoint.y - marker.getHeight() / 2, null);
            return true;
        }
    }	
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        updateLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 50, this);
        
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		updateLocation(location);
	}

	@Override
	public void onProviderDisabled(String provider) { 
		Intent intent = new Intent( android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}	

	@Override
	public void onProviderEnabled(String provider) {}
	
	protected void updateLocation(Location location){
		MapView mapView = (MapView) findViewById(R.id.mapview);
        MapController mapController = mapView.getController();
        GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
        mapController.animateTo(point);        
        mapController.setZoom(15);
        
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geoCoder.getFromLocation(
                point.getLatitudeE6()  / 1E6, 
                point.getLongitudeE6() / 1E6, 1);

            String address = "";
            if (addresses.size() > 0) {
                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++)
                   address += addresses.get(0).getAddressLine(i) + "\n";
            }

            Toast.makeText(getBaseContext(), address, Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {                
            e.printStackTrace();
        }           

        
        List<Overlay> mapOverlays = mapView.getOverlays();
        MyOverlay marker = new MyOverlay(point);
        mapOverlays.add(marker);  
        mapView.invalidate();		
	}
	
}