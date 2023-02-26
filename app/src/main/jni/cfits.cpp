#include <fitsio.h>
#include "cfits.h"
#include <jni.h>
#include <cstring>
#include "misc.h"

#define SPLIT_WRITE(x) \
	do {	\
		x* src = (x*)data; \
		x** dst = new x*[ch]; \
		for (int i = 0; i < ch; ++i) \
			dst[i] = new x[len]; \
		split(src, dst, len, ch); \
		for (int i = 0; i < ch; ++i) \
		{ \
			fp[2]++; \
			fits_write_pix(fptr, type, fp, len, dst[i], &status); \
		} \
		for (int i = 0; i < ch; ++i) \
			delete[] dst[i]; \
		delete[] dst;	\
	} while(0)

#define MERGE_READ(x) \
	do {	\
		x** src = new x*[chs]; \
		long fp[3] = {1, 1, 0}; \
		for (int i = 0; i < chs; ++i) \
		{ \
			src[i] = new x[len]; \
			fp[2] ++; \
			fits_read_pix(fptr, type, fp, len, &nulval, src[i], &anynul, &status); \
		} \
		x* dst = (x*)outdata; \
		merge(src, dst, len, chs); \
		for (int i = 0; i < chs; ++i) \
			delete[] src[i]; \
		delete[] src;	\
	} while(0)

bool CFits::write(const char* filePath, int width, int height, void* data, int ch, int bitpix)
{
	if (!data)
		return false;

	fitsfile* fptr = nullptr;
	int status = 0;
	fits_create_file(&fptr, filePath, &status);
	if (nullptr == fptr)
		return false;

    long naxes[3] = {width, height, ch};
    fits_create_img(fptr, bitpix, ch > 2 ? 3 : 2, naxes, &status);

    int type = bitpix2type(bitpix);
    long len = width * height;
    long fp[3] = {1, 1, 0};
    if (ch == 2)
        fits_write_pix(fptr, type, fp, len, data, &status);
    else
    {
        switch (bitpix) {
        case SHORT_IMG:
            SPLIT_WRITE(unsigned short);
            break;
        case LONG_IMG:
            SPLIT_WRITE(int);
            break;
        case LONGLONG_IMG:
            SPLIT_WRITE(long long);
            break;
        case FLOAT_IMG:
            SPLIT_WRITE(float);
            break;
        case DOUBLE_IMG:
            SPLIT_WRITE(double);
            break;
        default:
            SPLIT_WRITE(unsigned char);
            break;
        }
    }

	fits_close_file(fptr, &status);
	fits_report_error(stderr, status);
	return status == 0;
}

void* CFits::read(const char* filePath, int* width, int* height, int* ch, int* bitpix)
{
	fitsfile* fptr = nullptr;
	int status = 0, naxis = 0;
    long naxes[3] = {0};

	fits_open_file(&fptr, filePath, READONLY, &status);
	if (nullptr == fptr)
		return nullptr;
    fits_get_img_type(fptr, bitpix, &status);
    fits_get_img_dim(fptr, &naxis, &status);
    fits_get_img_size(fptr, 3, naxes, &status);
    int type = bitpix2type(*bitpix);

    const int w = naxes[0];
    const int h = naxes[1];
    const int chs = naxes[2];
    if (!w || !h)
        return nullptr;

    *width = w;
    *height = h;
    *ch = chs;

    int nulval = 0, anynul = 0;
    int len = w * h;
    void* outdata = nullptr;
    if (chs <= 2)
    {
        outdata = malloc(len * abs(*bitpix / 8));
        fits_read_img(fptr, type, 1, len, &nulval, outdata, &anynul, &status);
    }
    else
    {
        outdata = malloc(len * abs(*bitpix / 8) * chs);
        switch (*bitpix) {
        case SHORT_IMG:
            MERGE_READ(unsigned short);
            break;
        case LONG_IMG:
            MERGE_READ(int);
            break;
        case LONGLONG_IMG:
            MERGE_READ(long long);
            break;
        case FLOAT_IMG:
            MERGE_READ(float);
            break;
        case DOUBLE_IMG:
            MERGE_READ(double);
            break;
        default:
            MERGE_READ(unsigned char);
            break;
        }
    }
    if (status != 0)
    {
        if (outdata)
        {
            free(outdata);
            outdata = nullptr;
        }
    }
    fits_close_file(fptr, &status);
    fits_report_error(stderr, status);

    return outdata;
}

int CFits::type2bitpix(const int& type)
{
    switch (type) {
    case TSHORT:
        return SHORT_IMG;
    case TLONG:
        return LONG_IMG;
    case TLONGLONG:
        return LONGLONG_IMG;
    case TFLOAT:
        return FLOAT_IMG;
    case TDOUBLE:
        return DOUBLE_IMG;
    default:
        return BYTE_IMG;
    }
}

int CFits::bitpix2type(const int& bitpix)
{
    switch (bitpix) {
    case SHORT_IMG:
        return TSHORT;
    case LONG_IMG:
        return TLONG;
    case LONGLONG_IMG:
        return TLONGLONG;
    case FLOAT_IMG:
        return TFLOAT;
    case DOUBLE_IMG:
        return TDOUBLE;
    default:
        return TBYTE;
    }
}

/*
extern "C"
JNIEXPORT jint JNICALL
Java_com_touptek_astroclient_util_TpLib_getFrameWidth(JNIEnv *env, jobject thiz, jstring url) {
    int w = 0, h = 0, chs = 0, bitpix = 0;
    const char* jnamestr = env->GetStringUTFChars(url, NULL);
    void* data = CFits::io().read(jnamestr, &w, &h, &chs, &bitpix);
    if (data)
    {
*/
/*        QImage::Format qfmt;
        if (chs < 3)
            qfmt = bitpix == 8 ? QImage::Format_Grayscale8 : QImage::Format_Grayscale16;
        else if (chs == 3)
            qfmt = QImage::Format_RGB888;
        else
            qfmt = bitpix == 8 ? QImage::Format_RGBX8888 : QImage::Format_RGBX64;
        img = QImage((unsigned char*)data, w, h, qfmt);*//*

    }

    jint length = strlen(reinterpret_cast<const char *const>((unsigned char *) data));
    return length;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_touptek_astroclient_util_TpLib_getFrameBuffer(JNIEnv *env, jobject thiz, jstring url) {
    int w = 0, h = 0, chs = 0, bitpix = 0;
    const char* jnamestr = env->GetStringUTFChars(url, NULL);
    void* data = CFits::io().read(jnamestr, &w, &h, &chs, &bitpix);
    if (data)
    {
*/
/*        QImage::Format qfmt;
        if (chs < 3)
            qfmt = bitpix == 8 ? QImage::Format_Grayscale8 : QImage::Format_Grayscale16;
        else if (chs == 3)
            qfmt = QImage::Format_RGB888;
        else
            qfmt = bitpix == 8 ? QImage::Format_RGBX8888 : QImage::Format_RGBX64;
        img = QImage((unsigned char*)data, w, h, qfmt);*//*

    }

    jint length = w * h;
    jbyteArray array = env->NewByteArray(length);
    env->SetByteArrayRegion(array, 0, length,
                            reinterpret_cast<const jbyte *>(data));
    return array;
}*/
