package ceng.murathas.facerectest;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {


    private JavaCameraView javaCameraView;
    private Mat frame, mRgbaF, mRgbaT, imgGray, imgCanny;
    private String filenameFaceCascade;
    private String filenameEyesCascade;
    private CascadeClassifier faceCascade;
    private CascadeClassifier eyesCascade;
    private Mat image_roi;

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {

                case BaseLoaderCallback.SUCCESS:
                    javaCameraView.enableView();
                    break;

                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.train_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog settingsDialog = new Dialog(getApplicationContext());
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout,null));
                settingsDialog.show();
            }
        });

        javaCameraView = (JavaCameraView) findViewById(R.id.cameraview);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        filenameFaceCascade = "/storage/emulated/0/data/haarcascade_frontalface_alt.xml";
        filenameEyesCascade = "/storage/emulated/0/data//haarcascade_eye_tree_eyeglasses.xml";

        faceCascade = new CascadeClassifier();
        eyesCascade = new CascadeClassifier();

        if (!faceCascade.load(filenameFaceCascade)) {
            Log.d("TAG", "--(!)Error loading face cascade: " + filenameFaceCascade);
        }

        if (!eyesCascade.load(filenameEyesCascade)) {
            Log.d("TAG", "--(!)Error loading eyes cascade: " + filenameEyesCascade);
        }

        Log.d("TAG", stringFromJNI());


        if (OpenCVLoader.initDebug()) {
            Log.e("TAG", "Success");
        } else {
            Log.e("TAG", "fail");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.e("TAG", "Resume Success");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        } else {
            Log.e("TAG", "Resume fail");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, baseLoaderCallback);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        frame = new Mat(height, width, CvType.CV_8UC3);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        imgGray = new Mat(width, width, CvType.CV_8UC1);
        imgCanny = new Mat(width, width, CvType.CV_8UC1);


    }

    @Override
    public void onCameraViewStopped() {
        frame.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        frame = inputFrame.rgba();

        //rotate 90
        Core.transpose(frame, mRgbaT);
        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0, 0, 0);
        Core.flip(mRgbaF, frame, 1);

        Mat frameGray = inputFrame.gray();
        Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(frameGray, frameGray);
        // -- Detect faces
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(frameGray, faces);



        List<Rect> listOfFaces = faces.toList();
        Rect rectCrop=null;
        for (Rect face : listOfFaces) {
            Imgproc.rectangle(frame, new Point(face.x, face.y), new Point(face.x + face.width, face.y + face.height), new Scalar(0, 255, 0, 255), 3);
            rectCrop = new Rect(face.x, face.y, face.width, face.height);
            image_roi = new Mat(frame,rectCrop);
            Log.d("detect","detect new face"+image_roi.toString());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CustomDialogClass cdd = new CustomDialogClass(MainActivity.this,image_roi);
                    cdd.show();
                }
            });

            javaCameraView.enableView();
        }



        return frame;
    }


    public native String stringFromJNI();

}
