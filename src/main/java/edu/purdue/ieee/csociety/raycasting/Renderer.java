package edu.purdue.ieee.csociety.raycasting;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final Main main;
    private final Raycaster raycaster;

    private int rendererWidth;
    private int rendererHeight;

    private int renderTexture;

    public Renderer(Main main, Raycaster raycaster) {
        this.main = main;
        this.raycaster = raycaster;
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
    }

    public void onViewportSizeChanged(int newWidth, int newHeight) {
        rendererWidth = newWidth;
        rendererHeight = newHeight;
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
        glClearColor(0.5F, 0.5F, 0.5F, 1F);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //  TODO prep for a frame

        //  Notify raycaster to prep for calls to fillStrip()
        raycaster.startFrame();
    }

    public void renderFrame() {
        //  TODO Update the render texture with data from the raycaster
        //  Repeatedly fetch a column of pixels from the raycaster

    }

    public void finishFrame() {
        //  TODO Draw the result to the screen
    }

}
