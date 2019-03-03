package com.polka.rentplace.MLVisioin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.polka.rentplace.ConfirmFinalOrderActivity;
import com.polka.rentplace.R;
import com.polka.rentplace.model.Face;

import java.util.Arrays;
import java.util.List;

import static android.content.Context.CAMERA_SERVICE;
import static android.hardware.camera2.CameraCharacteristics.LENS_FACING;
import static android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW;
import static android.hardware.camera2.CameraMetadata.CONTROL_AF_MODE_AUTO;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_FRONT;
import static java.lang.System.currentTimeMillis;

public class CameraFragment extends Fragment {

    public static final String TAG = CameraFragment.class.getSimpleName();
    public int myuit = 0;
    String totalAmount;

    private static final float FACE_IMAGE_SCALE = 0.3f; // во сколько раз нужно уменьшить картинку с камеры для детектора лиц

    private TextureView mTextureView; // для превью с камеры
    private FaceView mFaceView; // для контуров лиц
    //private TextView mTextView; // для результата Image Labeling

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;


    //private FirebaseVisionLabelDetector mFirebaseLabelDetector; // детектор предметов
    private FirebaseVisionFaceDetector mFirebaseFaceDetector; // детектор лиц

    public CameraFragment() {
        // stub
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextureView = view.findViewById(R.id.texture_view);
        mFaceView = view.findViewById(R.id.face_view);
        //mTextView = view.findViewById(R.id.text_view);
    }

    @Override
    public void onResume() {
        super.onResume();

        //initializeFirebaseLabelDetector();
        initializeFirebaseFaceDetector();

        if (mTextureView.isAvailable()) {
            checkPermission();
        } else {
            mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    checkPermission();
                }
                @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                }
                @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return false;
                }
                @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });
        }
    }

    /*private void initializeFirebaseLabelDetector() {
        // создаём настройки
        final FirebaseVisionLabelDetectorOptions options =
                new FirebaseVisionLabelDetectorOptions.Builder()
                        .setConfidenceThreshold(0.8f)
                        .build();
        // создаём FirebaseVisionLabelDetector
        mFirebaseLabelDetector = FirebaseVision.getInstance()
                .getVisionLabelDetector(options);
    }*/

    private void initializeFirebaseFaceDetector() {
        // настраиваем детектор лиц
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
//                        .setMinFaceSize(0.1f)
//                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                        .build();
        // создаём детектор лиц
        mFirebaseFaceDetector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);
    }

    private void checkPermission() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        startThread();
                        openCamera();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        // TODO show message
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        // TODO show message
                    }
                }).check();
    }

    private void startThread() {
        // создаём поток
        mBackgroundThread = new HandlerThread(TAG);
        // стартуем его
        mBackgroundThread.start();
        // создаёми Handler, связываем его с тем потоком, который создали
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        // получаем CameraManager
        final CameraManager cameraManager = (CameraManager) getActivity().getSystemService(CAMERA_SERVICE);
        if (cameraManager != null) { // система может отдать null вместо cameraManager
            try {
                // получаем идентификаторы камеры
                final String[] cameraIds = cameraManager.getCameraIdList();
                // перебираем их
                Log.v(TAG, "cameraIds=" + Arrays.toString(cameraIds));
                String frontCameraId = null;
                for (String cameraId : cameraIds) {
                    // получаем характеристики камеры
                    final CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                    if (characteristics.get(LENS_FACING) == LENS_FACING_FRONT) {
                        frontCameraId = cameraId;
                        break;
                    }
                }

                // открываем камеру (если нашли заднюю)
                if (frontCameraId != null) {
                    cameraManager.openCamera(frontCameraId, new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice camera) {
                            captureCamera(camera);
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice camera) {
                            camera.close();
                        }

                        @Override
                        public void onError(@NonNull CameraDevice camera, int error) {
                            Log.e(TAG, "openCamera: error=" + error);
                        }
                    }, mBackgroundHandler);
                }
            } catch (CameraAccessException e) {
                Log.e(TAG, "openCamera", e);
            }
        }
    }

    private void captureCamera( final CameraDevice camera) {
        // получаем Surface из mTextureView
        // Surface - поверхность для отображения разных данных (просто видео, превью из камеры и так далее)
        final Surface surface = new Surface(mTextureView.getSurfaceTexture());
        // createCaptureSession принимает только список из Surface((, поэтому нужно создавать список из одного элемента
        final List<Surface> surfaces = Arrays.asList(surface);
        try {
            // создаём сессию захвата картинки с камеры
            // сессия примерно = подключение
            camera.createCaptureSession(surfaces
                    , new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            // когда сессия настроена - запускаем превью с камеры
                            startRecord(camera, session, surface);
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            // TODO показать ошибку
                        }
                    }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "captureCamera", e);
        }
    }

    private void startRecord(CameraDevice camera, CameraCaptureSession session, Surface surface) {
        try {
            // ок, сессия настроена, теперь создаём запрос картинки с камеры
            final CaptureRequest.Builder requestBuilder = camera.createCaptureRequest(TEMPLATE_PREVIEW);
            // картинка должна передаваться в Surface (который с mTextureView)
            requestBuilder.addTarget(surface);
            // автофокус - авто
            requestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CONTROL_AF_MODE_AUTO);
            // setRepeatingRequest означает, что видео записываем, а не фото снимаем
            session.setRepeatingRequest(requestBuilder.build()
                    , new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                            //startFirebaseLabelDetecting();
                            startFirebaseFaceDetecting();
                        }
                    }
                    , mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "captureCamera", e);
        }
    }

    private boolean mFirebaseLabelDetectingStarted = false;
    private boolean mFirebaseFaceDetectingStarted = false;

    /*private void startFirebaseLabelDetecting() {
        // если ещё не стартовали обработку
        if (!mFirebaseLabelDetectingStarted) {
            // то стартуем её (меняем флаг на true)
            mFirebaseLabelDetectingStarted = true;
            //processFirebaseLabelDetecting();
        }
    }

    /*private void processFirebaseLabelDetecting() {
        final long now = currentTimeMillis(); // запоминаем время старта обработки
        Bitmap bitmap = mTextureView.getBitmap(); // получаем битмап из TextureView
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap); // преобразовываем в нужный формат
        mFirebaseLabelDetector.detectInImage(image) // запускаем детектирование
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionLabel> firebaseLabels) {
                        String result = (currentTimeMillis() - now) + "ms\n"; // в текстовое поле пишем время, которое потратили на запрос
                        final List<String> labels = new ArrayList<>(); // создаём список пустой
                        for (FirebaseVisionLabel firebaseLabel : firebaseLabels) { // смотрим на то, что получили от ML Kit
                            // и пишем название предмета и его confidence
                            result += firebaseLabel.getLabel() + " - " + firebaseLabel.getConfidence() + "\n";
                        }
                        mTextView.setText(result); // отображаем из в TextView через запятую
                        processFirebaseLabelDetecting();
                    }
                });
    }*/

    private void startFirebaseFaceDetecting() {
        // если ещё не стартовали обработку
        if (!mFirebaseFaceDetectingStarted) {
            // то стартуем её (меняем флаг на true)
            mFirebaseFaceDetectingStarted = true;
            processFirebaseFaceDetecting();
        } else if (!mFirebaseFaceDetectingStarted){

            mFirebaseFaceDetectingStarted = false;

        }
    }

    private void processFirebaseFaceDetecting() {
        final long now = currentTimeMillis(); // запоминаем время старта обработки
        Bitmap bitmap = mTextureView.getBitmap(); // получаем битмап из TextureView
        // сжимаем картинку, чтобы обработка проходила быстрее
        bitmap = Bitmap.createScaledBitmap(bitmap
                , (int) (bitmap.getWidth() * FACE_IMAGE_SCALE)
                , (int) (bitmap.getHeight() * FACE_IMAGE_SCALE)
                , true);
        final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap); // преобразовываем в нужный формат
        mFirebaseFaceDetector.detectInImage(image) // запускаем детектирование
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseFaces) {
                        Log.v(TAG, "processFirebaseFaceDetecting: time=" + (currentTimeMillis() - now) + "ms\n");
                        // рисуем контуры лиц
                        mFaceView.showFaces(firebaseFaces, 1 / FACE_IMAGE_SCALE );
                        processFirebaseFaceDetecting();
                        /*if (mFirebaseFaceDetector != null){
                            Intent intent = new Intent(CameraFragment.this.getActivity(),NextActivity.class);
                            startActivity(intent);
                        }else {

                        }*/
                        if (Face.faceResult != 0){

                            Intent intent = new Intent(CameraFragment.this.getActivity(), ConfirmFinalOrderActivity.class);
                            startActivity(intent);
                            Face.faceResult -= 1;
                        } else {

                        }
                    }
                });
    }


    public static class TestActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_test);
        }
    }
}
