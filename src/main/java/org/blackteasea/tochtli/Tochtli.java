package org.blackteasea.tochtli;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tochtli implements ModInitializer {
    public static final String MOD_ID = "tochtli";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Tochtli started!");

    }
}
