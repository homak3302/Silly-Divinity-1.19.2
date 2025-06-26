package net.homak.sillydivinity;

import net.fabricmc.api.ModInitializer;

import net.homak.sillydivinity.common.custom.lib.ModEvents;
import net.homak.sillydivinity.common.registry.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SillyDivinity implements ModInitializer {
	public static final String MOD_ID = "silly-divinity";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModItems.registerItems(); // items

		ModEvents.register(); // death handler

		LOGGER.info("Time to get silly :3"); // main log
	}
}