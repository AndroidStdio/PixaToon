#include "filters.h"

using namespace std;
using namespace cv;

// Apply cartoon filter
void cartoonFilter(Mat& img)
{
	Mat grad, grad_rgba, gray;

	cvtColor(img, gray, CV_RGBA2GRAY);
	gradient(gray, grad);
	cvtColor(grad, grad_rgba, CV_GRAY2RGBA);
	subtract(img, grad_rgba, img);
}

// Gradient 
void gradient(Mat& src, Mat& dst)
{
	 /// Generate grad_x and grad_y
    Mat grad_x, grad_y;

    int scale = 1;
    int delta = 0;
    int ddepth = CV_16S;

    Sobel( src, grad_x, ddepth, 1, 0, 3, scale, delta, BORDER_DEFAULT );
    convertScaleAbs( grad_x, grad_x );

    /// Gradient Y
    Sobel( src, grad_y, ddepth, 0, 1, 3, scale, delta, BORDER_DEFAULT );
    convertScaleAbs( grad_y, grad_y );

    /// Total Gradient (approximate)
    addWeighted( grad_x, 0.5, grad_y, 0.5, 0, dst );
}