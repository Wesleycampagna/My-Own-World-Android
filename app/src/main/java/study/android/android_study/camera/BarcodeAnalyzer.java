package study.android.android_study.camera;

import android.media.Image;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;

import org.jetbrains.annotations.NotNull;

import study.android.android_study.listeners.BarcodeListener;

public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {

    private InputImage image;
    private BarcodeListener listener;

    public BarcodeAnalyzer(BarcodeListener listener) {
        this.listener = listener;
    }

    @Override
    public void analyze(@NotNull ImageProxy imageProxy) {
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            listener.onRecognize(image, imageProxy);
        }
    }
}
