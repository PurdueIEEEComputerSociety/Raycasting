package edu.purdue.ieee.csociety.raycasting;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final Main main;
    private final Raycaster raycaster;

    private int rendererWidth;
    private int rendererHeight;

    private int displayWidth;
    private int displayHeight;

    private int renderTexture;
    private int renderTextureWidth;
    private int renderTextureHeight;
    private float renderTextureU;
    private float renderTextureV;

    private int[] columnPixels;
    private IntBuffer columnPixelBuffer;

    private int clearColor;

    public Renderer(Main main, Raycaster raycaster) {
        this.main = main;
        this.raycaster = raycaster;
        this.clearColor = 0x00000000;
    }

    public void init(int initialWidth, int initialHeight) {
        rendererWidth = initialWidth;
        rendererHeight = initialHeight;
        //  Create our output texture
        //  Since OGL textures must be sizes of powers of two, we must find the smallest power of two
        //  greater than or equal to our real size
        //  When we render we simply discard the portion of the texture outside of the window
        setTextureSize();
        glEnable(GL_TEXTURE_2D);
        renderTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, renderTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
                renderTextureWidth, renderTextureHeight,
                0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glBindTexture(GL_TEXTURE_2D, 0);

        columnPixels = new int[rendererHeight];
        columnPixelBuffer = BufferUtils.createByteBuffer(rendererHeight * 4).
                order(ByteOrder.BIG_ENDIAN).
                asIntBuffer();
    }

    public void onViewportSizeChanged(int newWidth, int newHeight) {
        displayWidth = newWidth;
        displayHeight = newHeight;
        //  TODO: This is where we'd implement supersampling
        boolean heightChange = newHeight != rendererHeight;
        rendererWidth = displayWidth;
        rendererHeight = displayHeight;
        //  Resize column array
        if (heightChange) {
            columnPixels = new int[rendererHeight];
            columnPixelBuffer = BufferUtils.createByteBuffer(rendererHeight * 4).
                    order(ByteOrder.BIG_ENDIAN).
                    asIntBuffer();
        }
        //  Resize texture
        setTextureSize();
        glBindTexture(GL_TEXTURE_2D, renderTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
                renderTextureWidth, renderTextureHeight,
                0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glBindTexture(GL_TEXTURE_2D, 0);
        //  Notify raycaster
        raycaster.setViewportSize(rendererWidth, rendererHeight);
    }

    private void setTextureSize() {
        renderTextureWidth = nextPowerOfTwo(rendererWidth);
        renderTextureHeight = nextPowerOfTwo(rendererHeight);
        renderTextureU = rendererWidth / (float) renderTextureWidth;
        renderTextureV = rendererHeight / (float) renderTextureHeight;
    }

    public void startFrame() {
        //  TODO prep for a frame
        //  Clear column array
        Arrays.fill(columnPixels, clearColor);
        //  Notify raycaster to prep for calls to fillStrip()
        raycaster.startFrame();
    }

    public void renderFrame() {
        //  Repeatedly fetch a column of pixels from the raycaster
        glBindTexture(GL_TEXTURE_2D, renderTexture);
        for (int xPos = 0; xPos < rendererWidth; xPos++) {
            columnPixelBuffer.rewind();
            raycaster.fillStrip(columnPixels, xPos);
            columnPixelBuffer.put(columnPixels);
            columnPixelBuffer.flip();
            glTexSubImage2D(GL_TEXTURE_2D, 0, xPos, 0, 1, rendererHeight,
                    GL_RGBA, GL_UNSIGNED_BYTE, columnPixelBuffer);
        }
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void finishFrame() {
        //  Clear OGL frame
        glClearColor(0.5F, 0.5F, 0.5F, 1F);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //  Set up projection
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, displayWidth, 0, displayHeight, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, renderTexture);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthFunc(GL_ALWAYS);
        glBegin(GL_TRIANGLE_FAN);
        glTexCoord2f(0, 0);
        glVertex2f(0, 0);
        glTexCoord2f(0, renderTextureV);
        glVertex2f(0, displayHeight);
        glTexCoord2f(renderTextureU, renderTextureV);
        glVertex2f(displayWidth, displayHeight);
        glTexCoord2f(renderTextureU, 0);
        glVertex2f(displayWidth, 0);
        glEnd();
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private int nextPowerOfTwo(int num) {
        //  http://stackoverflow.com/a/365068
        --num;
        num |= num >> 1;
        num |= num >> 2;
        num |= num >> 4;
        num |= num >> 8;
        num |= num >> 16;
        return num + 1;
    }
}
