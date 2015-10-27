package edu.purdue.ieee.csociety.raycasting;

import edu.purdue.ieee.csociety.raycasting.util.SharedLibraryLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.glfwTerminate;

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
        //  TODO
    }

    private void mainLoop() {
        //  TODO
    }

    private void finish() {

    }

}
