package citymanager.area;

public class ScreenEmulator {

    int screenWidth;
    int screenHeight;
    float aspectRatio;
    float ppi;

    int[][] dimensions;
    String[] dimensionNames;

    float[] aspectRatios;
    String[] aspectRatioNames;

    float[] pixelDensities;

    public ScreenEmulator(){
        setup();
    }

    public void switchScreenDimension(int index){
        this.screenWidth = dimensions[index][0];
        this.screenHeight = dimensions[index][1];
    }
    public void switchAspectRatio(int index){
        this.aspectRatio = aspectRatios[index];
    }

    public int getWidth(){
        return screenWidth;
    }
    public int getHeight(){
        return screenHeight;
    }

    public void setup(){
        //dimensions
        dimensions = new int[3][2];
        dimensionNames = new String[3];
        dimensionNames[0] = "800x480"; //s2
        dimensions[0][0] = 800;
        dimensions[0][1] = 480;
        dimensionNames[1] = "1920x1080"; //one plus x
        dimensions[1][0] = 1920;
        dimensions[1][1] = 1080;
        dimensionNames[2] = "2560x1440"; //s6
        dimensions[2][0] = 2560;
        dimensions[2][1] = 1440;


        //aspect ratios
        aspectRatioNames = new String[8];
        aspectRatios = new float[8];
        aspectRatioNames[0] = "16:9"; //common
        aspectRatios[0] = 16/9;
        aspectRatioNames[1] = "16:10";
        aspectRatios[1] = 16/10;
        aspectRatioNames[2] = "3:2";
        aspectRatios[2] = 3/2;
        aspectRatioNames[3] = "5:3";
        aspectRatios[3] = 5/3;
        aspectRatioNames[4] = "18:9"; //increasing since 2017
        aspectRatios[4] = 18/9;
        aspectRatioNames[5] = "18.5:9";
        aspectRatios[5] = 18.5f/9;
        aspectRatioNames[6] = "19.5:9";
        aspectRatios[6] = 19.5f/9;
        aspectRatioNames[7] = "2:1";
        aspectRatios[7] = 2/1;

        //pixelDensities
        pixelDensities = new float[9];
        pixelDensities[0] = 160;
        pixelDensities[1] = 240;
        pixelDensities[2] = 320;
        pixelDensities[3] = 480;
        pixelDensities[4] = 640;
        pixelDensities[5] = 218; //galaxy s2
        pixelDensities[6] = 441; //one plus X
        pixelDensities[7] = 577; //galaxy s6
        pixelDensities[8] = 807; //sony 4k phone
    }
}
