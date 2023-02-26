#include "camera.h"
#include "misc.h"

CameraManager* CameraManager::m_pInstance = nullptr;

CameraManager::CameraManager() : m_pEventCallback(nullptr), m_circleQueueFrame(2), m_hAstroVideo(nullptr) {
}

CameraManager::~CameraManager() {
   closeCamera();
}

void CameraManager::init(CAMERA_EVENT_CALLBACK eventCallback, void *jvm) {
  m_pEventCallback = eventCallback;
   Astrovideo_put_JVM(jvm);
   Astrovideo_Init(EventCallBack, this);
   Astrovideo_PriFlag(0x0008, 0x0010);  //disable ffmpeg log
//   Astrovideo_PriFlag(0x0008, 0x0008);//enable ffmpeg error log
//   Astrovideo_PriFlag(0x0010, 0x0010);//enable ffmpeg verbose log
}

int CameraManager::openCamera(const char *url) {
   if (m_hAstroVideo)
    return JNI_ERR;
   m_circleQueueFrame.clear();
   m_hAstroVideo = Astrovideo_Open(url);
   return m_hAstroVideo ? JNI_OK : JNI_ERR;
}

void CameraManager::closeCamera() {
    m_circleQueueFrame.clear();
    if(m_hAstroVideo)
    {
      Astrovideo_Close(m_hAstroVideo);
      m_hAstroVideo = nullptr;
    }
}

void CameraManager::EventCallBack(unsigned nEvent, unsigned nPara, void *pCallbackCtx, AstrovideoEventExtra *pExtra) {
    auto* pThis = static_cast<CameraManager*>(pCallbackCtx);
    if (pThis)
       pThis->handleEventCallBack(nEvent, nPara, pExtra);
}

void CameraManager::handleEventCallBack(unsigned nEvent, unsigned nPara, AstrovideoEventExtra *pExtra)
{
  if (!m_hAstroVideo)
    return;
  if (ASTROVIDEO_EVENT_IMAGE == nEvent) {
      int width = 0, height = 0;
      m_circleQueueFrame.enqueue([&](Frame *frame) {
          unsigned w = 0, h = 0;
          getSize(w, h);
          if (w * h) {
              width = w;
              height = h;
              frame->changeSize(width, height);
              Astrovideo_PullImage(m_hAstroVideo, frame->m_pFrameData, 24, nullptr, nullptr);
          }
      });
      m_eventExtra.width = width;
      m_eventExtra.height = height;
      m_pEventCallback(CAMERA_EVENT_IMAGE, nullptr, &m_eventExtra);
  }
   else if (ASTROVIDEO_EVENT_ERROR == nEvent || ASTROVIDEO_EVENT_EOF == nEvent)
    m_pEventCallback(CAMERA_EVENT_ERROR, nullptr, nullptr);
   else
    LOGI("%s: nEvent = %d ", __FUNCTION__, nEvent);
}

int CameraManager::startStream() {
    if (!m_hAstroVideo)
      return JNI_ERR;
    const int ret = Astrovideo_StartPullModeWithCallback(m_hAstroVideo, nullptr);
    if (ret < 0) {
        Astrovideo_Close(m_hAstroVideo);
        return JNI_ERR;
    }
    return JNI_OK;
}

int CameraManager::stopStream() {
    return Astrovideo_Stop(m_hAstroVideo);
}

int CameraManager::getSize(unsigned& width, unsigned& height) {
    return Astrovideo_Get_Size(m_hAstroVideo, &width, &height);
}

int CameraManager::recordVideo(const char* fullpath) {
    return Astrovideo_Record(m_hAstroVideo,fullpath);
}