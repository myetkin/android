package com.yetkin.googlemaps;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;


public class MainActivity extends MapActivity implements LocationListener {
     MapView map;
     Long start;	
     Long stop;
     MyLocationOverlay  compass;
     MapController controller;
     int x,y,i;
     GeoPoint touchedPoint;
     Drawable d;
     List<Overlay>overlayList;
     int lat;
     int longi;
     LocationManager lm;
     String towers;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		   map=(MapView)findViewById(R.id.mvMain);
	       map.setBuiltInZoomControls(true);	
	 Touchy t=new Touchy();
	       overlayList=map.getOverlays();
	       overlayList.add(t);
	       compass=new MyLocationOverlay(MainActivity.this,map);
	       overlayList.add(compass);
	       controller=map.getController();
	 GeoPoint point=new GeoPoint(51643234,7848593);
	 	controller.animateTo(point);
	 	controller.setZoom(6);
	 
	   d=getResources().getDrawable(R.drawable.ic_launcher);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		compass.disableCompass();
		super.onPause();
		lm.removeUpdates(this);
	}
	@Override
	protected void onResume() {
		compass.enableCompass();
		super.onResume();
		lm.requestLocationUpdates(towers, 500, 1, this);
	}
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	class Touchy extends Overlay{
		public boolean onTouchEvent(MotionEvent e,MapView m)
		{
			if(e.getAction()==MotionEvent.ACTION_DOWN)
			{
				start=e.getEventTime();
				x=(int)e.getX();
				y=(int)e.getY();
				touchedPoint=map.getProjection().fromPixels(x, y);
				
			}
			if(e.getAction()==MotionEvent.ACTION_UP)
			{
				stop=e.getEventTime();
			}
			if(stop-start >1500)
			{
				AlertDialog alert=new AlertDialog.Builder(MainActivity.this).create();
				alert.setTitle("Pick an Option");
				alert.setMessage("Lütfen birini seçiniz!!!");
				alert.setButton("Place a pinpoint", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
			    
			     lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
			     
			     Criteria kriter=new Criteria();
			     towers=lm.getBestProvider(kriter, false);
			     Location location=lm.getLastKnownLocation(towers);
			     if(location !=null)
			     {
			    	 lat=(int)location.getLatitude();
			    	 longi=(int)location.getLongitude();
			    	 GeoPoint ourLocation=new GeoPoint(lat,longi);
				     OverlayItem overlayItem=new OverlayItem(touchedPoint,"What's up","2nd String");
					 CustomPinpoint custom=new CustomPinpoint(d,MainActivity.this);		
				     custom.insertPinpoint(overlayItem);	
				     overlayList.add(custom);
			     }else
			     {
			    	 Toast.makeText(MainActivity.this, "Couldn't get provider", Toast.LENGTH_SHORT).show();
			     }
			     
				
					}
				});
                alert.setButton2("Get an Address", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
                    Geocoder geocoder=new Geocoder(getBaseContext(),Locale.getDefault());
					
					try
					{
					List<Address> adress=geocoder.getFromLocation(touchedPoint.getLatitudeE6()/1E6, touchedPoint.getLongitudeE6()/1E6, 1);
					if(adress.size()>0)
					{
					String  display="";
					for(i=0;i<adress.get(0).getMaxAddressLineIndex();i++)
					{
						display+=adress.get(0).getAddressLine(i)+"\n";
					}
					Toast t=Toast.makeText(getBaseContext(),display,Toast.LENGTH_LONG);
					t.show();
					}
					}catch(IOException e)
					{
						e.printStackTrace();
					}finally
					{
					
					}
					}
				});
               alert.setButton3("Toggle View", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
						if(map.isSatellite())
						{
							map.setSatellite(false);
							map.setStreetView(true);
						}else
						{
                           map.setSatellite(true);
                           map.setStreetView(false);
						}
					}	
					
				});
               alert.show();
               
               return true;
			}
			
		return false;
		}
	}

	@Override
	public void onLocationChanged(Location l) {
		// TODO Auto-generated method stub
		 lat=(int)(l.getLatitude()*1E6);
    	 longi=(int)(l.getLongitude()*1E6);
    	 GeoPoint ourLocation=new GeoPoint(lat,longi);
	     OverlayItem overlayItem=new OverlayItem(touchedPoint,"What's up","2nd String");
		 CustomPinpoint custom=new CustomPinpoint(d,MainActivity.this);		
	     custom.insertPinpoint(overlayItem);	
	     overlayList.add(custom);
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
