#ifndef _CAMERA_H_
#define _CAMERA_H_

#include "cache.h"
#include "astrovideo.h"

typedef struct {
    int ret;
    int param;
    int value;
    int width;
    int height;
    size_t length;
    const void *ptr;//data
    void *ctx;
} EventExtra;

#define CAMERA_EVENT_ERROR       0x03    /* error*/
#define CAMERA_EVENT_IMAGE       0x04    /* image */

typedef void (__stdcall *CAMERA_EVENT_CALLBACK)(unsigned nEvent, void *pCallbackCtx, EventExtra *pExtra);

class CameraManager {
    CircleQueue<Frame> m_circleQueueFrame;
    HAstrovideo        m_hAstroVideo;
    CAMERA_EVENT_CALLBACK m_pEventCallback;
    EventExtra          m_eventExtra;
    static CameraManager* m_pInstance;
    static void EventCallBack(unsigned nEvent, unsigned nPara, void *pCallbackCtx, AstrovideoEventExtra *pExtra);
public:
    CameraManager();
    ~CameraManager();
    void init(CAMERA_EVENT_CALLBACK eventCallback, void *jvm);
    int openCamera(const char* url);
    void closeCamera();
    int startStream();
    int stopStream();
    int recordVideo(const char* fullpath);
    int getSize(unsigned& width, unsigned& height);
    inline Frame* getFrame() { return m_circleQueueFrame.get_rear(); }
    inline bool isAlive() { return m_hAstroVideo != nullptr; }
    inline static CameraManager* getInstance()
    {
      if (nullptr == m_pInstance)
        m_pInstance = new CameraManager();
      return m_pInstance;
    }
private:
    void handleEventCallBack(unsigned nEvent, unsigned nPara, AstrovideoEventExtra *pExtra);
};

#endif //_CAMERA_H_
