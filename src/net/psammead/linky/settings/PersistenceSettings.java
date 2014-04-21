package net.psammead.linky.settings;

import java.io.File;

import net.psammead.linky.config.Config;
import net.psammead.linky.util.Data;

public final class PersistenceSettings {
	public final long	autoSaveTime;
	public final File	storageDir;

	public PersistenceSettings(Config config) {
		autoSaveTime	= config.getInt("autoSaveTime") * 60000L;	// minutes!
		storageDir		= Data.appFile(config.getString("dir"));
	}
}
