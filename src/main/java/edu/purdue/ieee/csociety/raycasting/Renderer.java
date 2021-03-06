package edu.purdue.ieee.csociety.raycasting;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    /**
     * Reference back to main
     */
    private final Main main;
    /**
     * Our raycaster
     */
    private final Raycaster raycaster;

    /**
     * Width of the viewport (in pixels) for the raycaster
     */
    private int rendererWidth;
    /**
     * Height of the viewport (in pixels) for the raycaster
     */
    private int rendererHeight;

    /**
     * Width (in pixels) of the output space (window width)
     */
    private int displayWidth;
    /**
     * Height (in pixels) of the output space (window height)
     */
    private int displayHeight;

    /**
     * Handle to our output texture
     */
    private int renderTexture;
    /**
     * True width of the output texture. Must be a power of 2
     */
    private int renderTextureWidth;
    /**
     * True height of the output texture. Must be a power of 2
     */
    private int renderTextureHeight;
    /**
     * The U coordinate of the clipped edge of the output texture (the nonzero edge of the viewport)
     */
    private float renderTextureU;
    /**
     * The V coordinate of the clipped edge of the output texture (the nonzero edge of the viewport)
     */
    private float renderTextureV;

    /**
     * Reusable array for transporting a column of pixel data from the raycaster to the texture buffer
     */
    private int[] columnPixels;
    /**
     * The buffer containing the pixel data
     */
    private IntBuffer renderTextureBuffer;
    /**
     * The color to clear the output texture to before painting with the raycaster
     */
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
        //  Create our buffers
        columnPixels = new int[rendererHeight];
        renderTextureBuffer = BufferUtils.createByteBuffer(rendererHeight * rendererWidth * Integer.BYTES).
                order(ByteOrder.BIG_ENDIAN).
                asIntBuffer();
    }

    public void onViewportSizeChanged(int newWidth, int newHeight) {
        displayWidth = newWidth;
        displayHeight = newHeight;
        //  TODO: This is where we'd implement supersampling
        rendererWidth = displayWidth;
        rendererHeight = displayHeight;
        //  Resize buffers
        columnPixels = new int[rendererHeight];
        renderTextureBuffer = BufferUtils.createByteBuffer(rendererHeight * rendererWidth * Integer.BYTES).
                order(ByteOrder.BIG_ENDIAN).
                asIntBuffer();
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
        //  While this appears backwards, it actually is correct
        //  Our texture is rotated 90 degrees so that the column space of the raycaster is actually the
        //  row space of our texture, which is required for bulk pixel transfer to the GPU since texture data
        //  is row-major
        renderTextureWidth = nextPowerOfTwo(rendererHeight);
        renderTextureHeight = nextPowerOfTwo(renderTextureWidth);
        Main.LOGGER.debug("Texture resized to {}x{} (renderArea {}x{})",
                renderTextureWidth, renderTextureHeight, rendererWidth, rendererHeight);
        renderTextureU = rendererHeight / (float) renderTextureWidth;
        renderTextureV = rendererWidth / (float) renderTextureHeight;
    }

    public void startFrame() {
        //  Clear column array
        Arrays.fill(columnPixels, clearColor);
        //  Notify raycaster to prep for calls to fillStrip()
        raycaster.startFrame();
    }

    public void renderFrame() {
        //  Repeatedly fetch a column of pixels from the raycaster
        glBindTexture(GL_TEXTURE_2D, renderTexture);
        renderTextureBuffer.rewind();
        for (int xPos = 0; xPos < rendererWidth; xPos++) {
            raycaster.fillStrip(columnPixels, xPos);
            renderTextureBuffer.put(columnPixels);
        }
        renderTextureBuffer.flip();
        //  We purposefully reverse width and height here because our texture is rotated 90 degrees
        //noinspection SuspiciousNameCombination
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, rendererHeight, rendererWidth,
                GL_RGBA, GL_UNSIGNED_BYTE, renderTextureBuffer);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void finishFrame() {
        //  Clear OGL frame
        glClearColor(0.5F, 0.5F, 0.5F, 1F);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //  Set up projection
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glViewport(0, 0, displayWidth, displayHeight);
        glOrtho(0, displayWidth, displayHeight, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        //  Render a fullscreen quad
        //  The texture coordinates are rotated 90 degrees since our output texture is rotated 90 degrees
        //  Bind output texture
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, renderTexture);
        //  Enable alpha blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //  Ignore depth test
        glDepthFunc(GL_ALWAYS);
        //  Draw two triangles to form a fullscreen quad
        glBegin(GL_TRIANGLE_FAN);
        glTexCoord2f(0, 0);
        glVertex2f(0, 0);
        glTexCoord2f(renderTextureU, 0);
        glVertex2f(0, displayHeight);
        glTexCoord2f(renderTextureU, renderTextureV);
        glVertex2f(displayWidth, displayHeight);
        glTexCoord2f(0, renderTextureV);
        glVertex2f(displayWidth, 0);
        glEnd();
        //  Finish, unbind texture
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
