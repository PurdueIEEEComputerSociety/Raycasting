package edu.purdue.ieee.csociety.raycasting;

import edu.purdue.ieee.csociety.raycasting.util.SharedLibraryLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;

public class Main {

    public static final Logger LOGGER = LogManager.getLogger("Raycaster");

    public static void main(String[] args) {
        Thread.currentThread().setName("main");
        LOGGER.info("Starting...");
        //  Load LWJGL native libs
        SharedLibraryLoader.load();
        Main main = new Main();
        main.run();
    }

    public void run() {
        LOGGER.info("LWJL version is {}", Sys.getVersion());

        //  TODO
    }

    private void init() {
        //  TODO
    }

    private void mainLoop() {
        //  TODO
    }

}
