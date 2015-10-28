package edu.purdue.ieee.csociety.raycasting;

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
