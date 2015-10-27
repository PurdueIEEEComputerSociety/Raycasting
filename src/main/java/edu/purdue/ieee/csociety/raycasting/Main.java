package edu.purdue.ieee.csociety.raycasting;

import edu.purdue.ieee.csociety.raycasting.util.SharedLibraryLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GLContext;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
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
     * Window handle
     */
    private long windowHandle;

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
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);   //  Do not allow resizing

        LOGGER.debug("Creating window {}x{} \"{}\"", WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE);
        windowHandle = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE, NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Window creation failed");
        }

        //  Register key callback
        keyCallback = GLFWKeyCallback(this::handleKeyEvent);
        glfwSetKeyCallback(windowHandle, keyCallback);

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

    }

    private void handleKeyEvent(long window, int key, int scanCode, int action, int modifiers) {
        //  TODO Proper key event dispatching
        //  Quit on escape
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, GL_TRUE);
        }
    }

    private void mainLoop() {
        GLContext.createFromCurrent();

        //  Clear color
        glClearColor(1F, 1F, 1F, 0F);

        //  Keep rendering until its time to exit
        while (glfwWindowShouldClose(windowHandle) == GL_FALSE) {
            //  Prep the frame
            setUpFrame();
            //  Draw the frame
            renderFrame();
            //  Swap buffers
            glfwSwapBuffers(windowHandle);
            //  Poll for events
            glfwPollEvents();
        }
    }

    private void setUpFrame() {
        //  Clear frame
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //  TODO Set up camera, etc

    }

    private void renderFrame() {
        //  TODO Draw stuff
    }

    private void finish() {
        //  Release window and associated callbacks
        glfwDestroyWindow(windowHandle);
        keyCallback.release();
    }

}
