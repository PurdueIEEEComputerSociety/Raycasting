package edu.purdue.ieee.csociety.raycasting;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
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

    private int[] columnPixels;

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
        glEnable(GL_TEXTURE_2D);
        renderTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, renderTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
                rendererWidth, rendererHeight,
                0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glBindTexture(GL_TEXTURE_2D, 0);

        columnPixels = new int[rendererHeight];
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
        }
        //  Resize texture
        glBindTexture(GL_TEXTURE_2D, renderTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
                rendererWidth, rendererHeight,
                0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glBindTexture(GL_TEXTURE_2D, 0);
        //  Notify raycaster
        raycaster.setViewportSize(rendererWidth, rendererHeight);
    }

    public void startFrame() {
        //  TODO prep for a frame
        //  Clear column array
        Arrays.fill(columnPixels, clearColor);
        //  Notify raycaster to prep for calls to fillStrip()
        raycaster.startFrame();
    }

    public void renderFrame() {
        //  TODO Update the render texture with data from the raycaster
        //  Repeatedly fetch a column of pixels from the raycaster
        glBindTexture(GL_TEXTURE_2D, renderTexture);
        IntBuffer buffer = BufferUtils.createIntBuffer(rendererHeight);
        for (int xPos = 0; xPos < rendererWidth; xPos++) {
            buffer.rewind();
            raycaster.fillStrip(columnPixels, xPos);
            buffer.put(columnPixels);
            buffer.flip();
            glTexSubImage2D(GL_TEXTURE_2D, 0, xPos, 0, 1, rendererHeight, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        }
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void finishFrame() {
        glClearColor(0.5F, 0.5F, 0.5F, 1F);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //  TODO Draw the result to the screen
    }

}
