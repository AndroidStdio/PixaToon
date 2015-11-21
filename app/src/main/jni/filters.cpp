#include "filters.h"

#define INPUT_MAX 100

#define CARTOON_THICK_MIN 3
#define CARTOON_THICK_MAX 201
#define CARTOON_THRESH_MIN 1
#define CARTOON_THRESH_MAX 9

#define SKETCH_BLUR_MIN 3
#define SKETCH_BLUR_MAX 51
#define SKETCH_CONTRAST_MIN 1.0
#define SKETCH_CONTRAST_MAX 10.0 

#define OILPAINT_RADIUS_MIN 1
#define OILPAINT_RADIUS_MAX 10
#define OILPAINT_LEVELS_MIN 5
#define OILPAINT_LEVELS_MAX 30


/* Color-Cartoon Filter Imaplementation */
void colorCartoonFilter(Mat& src, Mat& dst, int edgeThickness, int edgeThreshold) 
{	
	// denormalize params
	edgeThickness = (edgeThickness*(CARTOON_THICK_MAX - CARTOON_THICK_MIN))/INPUT_MAX + CARTOON_THICK_MIN;
	if(edgeThickness%2 == 0) edgeThickness++;
	edgeThreshold = (edgeThreshold*(CARTOON_THRESH_MAX - CARTOON_THRESH_MIN))/INPUT_MAX + CARTOON_THRESH_MIN;
	
    Mat src_blurred, src_gray, quantized, edges;

    // Denoise image
    GaussianBlur(src, src_blurred, Size(5,5), 0);
    // Get src image grayscale
    cvtColor(src_blurred, src_gray, CV_RGBA2GRAY);
    // Quantize gray img to get discrete shades
    quantize(src_gray, quantized);
	cvtColor(quantized, dst, CV_GRAY2RGBA);
    // superimpose gray shades on color src img
    //subtract(src_blurred, ~dst, dst);
    add(0.7*src_blurred,0.7*dst,dst);
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
	edgeThickness = (edgeThickness*(CARTOON_THICK_MAX - CARTOON_THICK_MIN))/INPUT_MAX + CARTOON_THICK_MIN;
	if(edgeThickness%2 == 0) edgeThickness++;
	edgeThreshold = (edgeThreshold*(CARTOON_THRESH_MAX - CARTOON_THRESH_MIN))/INPUT_MAX + CARTOON_THRESH_MIN;
	
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

void getQuantizeSteps(Mat& src, float* prob_arr, int num_steps, uchar* steps) {
    Mat hist;
    int histSize = 256;

    float range[] = { 0, 256 } ;
    const float* histRange = { range };

    calcHist(&src, 1, 0, Mat(), hist, 1, &histSize, &histRange, true, false);

    float sum_hist = 0.0f;
    for(int i=0; i<histSize; i++)
        sum_hist += hist.at<float>(i,0);
    hist = hist/sum_hist;

    float step_prob = 0.0f;
    int step_index = 0;
    for(int i=0; i<histSize; i++) {
        step_prob += hist.at<float>(i,0);
        if(step_prob >= prob_arr[step_index]) {
            steps[step_index++]=i;
            step_prob = 0.0f;
        }
    }
    for(int i=step_index; i<num_steps; i++)
        steps[i] = 255;
}

void quantize(Mat& src, Mat& dst) 
{
    //uchar steps[4] = {50, 100, 150, 255};
    //uchar step_val[4] = {200, 210, 220, 255};
    uchar steps[4];
    float step_prob[] = { 0.1f, 0.2f, 0.2f, 0.5f};
    getQuantizeSteps(src, step_prob, 4, steps);

    uchar step_val[] = {0, 30, 50, 100};

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
    //uchar steps[4] = {50, 100, 150, 255};
    uchar steps[4];
    float step_prob[] = { 0.1f, 0.2f, 0.2f, 0.5f};
    getQuantizeSteps(src, step_prob, 4, steps);

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

void pencilSketchFilter(Mat& src, Mat& dst, int blurRadius, int contrast)
{
	// denormalize params
	blurRadius = (blurRadius*(SKETCH_BLUR_MAX - SKETCH_BLUR_MIN))/INPUT_MAX + SKETCH_BLUR_MIN;
	if(blurRadius%2 == 0) blurRadius++;
	float contrast1 = (float)(contrast*(SKETCH_CONTRAST_MAX - SKETCH_CONTRAST_MIN))/(float)INPUT_MAX + SKETCH_CONTRAST_MIN;
	
	Mat src_gray, dst_gray, blend;
    cvtColor(src, src_gray, CV_RGBA2GRAY);
    GaussianBlur(src_gray, src_gray, Size(3,3), 0);
    GaussianBlur(~src_gray, blend, Size(blurRadius,blurRadius), 0);
    colorDodgeBlend(src_gray, blend, dst_gray);
    dst_gray = ~(contrast1*(~dst_gray));
	cvtColor(dst_gray, dst, CV_GRAY2RGBA);
}

void colorDodgeBlend(Mat& src, Mat& blend, Mat& dst)
{
    dst.create(src.size(), src.type());
    for(int i=0; i< src.rows; i++) 
        for(int j=0; j< src.cols; j++) {
            
            if(src.channels() == 1) {
                int srcPixel, dstPixel, blendPixel;
                srcPixel = (int)src.at<uchar>(i,j);
                blendPixel = (int)blend.at<uchar>(i,j);

                //dstPixel = srcPixel + (uchar)( (255.0 - srcPixel) * (blendPixel/255.0) );
                dstPixel = (blendPixel >= 255)? 255: min(255, (srcPixel*255)/(255-blendPixel));

                dst.at<uchar>(i,j) = (uchar)dstPixel;
            }
        }
}

void oilPaintFilter(Mat& src1, Mat& dst1, int radius, int levels) 
{	
	// denormalize params
	radius = (radius*(OILPAINT_RADIUS_MAX - OILPAINT_RADIUS_MIN))/INPUT_MAX + OILPAINT_RADIUS_MIN;
	levels = (levels*(OILPAINT_LEVELS_MAX - OILPAINT_LEVELS_MIN))/INPUT_MAX + OILPAINT_LEVELS_MIN;
	
    int intensity_hist[levels], total_red[levels], total_green[levels], total_blue[levels];
	Mat src,dst;
	cvtColor(src1, src, CV_RGBA2RGB);
	
	dst.create(src.size(), src.type());
    for(int row=0; row<src.rows; ++row) 
    {
        for(int i=0; i<levels; i++)
            intensity_hist[i] = total_red[i] = total_green[i] = total_blue[i] = 0;

		Vec3b *dst_rptr = dst.ptr<Vec3b>(row);
        int k_row_min = ((row-radius) < 0) ? 0:(row-radius);
        int k_row_max = ((row+radius) >= src.rows) ? (src.rows-1):(row+radius);

        for(int k_row=k_row_min; k_row<=k_row_max; ++k_row) 
        {
            Vec3b *src_rptr = src.ptr<Vec3b>(k_row);
            for(int k_col=0; k_col<radius; ++k_col) 
            {
                Vec3b pix = src_rptr[k_col];
                int red = (int)pix.val[0];
                int green = (int)pix.val[1];
                int blue = (int)pix.val[2];

                int level = ((red+green+blue)*levels)/768;
                intensity_hist[level]++;
                total_red[level] += red;
                total_green[level] += green;
                total_blue[level] += blue;
            }
        }
		
        for(int col=0; col<src.cols; ++col)
        {
            for(int k_row=k_row_min; k_row<=k_row_max; ++k_row) 
            {
                Vec3b *src_rptr = src.ptr<Vec3b>(k_row);

                if(col-radius >= 0) {
                    Vec3b pix = src_rptr[col-radius];
                    int red = (int)pix.val[0];
                    int green = (int)pix.val[1];
                    int blue = (int)pix.val[2];

                    int level = ((red+green+blue)*levels)/768;
                    intensity_hist[level]--;
                    total_red[level] -= red;
                    total_green[level] -= green;
                    total_blue[level] -= blue;
                }

                if(col+radius < src.cols) {
                    Vec3b pix = src_rptr[col+radius];
                    int red = (int)pix.val[0];
                    int green = (int)pix.val[1];
                    int blue = (int)pix.val[2];

                    int level = ((red+green+blue)*levels)/768;
                    intensity_hist[level]++;
                    total_red[level] += red;
                    total_green[level] += green;
                    total_blue[level] += blue;
                }
            }

            Vec3b dst_pix;
            int max_level, max_intensity = 0;
            for(int i=0; i<levels; i++)
                if(intensity_hist[i]>max_intensity) {
                    max_intensity = intensity_hist[i];
                    max_level = i;
                }

            dst_pix.val[0] = (uchar)(total_red[max_level] / intensity_hist[max_level]);
            dst_pix.val[1] = (uchar)(total_green[max_level] / intensity_hist[max_level]);
            dst_pix.val[2] = (uchar)(total_blue[max_level] / intensity_hist[max_level]);
            dst_rptr[col] = dst_pix;
        }
    }
	cvtColor(dst, dst1, CV_RGB2RGBA);
}

void waterColorFilter(Mat& src, Mat& dst, int spatialRadius, int colorRadius, int maxLevels, int scaleFactor) {
	/*
    spatialRadius = spatialRadius/4;
	colorRadius = colorRadius/2;
	maxLevels = maxLevels/10;
	double scaleFactor1 = scaleFactor*0.01;
	
	Mat src_blur, dst1;
    GaussianBlur(src, src_blur, Size(3,3), 0);
	cvtColor(src_blur, src_blur, CV_RGBA2RGB);
	//src_blur = 1.5*src_blur - 50;
	
	if(scaleFactor1 < 0.95) {
		resize(src_blur, src_blur, Size(), scaleFactor1, scaleFactor1, INTER_LINEAR);
		pyrMeanShiftFiltering(src_blur, dst1, spatialRadius, colorRadius, maxLevels);
		resize(dst1, dst1, src.size(), 0, 0, INTER_CUBIC);
	}
	else {
		pyrMeanShiftFiltering(src_blur, dst1, spatialRadius, colorRadius, maxLevels);
	} 
	
	cvtColor(dst1, dst, CV_RGB2RGBA);
    */

    Mat src_blur, dst1;
    GaussianBlur(src, src_blur, Size(3,3), 0);
    cvtColor(src_blur, src_blur, CV_RGBA2RGB);
    
    resize(src_blur, src_blur, Size(), 0.7, 0.7, INTER_LINEAR);
    kmeans(src_blur,dst1,10);
    resize(dst1, dst1, src.size(), 0, 0, INTER_CUBIC);
    cvtColor(dst1, dst, CV_RGB2RGBA);
}

void kmeans(Mat& src, Mat& dst, int clusters) {

    Mat samples(src.rows * src.cols, 3, CV_32F);
    for( int y = 0; y < src.rows; y++ )
        for( int x = 0; x < src.cols; x++ )
          for( int z = 0; z < 3; z++)
            samples.at<float>(y + x*src.rows, z) = src.at<Vec3b>(y,x)[z];

        Mat labels;
        int attempts = 1;
        Mat centers;
        kmeans(samples, clusters, labels, TermCriteria(CV_TERMCRIT_ITER|CV_TERMCRIT_EPS, 10000, 0.0001), attempts, KMEANS_PP_CENTERS, centers );

        dst.create( src.size(), src.type() );
        for( int y = 0; y < src.rows; y++ )
            for( int x = 0; x < src.cols; x++ )
            { 
              int cluster_idx = labels.at<int>(y + x*src.rows,0);
              dst.at<Vec3b>(y,x)[0] = centers.at<float>(cluster_idx, 0);
              dst.at<Vec3b>(y,x)[1] = centers.at<float>(cluster_idx, 1);
              dst.at<Vec3b>(y,x)[2] = centers.at<float>(cluster_idx, 2);
          }
}