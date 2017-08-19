package com.metallic.tttandroid.utils;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

/**
 * Unzipping Files
 *
 * @author Thomas Krex
 *
 */
public class UnzipManager {
	private final String filePath;
	private final String destination;

	public UnzipManager(String file, String destination) {
		this.filePath = file;
		this.destination = destination;
	}

	public void unzip() {

		try {
			FileInputStream inputStream = new FileInputStream(filePath);
			ZipInputStream zipStream = new ZipInputStream(inputStream);
			ZipEntry zEntry = null;
			while ((zEntry = zipStream.getNextEntry()) != null) {

				if (zEntry.isDirectory()) {
					hanldeDirectory(zEntry.getName());
				} else {
					FileOutputStream fout = new FileOutputStream(
							this.destination + "/" + zEntry.getName());
					BufferedOutputStream bufout = new BufferedOutputStream(fout);
					byte[] buffer = new byte[1024];
					int read = 0;
					while ((read = zipStream.read(buffer)) != -1) {
						bufout.write(buffer, 0, read);
					}

					zipStream.closeEntry();
					bufout.close();
					fout.close();
				}
			}
			zipStream.close();
		} catch (Exception e) {
			Log.e("Unzip", "Unzipping failed");
			e.printStackTrace();
		}

	}

	public void hanldeDirectory(String dir) {
		File f = new File(this.destination + dir);
		if (!f.isDirectory()) {
			f.mkdirs();
		}

	}
}
