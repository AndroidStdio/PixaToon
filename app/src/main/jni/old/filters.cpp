#include "filters.h"

// Apply cartoon filter
void cartoonFilter(Mat& img)
{
    Mat src_gray, quantized, edges, dst;
    GaussianBlur(img, img, Size(5,5), 0);
    cvtColor(img, src_gray, CV_RGBA2GRAY);
    quantize(src_gray, quantized);
    
    Laplacian(src_gray, edges, 0, 3);
    //gradient(src_gray, edges);
    threshold(edges, edges, 30, 255, CV_THRESH_BINARY);

    int dilation_size = 3;
    Mat element = getStructuringElement( MORPH_ELLIPSE,
        Size( 2*dilation_size + 1, 2*dilation_size+1 ), 
        Point( dilation_size, dilation_size ) );
    dilate(edges, edges , element);

    subtract(quantized,edges,quantized);  
    cvtColor(quantized, dst, CV_GRAY2RGBA);
    addWeighted( img, 0.5, dst, 0.5, 0, img );
}

// Apply cartoon filter
void cartoon2Filter(Mat& img)
{
    Mat img_gray, edges;
    GaussianBlur(img, img, Size(3,3), 0);
    cvtColor(img, img_gray, CV_RGBA2GRAY);

    adaptiveThreshold(img_gray, img_gray,255,CV_ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY,51,5);
    cvtColor(img_gray, edges, CV_GRAY2RGBA);
    subtract(img, ~edges, img);
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

void quantize(Mat& src, Mat& dst)
{
    if(src.data != dst.data){
        dst.create(src.size(), src.type());
    }

    uchar steps[5] = {50, 100, 150, 255};
    uchar step_val[5] = {0, 70, 120, 255};

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