#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <iostream>

using namespace cv;
using namespace std;

void colorCartoonFilter(Mat& src, Mat& dst, int edgeThickness, int edgeThreshold);
void grayCartoonFilter(Mat& src, Mat& dst, int edgeThickness, int edgeThreshold);
void pencilSketch2Filter(Mat& src, Mat& dst, int blurRadius, int contrast);
void oilPaintFilter(Mat& src, Mat& dst, int radius, int levels);
void waterColorFilter(Mat& src, Mat& dst, int spatialRadius, int colorRadius, int maxLevels, int scaleFactor);
void colorDodgeBlend(Mat& src, Mat& blend, Mat& dst);
void getQuantizeSteps(Mat& src, float* prob_arr, int num_steps);
void quantize(Mat& src, Mat& dst);
void quantize1(Mat& src, Mat& dst);
void kmeans(Mat& src, Mat& dst, int clusters);

class SketchFilter
{
	public:
		static SketchFilter* getInstance();	
		void setSketchTexture(Mat& texture);
		void applyPencilSketch(Mat& src, Mat& dst, int sketchBlend, int contrast);
		void applyColorSketch(Mat& src, Mat& dst, int sketchBlend, int contrast);
	private:
		SketchFilter();
		
		static SketchFilter* instance;
		Mat texture;
};