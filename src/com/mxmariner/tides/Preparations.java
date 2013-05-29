// Copyright (C) 2011 by Will Kamp <manimaul!gmail.com>
// Distributed under the terms of the Simplified BSD Licence.
// See license.txt for details

package com.mxmariner.tides;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.mxmariner.util.Constants;

public class Preparations extends AsyncTask<Void, Integer, Void> {

//	private boolean debugon = true;
//	private Logger logger = null;
	private MainActivity mCtx;

	public Preparations(MainActivity ctx) {
		mCtx = ctx;

//		if (debugon) {
//			logger = LoggerFactory.getLogger(Preparations.class);
//			logger.info("debug is on!");
//		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		final AssetManager am = mCtx.getAssets();

		/*
		 * STEP 1 copy over world file file from assets if needed
		 */
		final File wFile = Constants.GetMXWorldFile();
		final File wDir = Constants.GetMXWorldDir();

		// 12800 bytes is 100kb ... current world.vmp is 82.8kb / 82.841b
		if (!wFile.isFile() && wDir.getUsableSpace() > 12800) {
			try {
				byte[] buffer = new byte[1024];
				InputStream is = am.open(Constants.WORLDMAPNAME);
				OutputStream os = new BufferedOutputStream(
						new FileOutputStream(wFile));
				int read = is.read(buffer);
				while (read != -1) {
					os.write(buffer, 0, read);
					read = is.read(buffer);
				}
				os.close();
				is.close();
			} catch (IOException e) {
			}
		}

		/*
		 * STEP 2 copy over harmonics files from assets if needed
		 */
		final File hDir = Constants.GetMXTideDir();
		try {
			for (String p : am.list("harmonics")) {
				final File h = new File(hDir + "/" + p);
				if (!h.isFile()) {
					InputStream is = am.open("harmonics/" + p);
					OutputStream os = new BufferedOutputStream(
							new FileOutputStream(h));
					byte[] buffer = new byte[1024];
					int read = is.read(buffer);
					while (read != -1) {
						os.write(buffer, 0, read);
						read = is.read(buffer);
					}
					os.close();
					is.close();
				}
			}
		} catch (IOException e) {
		}

		return null;
	}

	@Override
	public void onPostExecute(Void result) {
		mCtx.load();
	}

}
