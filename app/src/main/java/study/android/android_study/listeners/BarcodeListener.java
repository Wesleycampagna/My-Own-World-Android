package study.android.android_study.listeners;

import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;

public interface BarcodeListener {

    void onRecognize(InputImage image, ImageProxy imageProxy);
}
