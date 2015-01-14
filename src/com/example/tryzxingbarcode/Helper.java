package com.example.tryzxingbarcode;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class Helper {

	public static String readAssetTextFile(Context context, String inFile) {
		String tContents = "";

		try {
			InputStream stream = context.getAssets().open(inFile);

			int size = stream.available();
			byte[] buffer = new byte[size];
			stream.read(buffer);
			stream.close();
			tContents = new String(buffer);
		} catch (IOException e) {
			// Handle exceptions here
		}

		return tContents;

	}

}
