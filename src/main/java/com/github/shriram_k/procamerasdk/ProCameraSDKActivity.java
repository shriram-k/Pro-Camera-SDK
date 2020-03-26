package com.github.shriram_k.procamerasdk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.shriram_k.procamerasdk.fragments.CameraFragment;
import com.github.shriram_k.procamerasdk.fragments.PhotoPreviewFragment;
import com.github.shriram_k.procamerasdk.interfaces.PhotoTaken;

import java.util.Objects;

public class ProCameraSDKActivity extends AppCompatActivity implements PhotoTaken {
    private String[] PERMISSIONS = new String[] {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private int PERMISSION_REQUEST_CODE = 3118;
    private FrameLayout fragmentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pro_camera_s_d_k);
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bindViews();

        if(hasAllPermissions()) {
            startCameraFragment();
        }else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    private void bindViews() {
        fragmentView = findViewById(R.id.proCameraSdkfragmentView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE) {
            if(hasAllPermissions()) {
                startCameraFragment();
            }else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean hasAllPermissions() {
        /*
           Checking if all permissions are granted.
         */
        for(String permission : PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    private void startCameraFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.proCameraSdkfragmentView, new CameraFragment(this));
        fragmentTransaction.commit();
    }

    @Override
    public Image onPhotoTaken(Image image) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.proCameraSdkfragmentView, new PhotoPreviewFragment(image));
        fragmentTransaction.commit();
        return null;
    }

}
