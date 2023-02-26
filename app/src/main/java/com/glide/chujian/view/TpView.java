package com.glide.chujian.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;


import com.glide.chujian.BuildConfig;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import me.jessyan.autosize.utils.Preconditions;

public class TpView extends GLSurfaceView {

    private static String TAG = "TpView";
    public TpRender mRender;

    public TpView(Context context) {
        super(context);
        this.mRender = new TpRender(context);
        init(false, 0, 0);
    }

    public TpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRender = new TpRender(context);
        init(false, 0, 0);
    }

    public static void checkEglError(String prompt, EGL10 egl) {
        int error;
        while ((error = egl.eglGetError()) != EGL10.EGL_SUCCESS) {
            Log.e(TAG, String.format(Locale.getDefault(), "%s: EGL error: 0x%x", prompt, error));
        }
    }


    private void init(boolean translucent, int depth, int stencil) {
        if (translucent) {
            getHolder().setFormat(PixelFormat.TRANSLUCENT);
        }
        setEGLContextFactory(new ContextFactory());
        setEGLContextClientVersion(2);

        if (translucent)
            setEGLConfigChooser(new ConfigChooser(8,8,8,8,depth,stencil));
        else
            setEGLConfigChooser(new  ConfigChooser(5, 6, 5, 0, depth, stencil));


        setRenderer(mRender);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }


    void updateFrame() {
        mRender.updateFrame();
        requestRender();
    }


    private class ContextFactory implements EGLContextFactory {

        private int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
        @Override
        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
            checkEglError("Before eglCreateContext", egl);
            int[] attribs = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};
            EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attribs);
            checkEglError("After eglCreateContext", egl);
            return context;
        }

        @Override
        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            egl.eglDestroyContext(display, context);
        }
    }

    private static class ConfigChooser implements EGLConfigChooser {
        private final int[] mValue = new int[1];
        private int EGL_OPENGL_ES2_BIT = 4;
        private int[] CONFIG_ATTRIBS = {
                EGL10.EGL_RED_SIZE, 4, EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4,
                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, EGL10.EGL_NONE
        };
        private int mRedSize;
        private int mGreenSize;
        private int mBlueSize;
        private int mAlphaSize;
        private int mDepthSize;
        private int mStencilSize;

        public ConfigChooser(int mRedSize, int mGreenSize, int mBlueSize, int mAlphaSize, int mDepthSize, int mStencilSize) {
            this.mRedSize = mRedSize;
            this.mGreenSize = mGreenSize;
            this.mBlueSize = mBlueSize;
            this.mAlphaSize = mAlphaSize;
            this.mDepthSize = mDepthSize;
            this.mStencilSize = mStencilSize;
        }

        @Override
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int[] num_config = new int[1];
            egl.eglChooseConfig(display, CONFIG_ATTRIBS, null, 0, num_config);
            int numConfigs = num_config[0];
            Preconditions.checkArgument(numConfigs > 0,"No configs match configSpec" );
            EGLConfig[] configs = new EGLConfig[numConfigs];
            egl.eglChooseConfig(
                    display, CONFIG_ATTRIBS, configs, numConfigs,
                    num_config
            );
            if (BuildConfig.DEBUG) {
                printConfigs(egl, display, configs);
            }
            return chooseConfig(egl, display, configs);
        }
        public EGLConfig chooseConfig(@NotNull EGL10 egl, @NotNull EGLDisplay display, @NotNull EGLConfig[] configs) {
            for (EGLConfig config:configs){
                int d = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
                int s = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0);
                if (d < mDepthSize || s < mStencilSize) continue;
                int r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0);
                int g = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0);
                int b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0);
                int a = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0);
                if (r == mRedSize && g == mGreenSize && b == mBlueSize && a == mAlphaSize) return config;
            }

            return null;
        }


        private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
            return egl.eglGetConfigAttrib(display, config, attribute, this.mValue) ? this.mValue[0] : defaultValue;
        }


        private void printConfigs(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
            for (EGLConfig config : configs) {
                printConfig(egl, display, config);
            }
        }

        private final void printConfig(EGL10 egl, EGLDisplay display, EGLConfig config) {
            int[] attributes = new int[]{
                    EGL10.EGL_BUFFER_SIZE, EGL10.EGL_ALPHA_SIZE,
                    EGL10.EGL_BLUE_SIZE,
                    EGL10.EGL_GREEN_SIZE,
                    EGL10.EGL_RED_SIZE,
                    EGL10.EGL_DEPTH_SIZE,
                    EGL10.EGL_STENCIL_SIZE,
                    EGL10.EGL_CONFIG_CAVEAT,
                    EGL10.EGL_CONFIG_ID,
                    EGL10.EGL_LEVEL,
                    EGL10.EGL_MAX_PBUFFER_HEIGHT,
                    EGL10.EGL_MAX_PBUFFER_PIXELS,
                    EGL10.EGL_MAX_PBUFFER_WIDTH,
                    EGL10.EGL_NATIVE_RENDERABLE,
                    EGL10.EGL_NATIVE_VISUAL_ID,
                    EGL10.EGL_NATIVE_VISUAL_TYPE,
                    0x3030,  // EGL10.EGL_PRESERVED_RESOURCES,
                    EGL10.EGL_SAMPLES,
                    EGL10.EGL_SAMPLE_BUFFERS,
                    EGL10.EGL_SURFACE_TYPE,
                    EGL10.EGL_TRANSPARENT_TYPE,
                    EGL10.EGL_TRANSPARENT_RED_VALUE,
                    EGL10.EGL_TRANSPARENT_GREEN_VALUE,
                    EGL10.EGL_TRANSPARENT_BLUE_VALUE,
                    0x3039,  // EGL10.EGL_BIND_TO_TEXTURE_RGB,
                    0x303A,  // EGL10.EGL_BIND_TO_TEXTURE_RGBA,
                    0x303B,  // EGL10.EGL_MIN_SWAP_INTERVAL,
                    0x303C,  // EGL10.EGL_MAX_SWAP_INTERVAL,
                    EGL10.EGL_LUMINANCE_SIZE, EGL10.EGL_ALPHA_MASK_SIZE,
                    EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RENDERABLE_TYPE,
                    0x3042 // EGL10.EGL_CONFORMANT
            };
            String[] names = new String[]{
                    "EGL_BUFFER_SIZE", "EGL_ALPHA_SIZE",
                    "EGL_BLUE_SIZE", "EGL_GREEN_SIZE", "EGL_RED_SIZE",
                    "EGL_DEPTH_SIZE", "EGL_STENCIL_SIZE", "EGL_CONFIG_CAVEAT",
                    "EGL_CONFIG_ID", "EGL_LEVEL", "EGL_MAX_PBUFFER_HEIGHT",
                    "EGL_MAX_PBUFFER_PIXELS", "EGL_MAX_PBUFFER_WIDTH",
                    "EGL_NATIVE_RENDERABLE", "EGL_NATIVE_VISUAL_ID",
                    "EGL_NATIVE_VISUAL_TYPE", "EGL_PRESERVED_RESOURCES",
                    "EGL_SAMPLES", "EGL_SAMPLE_BUFFERS", "EGL_SURFACE_TYPE",
                    "EGL_TRANSPARENT_TYPE", "EGL_TRANSPARENT_RED_VALUE",
                    "EGL_TRANSPARENT_GREEN_VALUE",
                    "EGL_TRANSPARENT_BLUE_VALUE", "EGL_BIND_TO_TEXTURE_RGB",
                    "EGL_BIND_TO_TEXTURE_RGBA", "EGL_MIN_SWAP_INTERVAL",
                    "EGL_MAX_SWAP_INTERVAL", "EGL_LUMINANCE_SIZE",
                    "EGL_ALPHA_MASK_SIZE", "EGL_COLOR_BUFFER_TYPE",
                    "EGL_RENDERABLE_TYPE", "EGL_CONFORMANT"
            };
            int[] value = new int[1];
            for (int i = 0;i < attributes.length;i++){
                int attribute = attributes[i];
                String name = names[i];
                if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
                    Log.w(
                            TAG, String.format(
                                    Locale.getDefault(), "  %s: %d\n", name,
                                    value[0]
                            )
                    );
                } else {
                    Log.w(TAG, String.format(Locale.getDefault(), "  %s: failed\n", name));
                    while (egl.eglGetError() != EGL10.EGL_SUCCESS);
                }
            }
        }
    }
}
