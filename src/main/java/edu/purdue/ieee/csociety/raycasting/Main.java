package edu.purdue.ieee.csociety.raycasting;

import edu.purdue.ieee.csociety.raycasting.util.SharedLibraryLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memDecodeUTF8;

public class Main {

    public static final Logger LOGGER = LogManager.getLogger("Raycaster");

    public static void main(String[] args) {
        Thread.currentThread().setName("main");
        LOGGER.info("Loading libraries");
        //  Load LWJGL native libs
        SharedLibraryLoader.load();
        Main main = new Main();
        main.run();
    }

    private static final int WINDOW_WIDTH = 800;

    private static final int WINDOW_HEIGHT = 600;

    private static final String WINDOW_TITLE = "IEEE CSociety Raycaster";

    /**
     * Error handling callback
     */
    private GLFWErrorCallback errorCallback;
    /**
     * Input handling callback
     */
    private GLFWKeyCallback keyCallback;
    /**
     * Mouse button handling callback
     */
    private GLFWMouseButtonCallback mouseButtonCallback;
    /**
     * Cursor position handling callback
     */
    private GLFWCursorPosCallback cursorPosCallback;
    /**
     * Window size change handling callback
     */
    private GLFWWindowSizeCallback windowSizeChangeCallback;
    /**
     * Window handle
     */
    private long windowHandle;
    /**
     * Main renderer
     */
    private Renderer renderer;
    /**
     *
     */
    private int fullscreenQuadVbo;

    private final Raycaster raycaster;

    public Main() {
        //  TODO Replace this with your implementation, e.g.
        //  raycaster = new MyRaycaster();
        raycaster = new NOPRaycaster();
        renderer = new Renderer(this, raycaster);
    }

    public void run() {
        LOGGER.info("Starting");
        LOGGER.info("LWJL version is {}", Sys.getVersion());
        try {
            //  Inititalize our program
            init();
            //  Enter main loop
            mainLoop();
            //  Exiting the main loop means we're closing
            finish();
        } catch (Exception e) {
            LOGGER.error("Unhandled exception during execution", e);
        } finally {
            //  Clean up
            glfwTerminate();
            if (errorCallback != null) {
                errorCallback.release();
            }
        }
        LOGGER.info("Stopped");
    }

    private void init() {
        //  Set up error callback
        errorCallback = GLFW.GLFWErrorCallback((error, description) ->
                LOGGER.error("GLFW error {}: {}", error, memDecodeUTF8(description)));
        glfwSetErrorCallback(errorCallback);

        //  Init GLFW
        if (glfwInit() != GL_TRUE) {
            throw new RuntimeException("GLFW initialization failed");
        }

        //  Window config
        glfwDefaultWindowHints();   //  Default hints
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); //  Hide window until we decide to show
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);   //  Do not allow resizing
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);  //  We want OGL v2.0 compat
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

        String windowTitle = WINDOW_TITLE + ": " + raycaster.getClass().getSimpleName();

        LOGGER.debug("Creating window {}x{} \"{}\"", WINDOW_WIDTH, WINDOW_HEIGHT, windowTitle);
        windowHandle = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, windowTitle, NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Window creation failed");
        }

        //  Register key callback
        keyCallback = GLFWKeyCallback(this::handleKeyEvent);
        glfwSetKeyCallback(windowHandle, keyCallback);

        //  Register mouse callbacks
        mouseButtonCallback = GLFWMouseButtonCallback(this::handleMouseButtonEvent);
        cursorPosCallback = GLFWCursorPosCallback(this::handleCursorPositionEvent);

        //  Register window resize callback
        windowSizeChangeCallback = GLFWWindowSizeCallback(this::handleWindowResizeEvent);
        glfwSetWindowSizeCallback(windowHandle, windowSizeChangeCallback);

        //  Center the window
        ByteBuffer videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(windowHandle,
                (GLFWvidmode.width(videoMode) - WINDOW_WIDTH) / 2,
                (GLFWvidmode.height(videoMode) - WINDOW_HEIGHT) / 2);

        //  Make OpenGL context current for this window
        glfwMakeContextCurrent(windowHandle);

        //  V-sync
        glfwSwapInterval(1);

        //  Show window
        glfwShowWindow(windowHandle);

        //  Check for framebuffer support
        ContextCapabilities capabilities = GLContext.createFromCurrent().getCapabilities();
        if (!capabilities.GL_EXT_framebuffer_object) {
            throw new RuntimeException("EXT_framebuffer_object not supported");
        }

        //  Initialize OGL resources
        createFullscreenQuad();

        //  Set up renderer
        renderer.startFrame();
    }

    private void createFullscreenQuad() {
        fullscreenQuadVbo = glGenBuffers();     //  Allocate a VBO
        glBindBuffer(GL_ARRAY_BUFFER, fullscreenQuadVbo);   //  Use the VBO we just allocated
        ByteBuffer b = BufferUtils.createByteBuffer(2 * 4 * Float.BYTES);   //  Allocate space for 4 vertices
        FloatBuffer fb = b.asFloatBuffer();
        fb.put(-1F).put(-1F); //  Add (-1,-1) (top left)
        fb.put(-1F).put(1F); //  Add (-1,1) (bottom left)
        fb.put(1F).put(1F); //  Add (1,1) (bottom right)
        fb.put(1F).put(-1F); //  Add (1,-1) (top right)
        //  Convienently enough these will also be our UV coordinates
        glBufferData(GL_ARRAY_BUFFER, b, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void handleKeyEvent(long window, int key, int scanCode, int action, int modifiers) {
        //  TODO Proper key event dispatching
        //  Quit on escape
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, GL_TRUE);
        }
    }

    private void handleMouseButtonEvent(long window, int button, int action, int modifiers) {
        //  TODO
    }

    private void handleCursorPositionEvent(long window, double xPos, double yPos) {
        //  TODO
    }

    private void handleWindowResizeEvent(long window, int width, int height) {
        LOGGER.debug("Window resized to {}x{}", width, height);
        renderer.onViewportSizeChanged(width, height);
    }

    private void mainLoop() {
        GLContext.createFromCurrent();
        //  Keep rendering until its time to exit
        while (glfwWindowShouldClose(windowHandle) == GL_FALSE) {
            //  Prep the frame
            renderer.startFrame();
            //  Draw the frame
            renderer.renderFrame();
            //  Post-frame
            renderer.finishFrame();
            //  Swap buffers
            glfwSwapBuffers(windowHandle);
            //  Poll for events
            glfwPollEvents();
        }
    }

    private void finish() {
        //  Release window and associated callbacks
        glfwDestroyWindow(windowHandle);
        keyCallback.release();
    }

}
