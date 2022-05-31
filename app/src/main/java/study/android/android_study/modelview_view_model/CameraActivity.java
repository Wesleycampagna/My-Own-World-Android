package study.android.android_study.modelview_view_model;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import study.android.android_study.R;
import study.android.android_study.camera.BarcodeAnalyzer;
import study.android.android_study.databinding.CameraViewBinding;
import study.android.android_study.utils.Logger;

import static androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST;

public class CameraActivity extends AppCompatActivity {

    private ImageCapture imageCapture;
    private static final String TAG = "debug_wesley";
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private CameraViewBinding binding;
    private File outputDirectory;
    private BarcodeScanner scanner;
    private ImageAnalysis barcodeAnalyzer;
    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.camera_view);

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // Set up the listener for take photo button
        binding.cameraCaptureButton.setOnClickListener(v -> takePhoto());
        binding.textSuggest.setText("no começo!");
        outputDirectory = getOutputDirectory();
        cameraExecutor = Executors.newSingleThreadExecutor();

        setupScanner();
    }

    private void setupScanner() {
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_CODE_128)
                        .build();

        // Add client Barcode Reader
        startBarcodeProcessorClient();
    }

    private void takePhoto() {
        binding.textSuggest.setText("no clique!");
        Logger.debug("cliquei para tirar a foto! ");

        // Get a stable reference of the modifiable image capture use case
        if (imageCapture == null) return;

        // Create time-stamped output file to hold the image
//        String name = "image" + new SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault())
//                .format(System.currentTimeMillis() + ".jpg");

        String name = "aaaaaa.jpg";

        Logger.debug(name);
        File photoFile = new File (
            outputDirectory,
                name);

        // Create output options object which contains file + metadata
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture (
                outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = Uri.fromFile(photoFile);
                        String msg = String.format("Photo capture succeeded: %s", savedUri);
                        Toast.makeText(CameraActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Logger.debug(msg);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Logger.error("Photo capture failed: %s", exception);
                    }
                }
            );
    }

    private void onReadBarcode(InputImage image, ImageProxy imageProxy) {
        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(barcodes -> {

                    // Task completed successfully
                    for (Barcode barcode: barcodes) {
                        Rect bounds = barcode.getBoundingBox();
                        Point[] corners = barcode.getCornerPoints();

                        String rawValue = barcode.getRawValue();

                        int valueType = barcode.getValueType();

                        Logger.debug(rawValue);
                        Toast.makeText(this, rawValue, Toast.LENGTH_SHORT).show();

//                      See API reference for complete list of supported types
                        switch (valueType) {
                            case Barcode.TYPE_WIFI:
                                String ssid = barcode.getWifi().getSsid();
                                String password = barcode.getWifi().getPassword();
                                int type = barcode.getWifi().getEncryptionType();
                                Logger.debug(String.format("ssid: %s | password: %s | type: %s", ssid, password, type));
                                break;
                            case Barcode.TYPE_URL:
                                String title = barcode.getUrl().getTitle();
                                String url = barcode.getUrl().getUrl();
                                Logger.debug(String.format("title: %s | url: %s", title, url));
                                break;
                        }
                    }
                    imageProxy.close();
                })
                .addOnFailureListener(e -> {
//                    Logger.error(e.getMessage());
                    String x = "a";
                });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            ProcessCameraProvider cameraProvider;
            try {
                cameraProvider = cameraProviderFuture.get();

                // Preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                barcodeAnalyzer = new ImageAnalysis.Builder()
                        .setImageQueueDepth(STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                barcodeAnalyzer.setAnalyzer(cameraExecutor, new BarcodeAnalyzer(this::onReadBarcode));

                Logger.debug("quantity: " + barcodeAnalyzer.getImageQueueDepth());

                // Select back camera as a default
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                Camera camera = cameraProvider.bindToLifecycle (
                        this, cameraSelector, preview, imageCapture, barcodeAnalyzer);

                // Set time to autoFocus
                activateFocusAtEvery(2, camera);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
        Logger.debug("Iniciei a camera! ");
    }

    private void startBarcodeProcessorClient() {
        scanner = BarcodeScanning.getClient();
    }

    private void activateFocusAtEvery(int timeInSeconds, Camera camera) {
        MeteringPointFactory factory  = new SurfaceOrientedMeteringPointFactory ((float) binding.viewFinder.getWidth(), (float) binding.viewFinder.getWidth());
        float centerWidth = (float) binding.viewFinder.getWidth() / 2;
        float centerHeight = (float) binding.viewFinder.getWidth() / 2;

        //create a point on the center of the view
        MeteringPoint autoFocusPoint = factory.createPoint(centerWidth, centerHeight);
        camera.getCameraControl().startFocusAndMetering(
                new FocusMeteringAction.Builder(
                    autoFocusPoint,
                    FocusMeteringAction.FLAG_AF
                )
                .setAutoCancelDuration(timeInSeconds, TimeUnit.SECONDS)
                .build());

    }

    private boolean allPermissionsGranted()  {
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private File getOutputDirectory() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Logger.debug(path.getAbsolutePath());
        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == REQUEST_CODE_PERMISSIONS) {
                if (allPermissionsGranted()) {
                    startCamera();
                } else {
                    Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
    }
}