package com.github.shriram_k.procamerasdk.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.shriram_k.procamerasdk.R;
import com.github.shriram_k.procamerasdk.interfaces.PhotoTaken;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

public class CameraFragment extends Fragment implements CameraXConfig.Provider {
    private PreviewView viewFinder;
    private Button photoButton, yesButton, noButton;
    private RelativeLayout viewFinderView, previewView;
    private ImageView previewImageView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Activity context;
    private ImageCapture imageCapture;
    private PhotoTaken photoTakenCallback ;
    private Image takenImage;

    public CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment(photoTakenCallback);
        return fragment;
    }

    public CameraFragment(PhotoTaken photoTakenCallback) {
        this.photoTakenCallback = photoTakenCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        viewFinder = view.findViewById(R.id.cameraViewFinder);
        photoButton = view.findViewById(R.id.proCameraSdkTakePhotoButton);
        yesButton = view.findViewById(R.id.yesButton);
        noButton = view.findViewById(R.id.noButton);
        viewFinderView = view.findViewById(R.id.proCameraSdkViewFinderView);
        previewView = view.findViewById(R.id.proCameraSdkPreviewView);
        previewImageView = view.findViewById(R.id.proCamerasdkPhotoPreview);

        viewFinderView.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.GONE);

        cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        imageCapture = new ImageCapture.Builder().build();
        cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }

        }, ContextCompat.getMainExecutor(context));

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previewView.getVisibility() == View.VISIBLE) {
                    photoTakenCallback.onPhotoTaken(takenImage);
                }
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previewView.getVisibility() == View.VISIBLE) {
                    viewFinderView.setVisibility(View.VISIBLE);
                    previewView.setVisibility(View.GONE);
                    previewImageView.setImageDrawable(null);
                }
            }
        });

        return view;
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        preview.setSurfaceProvider(viewFinder.getPreviewSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();


        cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview);
    }

    private void takePhoto() {
        imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()) , new ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeExperimentalUsageError")
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                takenImage = image.getImage();
                showPreview();
                super.onCaptureSuccess(image);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
            }
        });
    }


    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }

    @Override
    public void onAttach(@NonNull Activity context) {
        this.context = context;
        super.onAttach(context);
    }

    private void showPreview() {
        viewFinderView.setVisibility(View.GONE);
        previewView.setVisibility(View.VISIBLE);
        ByteBuffer buffer = takenImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        Bitmap bitmapImage = rotateImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null), 90);
        previewImageView.setImageBitmap(bitmapImage);
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
}
