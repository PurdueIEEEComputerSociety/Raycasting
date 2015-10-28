package edu.purdue.ieee.csociety.raycasting;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final Main main;
    private final Raycaster raycaster;

    public Renderer(Main main, Raycaster raycaster) {
        this.main = main;
        this.raycaster = raycaster;
    }

    public void onViewportSizeChanged(int newWidth, int newHeight) {
        raycaster.setViewportSize(newWidth,  newHeight);
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
