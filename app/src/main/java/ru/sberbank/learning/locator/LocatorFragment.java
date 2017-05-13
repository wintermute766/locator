package ru.sberbank.learning.locator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

/**
 * Created by user10 on 13.05.2017.
 */

public class LocatorFragment extends Fragment implements LocationListener {

    private TextView positionView;
    private TextView addressView;

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
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 42);
            return;
        }

        for (int i = 0; i < lm.getProviders(true).size(); i++) {
            lm.requestLocationUpdates(lm.getProviders(true).get(i), 1000, 10f, this);
        }
    }

    private void stopListening() {

    }

    @Override
    public void onLocationChanged(Location location) {
        positionView.setText(getString(R.string.location_format,
                location.getLatitude(),
                location.getLongitude()));
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
        }
    }
}
