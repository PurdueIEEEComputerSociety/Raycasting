package edu.purdue.ieee.csociety.raycasting;

public interface Raycaster {

    /**
     * Configures the raycaster to output a scene of width x height
     * @param width The width of the viewport
     * @param height The height of the viewport
     */
    void setViewportSize(final int width, final int height);

    /**
     * Called by the renderer to notify the Raycaster to prepare for strip filling
     */
    void startFrame();

    /**
     * Renders the vertical strip of the scene at x.
     * @param pixelOutput The int array to fill with pixel data. Array is of length VIEWPORT_HEIGHT
     * @param x The x coordinate of the column, in screen space. Values [0, VIEWPORT_WIDTH)
     */
    void fillStrip(final int[] pixelOutput, final int x);

}
