package ru.sberbank.learning.locator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by user10 on 13.05.2017.
 */

public class LocatorFragment extends Fragment implements LocationListener {

    private TextView positionView;
    private TextView addressView;

    boolean requested = false;

    private static final String[] PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_locator,
                container, false);

        positionView = (TextView) root.findViewById(R.id.position);
        addressView = (TextView) root.findViewById(R.id.address);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopListening();
    }

    private void startListening () {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (lm == null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && !requested) {
            requested = true;
            requestPermissions(PERMISSIONS, 42);
            return;
        }

        for (int i = 0; i < lm.getProviders(true).size(); i++) {
            lm.requestLocationUpdates(lm.getProviders(true).get(i), 1000, 10f, this);
        }
    }

    private void stopListening() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (lm == null) {
            return;
        }

        lm.removeUpdates(this);

    }

    @Override
    public void onLocationChanged(Location location) {
        positionView.setText(getString(R.string.location_format,
                location.getLatitude(),
                location.getLongitude()));
        GeocodeTask task = new GeocodeTask(this, location);
        task.execute();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        } else {
            positionView.setText(R.string.error_no_permission);
        }
    }


    private static class GeocodeTask extends AsyncTask<Void, Void, String> {

        private WeakReference<LocatorFragment> fragmentRef = new WeakReference<LocatorFragment>(null);
        private Location location;
        private Context context;

        public GeocodeTask(LocatorFragment fragment, Location location) {
            fragmentRef = new WeakReference<LocatorFragment>(fragment);
            this.location = location;
            context = fragment.getContext().getApplicationContext();
        }

        @Override
        protected String doInBackground(Void... params) {
            if (Geocoder.isPresent()) {
                Geocoder geocoder = new Geocoder(context);
                try {
                    List<Address> result =
                            geocoder.getFromLocation(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    1);
                    if (result.size() > 0) {
                        StringBuilder address = new StringBuilder();
                        for (int i = 0; i < result.get(0).getMaxAddressLineIndex(); i++) {
                            String line = result.get(0).getAddressLine(i);

                            if (line != null) {
                                address.append(line).append(" ");
                            }
                            return address.toString();
                        }
                    }


                } catch (IOException e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            LocatorFragment fragment = fragmentRef.get();

            if (fragment != null) {
                fragment.setAddress(s);
            }
        }
    }

    private void setAddress(String s) {
        if (s == null) {
            addressView.setText(R.string.location_not_found);
        }

        addressView.setText(s);
    }
}
