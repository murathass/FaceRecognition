package ceng.murathas.facerectest;

public class OpenCVNativeClass {
    public native static int convertGray(long matAddrRgba, long matAddrGray);
    public native static void faceDetection(long matAddrRgba);
}
