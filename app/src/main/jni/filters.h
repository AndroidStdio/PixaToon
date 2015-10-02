#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace cv;
using namespace std;

void colorCartoonFilter(Mat& src, Mat& dst, int edgeThickness, int edgeThreshold);
void quantize(Mat& src, Mat& dst);