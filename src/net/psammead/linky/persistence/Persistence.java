package net.psammead.linky.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import net.psammead.linky.settings.PersistenceSettings;
import net.psammead.linky.util.NamedLogger;
import net.psammead.util.IOUtil;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/** loads and saves persistent data */
public final class Persistence implements Runnable {
	private final NamedLogger			logger;
	private final PersistenceSettings	settings;

	public Persistence(NamedLogger logger, PersistenceSettings settings) {
		this.logger			= logger;
		this.settings		= settings;
		
		// ensure the base directory exists
		settings.storageDir.mkdirs();
	}
	
	// all persistent objects we are managing
	private List<Persistent> persistents	= new LinkedList<Persistent>();

	// whether the autoSave-Thread should be stopped
	private boolean	stopThread	= false;
	
	// constants
	private static final String	ENCODING	= "UTF-8";

	//------------------------------------------------------------------------------
	//## lifecycle
	
	/** starts the autoSave Thread */
	public synchronized void start() {
		logger.message("Persistence starting");
		load();
		new Thread(this).start();
		logger.message("Persistence started");
	}

	/** stops the autoSave Thread */
	public synchronized void stop() {
		logger.message("Persistence stopping");
		stopThread	= true;
		save();
		logger.message("Persistence stopped");
	}
	
	//------------------------------------------------------------------------------
	//## public api
	
	/** registers a Model to be loaded and stored with this Persistence */
	public synchronized void register(Persistent persistent) {
		persistents.add(persistent);
	}

	/** unregisters a Model to be loaded and stored with this Persistence */
	public synchronized void unregister(Persistent persistent) {
		persistents.remove(persistent);
	}
	
	//------------------------------------------------------------------------------
	//## private implementation
	
	/** load all registered Persistent objects */
	private synchronized void load() {
		for (Persistent persistent : persistents)	load(persistent);
	}
	
	/** save all registered Persistent objects */
	private synchronized void save() {
		for (Persistent persistent : persistents)	save(persistent);
	}
	
	/** load all seens from a file */
	private void load(Persistent persistent) {
		String	name	= persistent.getPersistentName();
		File	file	= new File(settings.storageDir, name + ".xml"); 
		
		logger.message("loading persistent data from " + file);

		Object	model	= null;
		Reader	in		= null;
		try {
			XStream xstream = new XStream(new DomDriver(ENCODING)); 
			in		= new InputStreamReader(new FileInputStream(file), ENCODING);
			model	= xstream.fromXML(in);
		} 
		catch (Exception e) {
			logger.message("cannot load " + name + " from " + file + ": " + e.getMessage());
		} 
		finally {
			IOUtil.closeSilent(in);
		}

		if (model != null)	persistent.setPersistentModel(model);
	}
	
	/** store all seends into a file */
	private void save(Persistent persistent) {
		String	name	= persistent.getPersistentName();
		File	file	= new File(settings.storageDir, name + ".xml"); 

		logger.message("save persistent data to " + file);

		Object	model	= persistent.getPersistentModel();
		Writer	out		= null;
		try {
			XStream xstream = new XStream(new DomDriver(ENCODING)); 
			out	= new OutputStreamWriter(new FileOutputStream(file), ENCODING);
			xstream.toXML(model, out);
		}
		catch (Exception e) {
			logger.message("cannot save " + name  + " to " + file + ": " + e.getMessage());
		}
		finally {
			IOUtil.closeSilent(out);
		}
	}

	/** the autoSave Thread */
	public void run() {
		for (;;) {
			if (stopThread)	break;
			try { Thread.sleep(settings.autoSaveTime); }
			catch (InterruptedException e) { break; }
			if (stopThread)	break;
			logger.message("autoSave");
			save();
		}
	}
}
