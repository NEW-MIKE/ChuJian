#include "toup.h"
#include <fitsio.h>
#include <iostream>
#include "misc.h"
#include "camera.h"
#include "cfits.h"
#include <math.h>

jobject gObject = nullptr;
JavaVM *gLocalVm;
void *pictureData = nullptr;
int gW = 0, gH = 0, gCh = 0, gBitpix = 0;
bool isRecalculate = true;
int histLow = 0;
int histHigh = 255;
int initSize = 0;
unsigned histData[256][4];

unsigned short *pictureLut = nullptr;

static void CameraEventCallback(unsigned nEvent, void *pCallbackCtx, EventExtra *pExtra) {
    JNIEnv *env = nullptr;
    bool isAttached = false;
    if (gLocalVm->GetEnv((void **) &env, JNI_VERSION_1_2) < 0) {
        if (gLocalVm->AttachCurrentThread(&env, nullptr) < 0)
            return;
        isAttached = true;
    }
    jclass lds = env->GetObjectClass(gObject);
    switch (nEvent) {
        case CAMERA_EVENT_IMAGE: {
            LOGE("CAMERA_EVENT_IMAGE");
            jmethodID imgCallBack = env->GetMethodID(lds, "ImageCallBack", "(II)V");
            if (imgCallBack != nullptr) {
                env->CallVoidMethod(gObject, imgCallBack, pExtra->width, pExtra->height);
            } else {
            }
            break;
        }
        case CAMERA_EVENT_ERROR: {
            LOGE("CAMERA_EVENT_ERROR");
            jmethodID errorCallBack = env->GetMethodID(lds, "ErrorCallBack", "(I)V");
            if (errorCallBack != nullptr)
                env->CallVoidMethod(gObject, errorCallBack, 0);
            break;
        }
        default:
            break;
    }
    env->DeleteLocalRef(lds);

    if (isAttached)
        gLocalVm->DetachCurrentThread();
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGE("%s",__func__ );
    gLocalVm = vm;
    return JNI_VERSION_1_6;
}

void JNI_OnUnload(JavaVM *vm, void *reserved) {
    LOGE("%s",__func__ );
    bool isAttached = false;
    JNIEnv *env = nullptr;
    if (gLocalVm->GetEnv((void **) &env, JNI_VERSION_1_2) < 0) {
        if (gLocalVm->AttachCurrentThread(&env, nullptr) < 0)
            return;
        isAttached = true;
    }
    env->DeleteGlobalRef(gObject);
    if (isAttached)
        gLocalVm->DetachCurrentThread();
}

void *data2rgb888(void *data, int imgW, int imgH, int bitpix, int channel) {
    LOGE("%s",__func__ );
    if (!data || !imgW || !imgH)
        return nullptr;
    if (8 != bitpix && 16 != bitpix)
        return nullptr;
    if (channel != 3 && channel != 1 && channel != 0)
        return nullptr;

    void *dstData = malloc(imgH * TDIBWIDTHBYTES(24 * imgW));
    const unsigned short MAXVALUE = bitpix == 8 ? 255 : pow(2, bitpix) - 1;
    if (channel < 3) {
        unsigned char dstvalue;
        for (size_t j = 0; j < imgH; ++j) {
            for (size_t i = 0; i < imgW; ++i) {
                const size_t curPos = j * imgW + i;
                dstvalue = static_cast<char>(((unsigned short *) data)[curPos] * 255 / MAXVALUE);
                ((unsigned char *) dstData)[3 * curPos] = dstvalue;
                ((unsigned char *) dstData)[3 * curPos + 1] = dstvalue;
                ((unsigned char *) dstData)[3 * curPos + 2] = dstvalue;
            }
        }
    } else {
        for (size_t j = 0; j < imgH; ++j) {
            for (size_t i = 0; i < imgW; ++i) {
                unsigned short *tmp = (unsigned short *) data;
                const size_t curPos = j * imgW * channel + i;
                ((unsigned char *) dstData)[3 * curPos] = static_cast<char>(tmp[curPos] * 255 /
                                                                            MAXVALUE);
                ((unsigned char *) dstData)[3 * curPos + 1] = static_cast<char>(tmp[curPos + 1] *
                                                                                255 / MAXVALUE);
                ((unsigned char *) dstData)[3 * curPos + 2] = static_cast<char>(tmp[curPos + 2] *
                                                                                255 / MAXVALUE);
            }
        }
    }
    return dstData;
}

bool calcHistogram(const void *data, const int &width, const int &height, const int &bitpix,
                   const int &ch, unsigned hist[][4]) {
    LOGE("%s",__func__ );
    if (!data || !width || !height)
        return false;
    if (bitpix != 8 && bitpix != 16)
        return false;
    if (ch != 0 && ch != 1 && ch != 3)
        return false;
    const int bitLen = pow(2, bitpix);
    const int dataLen = width * height * (ch == 3 ? 3 : 1);
    memset(hist, 0, 4 * bitLen * sizeof(unsigned));
    if (ch == 3) {
        for (int i = 0; i < dataLen; i += 3) {
            const int r = static_cast<int>(bitpix == 8 ? ((unsigned char *) data)[i]
                                                       : ((unsigned short *) data)[i]);
            const int g = static_cast<int>(bitpix == 8 ? ((unsigned char *) data)[i + 1]
                                                       : ((unsigned short *) data)[i + 1]);
            const int b = static_cast<int>(bitpix == 8 ? ((unsigned char *) data)[i + 2]
                                                       : ((unsigned short *) data)[i + 2]);
            const int gray = 0.299 * r + 0.587 * g + 0.114 * b;
            hist[r][0]++;
            hist[g][1]++;
            hist[b][2]++;
            hist[gray][3]++;
        }
    } else {
        for (int i = 0; i < dataLen; ++i) {
            const int v = static_cast<int>(bitpix == 8 ? ((unsigned char *) data)[i]
                                                       : ((unsigned short *) data)[i]);
            hist[v][0]++;
            hist[v][1]++;
            hist[v][2]++;
            hist[v][3]++;
        }
    }
    return true;
}

long max(long i, long j) {
    if (i > j) {
        return i;
    } else {
        return j;
    }
}


bool calcLut(const int &low, const int &high, const int &bitpix, void *lut) {
    LOGE("%s",__func__ );
    if (low >= high || !lut)
        return false;
    if (bitpix != 8 && bitpix != 16)
        return false;
    const int bitLen = pow(2, bitpix);
    for (int i = 0; i < bitLen; ++i) {
        long long x = max(0, i - low) * (bitLen - 1) / (high - low);

        if (x > bitLen - 1)
            x = bitLen - 1;
        else if (x < 0)
            x = 0;
        if (bitpix == 8)
            ((unsigned char *) lut)[i] = static_cast<unsigned char>(x);
        else {
            ((unsigned short *) lut)[i] = static_cast<unsigned short>(x);
        }
    }
    return true;
}

bool applyLut(void *data, const int &width, const int &height, const int &bitpix, const int &ch,
              void *lut) {
    LOGE("%s",__func__ );
    if (!data || !width || !height)
        return false;
    if (bitpix != 8 && bitpix != 16)
        return false;
    if (ch != 0 && ch != 1 && ch != 3)
        return false;
    const int dataLen = width * height * (ch == 3 ? 3 : 1);
    for (int i = 0; i < dataLen; ++i) {
        const int v = static_cast<int>(bitpix == 8 ? ((unsigned char *) data)[i]
                                                   : ((unsigned short *) data)[i]);
        if (bitpix == 8) {
            ((unsigned char *) data)[i] = ((unsigned char *) lut)[v];
        }
        else {
            ((unsigned short *) data)[i] = ((unsigned short *) lut)[v];
        }
    }
    return true;
}

extern "C" {
JNIEXPORT void JNICALL
Java_com_glide_chujian_util_TpLib_fini(JNIEnv *env, jobject instance) {
    LOGE("%s",__func__ );
    CameraManager *pCamManger = CameraManager::getInstance();
    if (pCamManger) {
        delete pCamManger;
        pCamManger = nullptr;
    }
}

JNIEXPORT void JNICALL
Java_com_glide_chujian_util_TpLib_updateFrame(JNIEnv *env, jobject obj,
                                                    jobject directBuffer,jint size) {
    LOGE("%s",__func__ );
    LOGE("worktogether guide");
    void *buffer = env->GetDirectBufferAddress(directBuffer);
    CameraManager *pCamManger = CameraManager::getInstance();
    if (pCamManger) {
        Frame *frame = pCamManger->getFrame();
        if (frame && frame->m_pFrameData) {
            if (buffer) {
                LOGE("updateFrame size is %d", (unsigned int) frame->getSize());
                memcpy(buffer, frame->m_pFrameData, size);
            }
        }
    }
}


JNIEXPORT jintArray JNICALL
Java_com_glide_chujian_util_TpLib_loadNativeFitsPicture(JNIEnv *env, jobject thiz,
                                                              jstring url, jint new_pic_url) {
    LOGE("%s",__func__ );
    if (new_pic_url == 1) {
        if(pictureData){
            free(pictureData);
        }
        const char *jnamestr = env->GetStringUTFChars(url, JNI_FALSE);
        pictureData = CFits::io().read(jnamestr, &gW, &gH, &gCh, &gBitpix);
        env->ReleaseStringUTFChars(url, jnamestr);
    }
    jintArray size = env->NewIntArray(2);
    if (pictureData) {
        jint values[2];
        values[0] = gW;
        values[1] = gH;
        env->SetIntArrayRegion(size, (jsize) 0, (jsize) 2, values);
    }
    return size;
}

JNIEXPORT jintArray JNICALL
Java_com_glide_chujian_util_TpLib_updatePictureFrame(JNIEnv *env, jobject obj,
                                                           jobject directBuffer, jstring url,jint sizej) {
    LOGE("%s",__func__ );
    const int size = pow(2, gBitpix);
    jintArray histHighData = env->NewIntArray(size);
    if (pictureData) {
        LOGE("图片还在的%d", size);
        if (initSize != size) {
            isRecalculate = true;
            histLow = 0;
            histHigh = size - 1;
            initSize = size;
            if (pictureLut) {
                pictureLut = new unsigned short[size];
            } else {
                free(pictureLut);
                pictureLut = new unsigned short[size];
            }
        }

        int datasize;
        if (gCh <= 2)
        {
            datasize = gH * gW * abs(gBitpix / 8);
        }
        else {
            datasize = gH * gW * abs(gBitpix / 8) * gCh;
        }
        unsigned char *data = new unsigned char[datasize];
        if (data) {
            memcpy(data, pictureData, datasize);
            unsigned histPicDataData[size][4];
            if (calcHistogram(data, gW, gH, gBitpix, gCh, histPicDataData)) // 获取直方图
            {
                if (isRecalculate) // LUT计算条件：LUT没有被计算过 或者 low/high发生变化，不需要反复计算
                {
                    isRecalculate = !calcLut(histLow, histHigh, gBitpix, pictureLut);
                    LOGE("histHigh %d",histHigh);
                    LOGE("histLow %d",histLow);
                }
                applyLut(data, gW, gH, gBitpix, gCh, pictureLut);
                void *rgb888data = data2rgb888(data, gW, gH, gBitpix, gCh);
                if (rgb888data) {
                    void *buffer = env->GetDirectBufferAddress(directBuffer);
                    if (buffer) {
                        memcpy(buffer, rgb888data, sizej);
                       jint databack[size];
                        for (int i = 0; i < size; ++i) {
                            databack[i] = histPicDataData[i][3];
                        }
                        env->SetIntArrayRegion(histHighData, (jsize) 0, (jsize) size, databack);
                    }
                    free(rgb888data);
                    rgb888data = nullptr;
                }
            }
            delete[] data;
        }
    }
    return histHighData;
}

JNIEXPORT jintArray JNICALL
Java_com_glide_chujian_util_TpLib_updateVideoFrame(JNIEnv *env, jobject obj,
                                                         jobject directBuffer, jint size) {
    LOGE("%s",__func__ );
    LOGE("worktogether main");
    static unsigned char lut[256];
    void *buffer = env->GetDirectBufferAddress(directBuffer);
    CameraManager *pCamManger = CameraManager::getInstance();

    jintArray histHighData = env->NewIntArray(256);
    if (initSize != 256) {
        isRecalculate = true;
        histLow = 0;
        histHigh = 256;
        initSize = 256;
    }
    if (pCamManger) {
        Frame *frame = pCamManger->getFrame();
        if (frame && frame->m_pFrameData) {
            unsigned char *data = new unsigned char[size];
            if (data) {
                LOGE("updateVideoFrame Frame size is %d", (unsigned int) frame->getSize());
                LOGE("buffer size is %d", (unsigned int) size);
                memcpy(data, frame->m_pFrameData, size);
                int w = frame->m_iFrameW, h = frame->m_iFrameH, ch = 3, bitpix = 8;
                if (calcHistogram(data, w, h, bitpix, ch, histData)) // 获取直方图
                {
                    if (isRecalculate) // LUT计算条件：LUT没有被计算过 或者 low/high发生变化，不需要反复计算
                    {
                        isRecalculate = !calcLut(histLow, histHigh, bitpix, lut);
                    }
                    applyLut(data, w, h, bitpix, ch, lut);
                    jint databack[256];
                    for (int i = 0; i < 256; ++i) {
                        databack[i] = histData[i][3];
                    }
                    env->SetIntArrayRegion(histHighData, (jsize) 0, (jsize) 256, databack);
                }
                if (buffer) {
                    memcpy(buffer, data, size);
                }
                delete[] data;
            }
        }
    }
    return histHighData;
}


JNIEXPORT void JNICALL
Java_com_glide_chujian_util_TpLib_clearNativeFrame(JNIEnv *env, jobject obj,
                                                         jobject directBuffer,
                                                         jint size) {
    LOGE("%s",__func__ );
    void *buffer = env->GetDirectBufferAddress(directBuffer);
    if (buffer) {
        LOGE("clear guide native size is %d", size);
        memset(buffer, 0, size);
    }
}

JNIEXPORT jboolean JNICALL
Java_com_glide_chujian_util_TpLib_isAlive(JNIEnv *env, jobject obj) {
    LOGE("%s",__func__ );
    CameraManager *pCamManger = CameraManager::getInstance();
    if (pCamManger) {
        return pCamManger && pCamManger->isAlive() ? JNI_TRUE : JNI_FALSE;
    } else {
        return JNI_FALSE;
    }
}

JNIEXPORT void JNICALL
Java_com_glide_chujian_util_TpLib_releaseCamera(JNIEnv *env, jobject obj) {
    LOGE("%s",__func__ );
    CameraManager *pCamManger = CameraManager::getInstance();
    if (pCamManger) {
        pCamManger->closeCamera();
    }
}

JNIEXPORT jboolean JNICALL
Java_com_glide_chujian_util_TpLib_restartCamera(JNIEnv *env, jobject instance) {
    CameraManager *pCamManger = CameraManager::getInstance();
    if (pCamManger) {
        //TODO
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_glide_chujian_util_TpLib_startCameraStream(JNIEnv *env, jobject thiz) {
    LOGE("%s",__func__ );
    CameraManager *pCamManger = CameraManager::getInstance();
    if (pCamManger) {
        return pCamManger->startStream() == JNI_OK;
    }
    return JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_glide_chujian_util_TpLib_init(JNIEnv *env, jobject obj) {
    LOGE("%s",__func__ );
    gObject = env->NewGlobalRef(obj);
    CameraManager *pCamManger = CameraManager::getInstance();
    if (pCamManger)
        pCamManger->init(CameraEventCallback, gLocalVm);
}

JNIEXPORT jint JNICALL
Java_com_glide_chujian_util_TpLib_openDevice(JNIEnv *env, jobject obj, jstring url) {
    CameraManager *pCamManger = CameraManager::getInstance();
    const char *s = env->GetStringUTFChars(url, JNI_FALSE);
    char rtspurl[64] = {'\0'};
    sprintf(rtspurl, "rtsp://%s", s);
    LOGE("open camera");
    env->ReleaseStringUTFChars(url, s);
    if (pCamManger) {
        const int ret = pCamManger->openCamera(rtspurl);
        return ret;
    } else {
        return 1;
    }
}


JNIEXPORT jboolean JNICALL
Java_com_glide_chujian_util_TpLib_stopCameraStream(JNIEnv *env, jobject thiz) {
    LOGE("%s",__func__ );
    CameraManager *pCamManger = CameraManager::getInstance();
    if (pCamManger) {
        return pCamManger->stopStream() == JNI_OK;
    }
    return JNI_FALSE;
}
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_glide_chujian_util_TpLib_startRecordVideo(JNIEnv *env, jobject thiz, jstring url) {
    LOGE("%s",__func__ );
    CameraManager *pCamManger = CameraManager::getInstance();
    const char *s = env->GetStringUTFChars(url, JNI_FALSE);
    if (pCamManger) {
        return pCamManger->recordVideo(s) == JNI_OK;
    }
    env->ReleaseStringUTFChars(url, s);
    return JNI_FALSE;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_glide_chujian_util_TpLib_stopRecordVideo(JNIEnv *env, jobject thiz) {
    LOGE("%s",__func__ );
    CameraManager *pCamManger = CameraManager::getInstance();
    if (pCamManger) {
        return pCamManger->recordVideo(nullptr) == JNI_OK;
    }
    return JNI_FALSE;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_glide_chujian_util_TpLib_updateBrowserPictureFrame(JNIEnv *env, jobject thiz,
                                                                  jobject direct_buffer,
                                                                  jstring url,jint size) {
    int w = 0, h = 0, chs = 0, bitpix = 0;

    LOGE("%s",__func__ );
    const char *jnamestr = env->GetStringUTFChars(url, JNI_FALSE);
    void *data = CFits::io().read(jnamestr, &w, &h, &chs, &bitpix);
    env->ReleaseStringUTFChars(url, jnamestr);
    if (data) {
        void *rgb888data = data2rgb888(data, w, h, bitpix, chs);
        if (rgb888data) {
            void *buffer = env->GetDirectBufferAddress(direct_buffer);
            if (buffer) {
                memcpy(buffer, rgb888data, size);
            }
            free(rgb888data);
            rgb888data = nullptr;
        }
        free(data);
        data = nullptr;
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_glide_chujian_util_TpLib_setHistHigh(JNIEnv *env, jobject thiz,
                                                    jint high) {
    if (high != histHigh) {
        isRecalculate = true;
        histHigh = high;
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_glide_chujian_util_TpLib_setHistLow(JNIEnv *env, jobject thiz, jint low) {
    if (low != histLow) {
        isRecalculate = true;
        histLow = low;
    }
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_glide_chujian_util_TpLib_loadBrowserFitsPicture(JNIEnv *env, jobject thiz,
                                                               jstring url) {
    int w = 0, h = 0, chs = 0, bitpix = 0;
    LOGE("%s",__func__ );
    const char *jnamestr = env->GetStringUTFChars(url, JNI_FALSE);
    void *data = CFits::io().read(jnamestr, &w, &h, &chs, &bitpix);
    env->ReleaseStringUTFChars(url, jnamestr);
    jintArray size = env->NewIntArray(2);
    if (data) {
        jint values[2];
        values[0] = w;
        values[1] = h;
        env->SetIntArrayRegion(size, (jsize) 0, (jsize) 2, values);
        free(data);
        data = nullptr;
    }
    return size;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_glide_chujian_util_TpLib_clearMainNativeFrame(JNIEnv *env, jobject thiz,
                                                             jobject direct_buffer, jint size) {
    LOGE("%s",__func__ );
    void *buffer = env->GetDirectBufferAddress(direct_buffer);
    if (buffer) {
        LOGE("clear main native size is %d", size);
        memset(buffer, 0, size);
    }
    if (pictureData) {
        free(pictureData);
        pictureData = nullptr;
    }

    if (pictureLut) {
        free(pictureLut);
        pictureLut = nullptr;
        initSize = 0;
    }
}