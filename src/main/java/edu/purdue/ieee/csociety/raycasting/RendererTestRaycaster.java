package edu.purdue.ieee.csociety.raycasting;

import edu.purdue.ieee.csociety.raycasting.util.ColorUtils;

import java.util.Arrays;

/**
 * A raycaster impl used to test the renderer - does not actually perform any raycasting
 */
public class RendererTestRaycaster implements Raycaster {

    private int width;
    private int height;

    @Override
    public void setViewportSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void startFrame() {

    }

    @Override
    public void fillStrip(int[] pixelOutput, int x) {
        //  Top half is a black to white gradient
        int shade = x * 255 / width;
        Arrays.fill(pixelOutput, 0, height / 2, ColorUtils.gray(shade));
        //  Bottom half graduates between red, green, and blue
        int third = width / 3;
        int sixth = width / 6;
        int redPower = 0xFF - Math.abs((x - sixth) * 255 / width);
        int greenPower = 0xFF - Math.abs((x - third - sixth) * 255 / width);
        int bluePower = 0xFF - Math.abs(x - third - third - sixth) * 255 / width;
        Arrays.fill(pixelOutput, height / 2, height, ColorUtils.color(redPower, greenPower, bluePower));
    }
}
