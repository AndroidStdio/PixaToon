#include "filters.h"

#define INPUT_MAX 100
#define EDGE_THICK_MIN 1
#define EDGE_THICK_MAX 401
#define EDGE_THRESH_MIN 1
#define EDGE_THRESH_MAX 15

void colorCartoonFilter(Mat& src, Mat& dst, int edgeThickness, int edgeThreshold) {
	// denormalize params
	edgeThickness *= (EDGE_THICK_MAX - EDGE_THICK_MIN)/INPUT_MAX + EDGE_THICK_MIN;
	if(edgeThickness%2 == 0) edgeThickness++;
	edgeThreshold *= (EDGE_THRESH_MAX - EDGE_THRESH_MIN)/INPUT_MAX + EDGE_THRESH_MIN;
	
    Mat src_gray, quantized, edges;
    // Denoise image
    GaussianBlur(src, src, Size(5,5), 0);
    // Get src image grayscale
    cvtColor(src, src_gray, CV_RGBA2GRAY);
    // Quantize gray img to get discrete shades
    quantize(src_gray, quantized);
    // superimpose gray shades on color src img
    cvtColor(quantized, dst, CV_GRAY2RGBA);
    subtract(src, ~dst, dst);
    // get illumination-resistant edges by adaptive thresholding
    adaptiveThreshold(src_gray, src_gray, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, edgeThickness, edgeThreshold);
    cvtColor(src_gray, edges, CV_GRAY2RGBA);
    // superimpose edges on shaded src img
    subtract(dst, ~edges, dst);
}

void quantize(Mat& src, Mat& dst) {
    uchar steps[4] = {50, 100, 150, 255};
    //uchar step_val[4] = {10, 50, 100, 255};
    uchar step_val[4] = {200, 210, 220, 255};

    uchar buffer[256];
    int j=0;
    for(int i=0; i!=256; ++i) {
        if(i > steps[j])
            j++;
        buffer[i] = step_val[j];
    } 
    Mat table(1, 256, CV_8U, buffer, sizeof(buffer));
    LUT(src, table, dst);
}