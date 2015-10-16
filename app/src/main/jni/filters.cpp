#include "filters.h"

#define INPUT_MAX 100
#define EDGE_THICK_MIN 3
#define EDGE_THICK_MAX 201
#define EDGE_THRESH_MIN 1
#define EDGE_THRESH_MAX 8

/* Color-Cartoon Filter Imaplementation */
void colorCartoonFilter(Mat& src, Mat& dst, int edgeThickness, int edgeThreshold) 
{	
	// denormalize params
	edgeThickness = (edgeThickness*(EDGE_THICK_MAX - EDGE_THICK_MIN))/INPUT_MAX + EDGE_THICK_MIN;
	if(edgeThickness%2 == 0) edgeThickness++;
	edgeThreshold = (edgeThreshold*(EDGE_THRESH_MAX - EDGE_THRESH_MIN))/INPUT_MAX + EDGE_THRESH_MIN;
	
    Mat src_blurred, src_gray, quantized, edges;
    // Denoise image
    GaussianBlur(src, src_blurred, Size(5,5), 0);
    // Get src image grayscale
    cvtColor(src_blurred, src_gray, CV_RGBA2GRAY);
    // Quantize gray img to get discrete shades
    quantize(src_gray, quantized);
	cvtColor(quantized, dst, CV_GRAY2RGBA);
    // superimpose gray shades on color src img
    subtract(src_blurred, ~dst, dst);
    // get illumination-resistant edges by adaptive thresholding
    adaptiveThreshold(src_gray, src_gray, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, edgeThickness, edgeThreshold);
    cvtColor(src_gray, edges, CV_GRAY2RGBA);
    // superimpose edges on shaded src img
    subtract(dst, ~edges, dst);
}

/* Gray-Cartoon Filter Implementation */
void grayCartoonFilter(Mat& src, Mat& dst, int edgeThickness, int edgeThreshold) 
{
	// denormalize params
	edgeThickness = (edgeThickness*(EDGE_THICK_MAX - EDGE_THICK_MIN))/INPUT_MAX + EDGE_THICK_MIN;
	if(edgeThickness%2 == 0) edgeThickness++;
	edgeThreshold = (edgeThreshold*(EDGE_THRESH_MAX - EDGE_THRESH_MIN))/INPUT_MAX + EDGE_THRESH_MIN;
	
    Mat src_blurred, src_gray, quantized, edges;
    // Denoise image
    GaussianBlur(src, src_blurred, Size(5,5), 0);
    // Get src image grayscale
    cvtColor(src_blurred, src_gray, CV_RGBA2GRAY);
    // Quantize gray img to get discrete shades
    quantize1(src_gray, quantized);
    cvtColor(quantized, dst, CV_GRAY2RGBA);
    // get illumination-resistant edges by adaptive thresholding
    adaptiveThreshold(src_gray, src_gray, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, edgeThickness, edgeThreshold);
    cvtColor(src_gray, edges, CV_GRAY2RGBA);
    // superimpose edges on shaded src img
    subtract(dst, ~edges, dst);
}

void quantize(Mat& src, Mat& dst) 
{
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

void quantize1(Mat& src, Mat& dst) 
{
    uchar steps[4] = {50, 100, 150, 255};
    uchar step_val[4] = {10, 50, 100, 255};
    //uchar step_val[4] = {200, 210, 220, 255};

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

SketchFilter* SketchFilter::instance = NULL;
SketchFilter* SketchFilter::getInstance() 
{
	if(instance == NULL)
		instance = new SketchFilter();
	return instance;
}

SketchFilter::SketchFilter()
{	
	q_steps[0] = 50;
	q_steps[1] = 100;
	q_steps[2] = 150;
	q_steps[3] = 255;
}

void SketchFilter::setSketchTextures(Mat& dark_t, Mat& medium_t, Mat& light_t)
{
	textures[0] = dark_t;
	textures[1] = medium_t;
	textures[2] = light_t;
}

void SketchFilter::applyGraySketch(Mat& src, Mat& dst)
{
	Mat src_blurred, src_gray, edges, dst_gray;
	GaussianBlur(src, src_blurred, Size(5,5),0);
	cvtColor(src_blurred, src_gray, CV_RGBA2GRAY);
	
	dst_gray.create(src_gray.size(), src_gray.type());
	for(int row=0; row<src.rows; row++)
		for(int col=0; col<src.cols; col++)
		{
			uchar src_pixel, dst_pixel;
			src_pixel = src_gray.at<uchar>(row,col);
			
            int t_row = row % textures[0].rows;
            int t_col = col % textures[0].cols;

            if(src_pixel <= q_steps[0])
				dst_pixel = textures[0].at<uchar>(t_row, t_col);
			else if(src_pixel <= q_steps[1])
				dst_pixel = textures[1].at<uchar>(t_row, t_col);
			else if(src_pixel <= q_steps[2])
				dst_pixel = textures[2].at<uchar>(t_row, t_col);
			else if(src_pixel <= q_steps[3])
				dst_pixel = 255;
			
			dst_gray.at<uchar>(row,col) = dst_pixel;
		}
	cvtColor(dst_gray, dst, CV_GRAY2RGBA);
}

void SketchFilter::applyColorSketch(Mat& src, Mat& dst)
{
	//todo
}