package com.mxmariner.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Environment;

public class Constants {
	public static final String WORLDMAPNAME = "world.map";
	
	public static File getMXBaseDir() {

		final String path = (Environment.getExternalStorageDirectory() + "/mxmariner");
		final File f = new File(path);
		if (!f.isDirectory()) {
			f.mkdir();
		}

		try {
			final File crumb = new File(path + "/dir.config");
			if (crumb.isFile()) {
				final InputStream fis = new FileInputStream(crumb);
				final BufferedReader br = new BufferedReader(
						new InputStreamReader(fis, "UTF-8"));
				final File userDir = new File(br.readLine());
				br.close();
				if (userDir.isDirectory()) {
					return userDir;
				}
			}
		} catch (IOException e) {

		}
		return f;
	}

	public static File GetMXTideDir() {

		final String path = getMXBaseDir() + "/tideharmonics";
		final File f = new File(path);
		if (!f.isDirectory()) {
			f.mkdir();
		}
		return f;
	}

	public static File GetMXWorldFile() {
		return new File(GetMXWorldDir() + "/" + WORLDMAPNAME);
	}

	public static File GetMXWorldDir() {

		final String path = getMXBaseDir() + "/world";
		final File f = new File(path);
		if (!f.isDirectory()) {
			f.mkdir();
		}
		return f;
	}

}