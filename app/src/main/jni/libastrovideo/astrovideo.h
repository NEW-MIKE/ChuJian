#ifndef __astrovideo_h__
#define __astrovideo_h__

#ifdef _WIN32
#ifndef _INC_WINDOWS
#include <windows.h>
#endif
#endif

#ifdef __cplusplus
extern "C" {
#endif

#ifdef _WIN32
#pragma pack(push, 8)
#ifdef ASTROVIDEO_EXPORTS
#define ASTROVIDEO_API(x)    __declspec(dllexport)   x   __stdcall  /* in Windows, we use __stdcall calling convention, see https://docs.microsoft.com/en-us/cpp/cpp/stdcall */
#elif !defined(ASTROVIDEO_NOIMPORTS)
#define ASTROVIDEO_API(x)    __declspec(dllimport)   x   __stdcall
#else
#define ASTROVIDEO_API(x)    x   __stdcall
#endif

#else

#define ASTROVIDEO_API(x)    x

#ifndef HRESULT
#define HRESULT int
#endif

#ifndef SUCCEEDED
#define SUCCEEDED(hr)   (((HRESULT)(hr)) >= 0)
#endif
#ifndef FAILED
#define FAILED(hr)      (((HRESULT)(hr)) < 0)
#endif

#ifndef __stdcall
#define __stdcall
#endif

#ifndef __BITMAPINFOHEADER_DEFINED__
#define __BITMAPINFOHEADER_DEFINED__
typedef struct {
    unsigned        biSize;
    int             biWidth;
    int             biHeight;
    unsigned short  biPlanes;
    unsigned short  biBitCount;
    unsigned        biCompression;
    unsigned        biSizeImage;
    int             biXPelsPerMeter;
    int             biYPelsPerMeter;
    unsigned        biClrUsed;
    unsigned        biClrImportant;
} BITMAPINFOHEADER;
#endif

#endif

/* handle */
typedef struct AstrovideoT { int unused; } *HAstrovideo;

#ifndef TDIBWIDTHBYTES
#define TDIBWIDTHBYTES(bits)    ((unsigned)(((bits) + 31) & (~31)) / 8)
#endif

#define ASTROVIDEO_EVENT_IMAGE       0x04    /* image */
#define ASTROVIDEO_EVENT_ERROR       0x80    /* error */
#define ASTROVIDEO_EVENT_EOF         0x81    /* end of file */

#define ASTROVIDEO_PRIFLAG_RTPOVERRTSP     0x0001  /* rtp over rtsp */
#define ASTROVIDEO_PRIFLAG_BROADCAST       0x0002  /* broadcast discovery, change this must be before Astrovideo_Init */

typedef struct {
    int         result;
    unsigned    length;
    void*       ptr;
    void*       ctx;
}AstrovideoEventExtra;

typedef void(__stdcall* PASTROVIDEO_EVENT_CALLBACK)(unsigned nEvent, unsigned nPara, void* pCallbackCtx, AstrovideoEventExtra* pExtra);
typedef void(__stdcall* PASTROVIDEO_DATA_CALLBACK)(const void* pData, const BITMAPINFOHEADER* pHeader, void* pCallbackCtx);

ASTROVIDEO_API(void)     Astrovideo_Init(PASTROVIDEO_EVENT_CALLBACK pCallback, void* pCallbackCtx);
ASTROVIDEO_API(HAstrovideo) Astrovideo_Open(const char* id);
ASTROVIDEO_API(void)     Astrovideo_Close(HAstrovideo h); /* close the handle */
ASTROVIDEO_API(HRESULT)  Astrovideo_StartPushMode(HAstrovideo h, PASTROVIDEO_DATA_CALLBACK pDataCallback, void* pCallbackCtx);
#ifdef _WIN32
ASTROVIDEO_API(HRESULT)  Astrovideo_StartPullModeWithWndMsg(HAstrovideo h, HWND hWnd, UINT nMsg);
#endif
ASTROVIDEO_API(HRESULT)  Astrovideo_PullImage(HAstrovideo h, void* pImageData, int bits, unsigned* pnWidth, unsigned* pnHeight);
ASTROVIDEO_API(HRESULT)  Astrovideo_StartPullModeWithCallback(HAstrovideo h, void* pCallbackContext);
ASTROVIDEO_API(HRESULT)  Astrovideo_Get_Size(HAstrovideo h,  unsigned* pnWidth, unsigned* pnHeight);
ASTROVIDEO_API(HRESULT)  Astrovideo_Stop(HAstrovideo h);
ASTROVIDEO_API(HRESULT)  Astrovideo_Pause(HAstrovideo h, int bPause);
ASTROVIDEO_API(void)     Astrovideo_PriFlag(unsigned nFlag, unsigned nMask);
ASTROVIDEO_API(HRESULT)  Astrovideo_put_JVM(void* jvm);
ASTROVIDEO_API(HRESULT)  Astrovideo_Record(HAstrovideo h, const char* fullpath); /* fullpath == nullptr to stop record */

#ifdef _WIN32
#pragma pack(pop)
#endif

#ifdef __cplusplus
}
#endif

#endif
