#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

using namespace cv;
using namespace std;

void colorCartoonFilter(Mat& src, Mat& dst, int edgeThickness, int edgeThreshold);
void grayCartoonFilter(Mat& src, Mat& dst, int edgeThickness, int edgeThreshold);
void pencilSketchFilter(Mat& src, Mat& dst, int blurRadius, int contrast);
void oilPaintFilter(Mat& src, Mat& dst, int radius, int levels);
void waterColorFilter(Mat& src, Mat& dst, int spatialRadius, int colorRadius, int maxLevels, int scaleFactor);
void colorDodgeBlend(Mat& src, Mat& blend, Mat& dst);
void quantize(Mat& src, Mat& dst);
void quantize1(Mat& src, Mat& dst);

class SketchFilter
{
	public:
		static SketchFilter* getInstance();	
		void setSketchTextures(Mat& dark_t, Mat& medium_t, Mat& light_t);
		void applyGraySketch(Mat& src, Mat& dst);
		void applyColorSketch(Mat& src, Mat& dst);
	private:
		SketchFilter();
		
		static SketchFilter* instance;
		Mat textures[3];
		uchar q_steps[4];
};