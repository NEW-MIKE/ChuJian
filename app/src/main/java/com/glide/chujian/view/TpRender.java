package com.glide.chujian.view;

import android.content.Context;
import android.graphics.RectF;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import android.graphics.*;
import android.opengl.GLUtils;

import com.glide.chujian.R;
import com.glide.chujian.util.ShaderUtil;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TpRender implements GLSurfaceView.Renderer {
    private String TAG = "TpRender";
    private int mCurrentBitmapWidth = 0;
    private int mCurrentBitmapHeight = 0;
    private int mCurrentWidth = 0;
    private int mCurrentHeight = 0;
    private Matrix mMatrix = new Matrix();
    float mMinScale = 1.0f;
    float mMaxScale = 4.0f;
    private RectF mImageBoundsRect = new RectF(-1f, -1f, 1f, 1f);
    public float[] mOldPoints = {
            0f, 0f,
            0f, 0f,
            0f, 0f,
            0f, 0f
    };
    public float[] mNewPoints = {
            0f, 0f,
            0f, 0f,
            0f, 0f,
            0f, 0f
    };
    public float  mBoundLeft = 0f;
    public float  mBoundRight = 0f;
    public float  mBoundTop = 0f;
    public float  mBoundBottom = 0f;
    private int FLOAT_BYTE_LENGTH = 4;
    private int mProgram = 0;
    private boolean mIsPictureSource = false;
    private FloatBuffer mVertexBuffer;
    private ByteBuffer mDirectBuffer;
    private int mPositionAttributeLocation;
    private int mTexCoordAttributeLocation;
    private int[] mTextures;
    private int mTexture1 = 0;
/*    private float[] mVertices = {
            -1f, -1f, 0.0f, 0f, 1f,
            1f, -1f, 0.0f, 1f, 1f,
            -1f, 1f, 0.0f, 0f, 0f,
            1f, 1f, 0.0f, 1f, 0f
    };*/

    private float[] mVertices = {
            0f, 0f, 0.0f, 0f, 0f,
            0f, 0f, 0.0f, 0f, 0f,
            0f, 0f, 0.0f, 0f, 0f,
            0f, 0f, 0.0f, 0f, 0f,
    };

    private volatile float mZoom = 1.0f;
    private float mScaleFactor = 1f;

    public TpRender(Context context) {
        this.mContext = context;
    }

    private Context mContext;
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mProgram = ShaderUtil.createProgram(R.raw.vertex_shader, R.raw.fragment_shader);
        mPositionAttributeLocation = GLES20.glGetAttribLocation(mProgram, "position");
        mTexCoordAttributeLocation = GLES20.glGetAttribLocation(mProgram, "TexCoordIn");
        GLES20.glDisable(GL10.GL_CULL_FACE);
        mVertexBuffer = ByteBuffer.allocateDirect(mVertices.length * FLOAT_BYTE_LENGTH).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(mVertices);
        mVertexBuffer.position(0);
        mTexture1 = GLES20.glGetUniformLocation(mProgram, "texture1");
        mTextures = new int[2];
        GLES20.glGenTextures(2, mTextures, 0);
        for (int texture : mTextures) {
            setTexture(texture);
        }
    }

    public void setFrameSource(boolean isPicture){
        mIsPictureSource = isPicture;
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCurrentWidth = width;
        mCurrentHeight = height;
        GLES20.glViewport(0, 0, width, height);
        setupImageRect();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mTextures[0] == 0 || null == mDirectBuffer) {
            return;
        }

        GLES20.glClearColor(0, 0, 0, 0f);
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        GLES20.glActiveTexture(GLES10.GL_TEXTURE0);
        GLES20.glBindTexture(GLES10.GL_TEXTURE_2D, mTextures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, mCurrentBitmapWidth, mCurrentBitmapHeight, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, mDirectBuffer);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgram, "bDrawWindow"), 0);
        float[] points = {
                mImageBoundsRect.left, mImageBoundsRect.top,
                mImageBoundsRect.right, mImageBoundsRect.top,
                mImageBoundsRect.left, mImageBoundsRect.bottom,
                mImageBoundsRect.right, mImageBoundsRect.bottom
        };
        mOldPoints = points.clone();
        mMatrix.mapPoints(points);
        mNewPoints = points;
        if (mIsPictureSource) {
            mVertices[0] = points[0];
            mVertices[1] = points[1];
            mVertices[2] = 0.0f;
            mVertices[3] = 0.0f;
            mVertices[4] = 1.0f;
            mVertices[5] = points[2];
            mVertices[6] = points[3];
            mVertices[7] = 0.0f;
            mVertices[8] = 1.0f;
            mVertices[9] = 1.0f;
            mVertices[10] = points[4];
            mVertices[11] = points[5];
            mVertices[12] = 0.0f;
            mVertices[13] = 0.0f;
            mVertices[14] = 0.0f;
            mVertices[15] = points[6];
            mVertices[16] = points[7];
            mVertices[17] = 0.0f;
            mVertices[18] = 1.0f;
            mVertices[19] = 0.0f;
        }else {
            mVertices[0] = points[4];
            mVertices[1] = points[5];
            mVertices[2] = 0.0f;
            mVertices[3] = 0.0f;
            mVertices[4] = 1.0f;
            mVertices[5] = points[6];
            mVertices[6] = points[7];
            mVertices[7] = 0.0f;
            mVertices[8] = 1.0f;
            mVertices[9] = 1.0f;
            mVertices[10] = points[0];
            mVertices[11] = points[1];
            mVertices[12] = 0.0f;
            mVertices[13] = 0.0f;
            mVertices[14] = 0.0f;
            mVertices[15] = points[2];
            mVertices[16] = points[3];
            mVertices[17] = 0.0f;
            mVertices[18] = 1.0f;
            mVertices[19] = 0.0f;
        }
        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertices);
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(
                mPositionAttributeLocation,
                3,
                GLES10.GL_FLOAT,
                false,
                5 * FLOAT_BYTE_LENGTH,
                mVertexBuffer
        );
        mVertexBuffer.position(3);
        GLES20.glVertexAttribPointer(
                mTexCoordAttributeLocation,
                2,
                GLES10.GL_FLOAT,
                false,
                5 * FLOAT_BYTE_LENGTH,
                mVertexBuffer
        );
        GLES20.glEnableVertexAttribArray(mPositionAttributeLocation);
        GLES20.glEnableVertexAttribArray(mTexCoordAttributeLocation);
        GLES20.glDrawArrays(GLES10.GL_TRIANGLE_STRIP, 0, 4);
    }

    public void setCurrentVideoSize(int width, int height) {
        this.mCurrentBitmapWidth = width;
        this.mCurrentBitmapHeight = height;
        this.mDirectBuffer = ByteBuffer.allocateDirect((width * 24 + 31 & ~31) / 8 * height);
        this.setZoom(width, height, width);
    }

    public void setZoom(int videoWidth, int videoHeight, int imgWidth) {
        float k1 = Math.max((float)videoWidth / (float)this.mCurrentWidth, (float)videoHeight / (float)this.mCurrentHeight);
        float k2 = imgWidth == 0 ? 1.0F : (float)imgWidth / (float)videoWidth;
        mZoom = k1 * k2;
        setupImageRect();
    }

    private void setupImageRect() {

        if (mCurrentBitmapHeight == 0 || mCurrentBitmapWidth == 0 || mCurrentHeight == 0 || mCurrentWidth == 0) {
            return;
        }
        float glHalfWidth = 0.0F;
        float glHalfHeight = 0.0F;
        float imageAspect = (float)this.mCurrentBitmapWidth / (float)this.mCurrentBitmapHeight;
        float renderBufferAspect = (float)this.mCurrentWidth / (float)this.mCurrentHeight;
        if (imageAspect > renderBufferAspect) {
            glHalfWidth = 1.0F;
            glHalfHeight = glHalfWidth * renderBufferAspect / imageAspect;
        } else {
            glHalfHeight = 1.0F;
            glHalfWidth = glHalfHeight * imageAspect / renderBufferAspect;
        }

        this.mImageBoundsRect.set(-glHalfWidth, -glHalfHeight, glHalfWidth, glHalfHeight);
        this.mBoundLeft = (-glHalfWidth + (float)1) / (float)2 * (float)this.mCurrentWidth;
        this.mBoundRight = (glHalfWidth + (float)1) / (float)2 * (float)this.mCurrentWidth;
        this.mBoundTop = (-glHalfHeight + (float)1) / (float)2 * (float)this.mCurrentHeight;
        this.mBoundBottom = (glHalfHeight + (float)1) / (float)2 * (float)this.mCurrentHeight;
    }

    public void resetMatrix() {
        float[] v = new float[9];
        this.mMatrix.getValues(v);
        v[0] = 1.0F;
        v[4] = 1.0F;
        v[2] = 0.0F;
        v[5] = 0.0F;
        this.mMatrix.setValues(v);
    }


    public float getScale() {
        float[] values = new float[9];
        mMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    public void scale(float scale, float x, float y) {
        if (scale * this.mScaleFactor < this.mMinScale) {
            this.mScaleFactor = 1.0F;
            this.resetMatrix();
        } else {
            this.mScaleFactor *= scale;
            this.mMatrix.postScale(scale, scale, 0.0F, 0.0F);
        }
    }

    public PointF getTranslation() {
        float[] values = new float[9];
        this.mMatrix.getValues(values);
        return new PointF(
                values[Matrix.MTRANS_X],
                values[Matrix.MTRANS_Y]);
    }

    public void trans(Float x , Float y) {
        Float x1 = x;
        Float y1 = y;
        x1 = x1 * 2 / mCurrentWidth;
        y1 = y1 * 2 / mCurrentHeight;
        constrainTranslation(getScale(), x1, y1);
    }


    private void constrainTranslation(float scale, float tx, float ty) {
        float[] values = new float[9];
        PointF translation = new PointF();
        this.mMatrix.getValues(values);
        float transX = values[2] + tx;
        float transY = values[5] + ty;
        RectF imgeRect = this.mImageBoundsRect;
        float scaleX = Math.max(scale * imgeRect.right, 1.0F);
        float scaleY = Math.max(scale * imgeRect.bottom, 1.0F);
        if (transX < (float)0) {
            translation.x = Math.max(transX, (float)1 - scaleX);
        } else {
            translation.x = Math.min(transX, scaleX - (float)1);
        }

        if (transY < (float)0) {
            translation.y = Math.max(transY, (float)1 - scaleY);
        } else {
            translation.y = Math.min(transY, scaleY - (float)1);
        }

        values[Matrix.MTRANS_X] = translation.x;
        values[Matrix.MTRANS_Y] = translation.y;
        this.mMatrix.setValues(values);
    }


    public void mapPoints(@NotNull float[] points) {
        Matrix matrix = new Matrix();
        matrix.postConcat(matrix);
        matrix.postScale(this.mZoom, this.mZoom);
        matrix.mapPoints(points);
    }

    private void setTexture(int texture) {
        GLES20.glBindTexture(GLES10.GL_TEXTURE_2D, texture);
        GLES20.glTexParameterf(
                GL10.GL_TEXTURE_2D,
                GLES10.GL_TEXTURE_WRAP_S,
                GLES10.GL_CLAMP_TO_EDGE
        );
        GLES20.glTexParameterf(
                GL10.GL_TEXTURE_2D,
                GLES10.GL_TEXTURE_WRAP_T,
                GLES10.GL_CLAMP_TO_EDGE
        );
        GLES20.glTexParameterf(
                GL10.GL_TEXTURE_2D,
                GLES10.GL_TEXTURE_MIN_FILTER,
                GLES10.GL_NEAREST
        );
        GLES20.glTexParameterf(
                GL10.GL_TEXTURE_2D,
                GLES10.GL_TEXTURE_MAG_FILTER,
                GLES10.GL_LINEAR
        );
    }
    public void setStaticBitmap(@NotNull Bitmap bitmap) {
        GLES20.glActiveTexture(GLES10.GL_TEXTURE0);
        GLES20.glBindTexture(GLES10.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GLES10.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glUniform1i(mTexture1, 0);
        setCurrentVideoSize(bitmap.getWidth(), bitmap.getHeight());
    }


    public void updateFrame() {
/*        if (mDirectBuffer != null)
            GuideActivity.mLib.updateFrame(mDirectBuffer);*/
    }

    public ByteBuffer getDirectBuffer() {
        return mDirectBuffer;
    }
}
