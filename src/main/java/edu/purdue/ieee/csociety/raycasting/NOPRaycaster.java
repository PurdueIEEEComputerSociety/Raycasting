package edu.purdue.ieee.csociety.raycasting;

/**
 * A NOP raycaster that only returns black
 */
public class NOPRaycaster implements Raycaster {
    @Override
    public void setViewportSize(int width, int height) {
        //  NOP
    }

    @Override
    public void startFrame() {
        //  NOP
    }

    @Override
    public void fillStrip(int[] pixelOutput, int x) {
        //  NOP
    }
}
