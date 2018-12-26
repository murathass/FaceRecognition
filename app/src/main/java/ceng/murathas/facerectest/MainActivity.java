package ceng.murathas.facerectest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.imgproc.Imgproc;

import java.lang.annotation.Native;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {


    private JavaCameraView javaCameraView;
    private Mat mat,mRgbaF,mRgbaT,imgGray,imgCanny;
    private BaseLoaderCallback  baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){

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
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        javaCameraView = (JavaCameraView)findViewById(R.id.cameraview);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

        Log.d("TAG",stringFromJNI());


        if (OpenCVLoader.initDebug()){
            Log.e("TAG","Success");
        }else{
            Log.e("TAG","fail");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView!=null){
            javaCameraView.disableView();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView!=null){
            javaCameraView.disableView();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()){
            Log.e("TAG","Resume Success");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        }else{
            Log.e("TAG","Resume fail");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,baseLoaderCallback);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mat = new Mat(height, width, CvType.CV_8UC3);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        imgGray = new Mat(width, width, CvType.CV_8UC1);
        imgCanny = new Mat(width, width, CvType.CV_8UC1);


    }

    @Override
    public void onCameraViewStopped() {
        mat.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        mat = inputFrame.rgba();

        //rotate 90
        Core.transpose(mat, mRgbaT);
        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
        Core.flip(mRgbaF, mat, 1 );

        /*Imgproc.cvtColor(mat,imgGray, Imgproc.COLOR_RGB2GRAY);
        MatOfDouble mu = new MatOfDouble();
        MatOfDouble stddev = new MatOfDouble();
        Core.meanStdDev(imgGray, mu, stddev);
        Double threshold1 = mu.get(0, 0)[0];
        Double threshold2 = stddev.get(0, 0)[0];
        Log.e("canny","t1:"+threshold1+"  t2:"+threshold2);
        Imgproc.Canny(imgGray,imgCanny,threshold1,threshold2);

        return imgCanny;*/

        OpenCVNativeClass.convertGray(mat.getNativeObjAddr(),imgGray.getNativeObjAddr());
        return imgGray;
    }


    public native String stringFromJNI();

}
