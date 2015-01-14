package com.example.tryzxingbarcode;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity {

	private Button scanBtn, btLoad;
	private TextView formatTxt, contentTxt, orientationTxt, tvInstruction;

	HashMap<String, String> map;
	static final String FILE_INSTR = "instructions.txt";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		scanBtn = (Button) findViewById(R.id.scan_button);
		formatTxt = (TextView) findViewById(R.id.scan_format);
		contentTxt = (TextView) findViewById(R.id.scan_content);
		orientationTxt = (TextView) findViewById(R.id.scan_orientation);
		tvInstruction = (TextView) findViewById(R.id.tvInstruction); 
		scanBtn.setOnClickListener(listener1);

		btLoad = (Button) this.findViewById(R.id.btReloadInstruction);
		btLoad.setOnClickListener(listener2);

		map = new HashMap<String, String>();
		readInstructionIntoMap(map);
	}

	private void readInstructionIntoMap(HashMap<String, String> m) {
		String str = Helper.readAssetTextFile(this, FILE_INSTR);
		// Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		try {
			ObjectMapper mapper = new ObjectMapper();
			List<CodeInstruction> list = mapper.readValue(
					str,
					TypeFactory.defaultInstance().constructCollectionType(
							List.class, CodeInstruction.class));
			if (list.size() > 0) {
				map.clear();
				for (CodeInstruction ci : list) {
					Toast.makeText(getBaseContext(),
							ci.getCode() + ": " + ci.getInstruction(),
							Toast.LENGTH_SHORT).show();
					map.put(ci.getCode(), ci.getInstruction());
				}
			} else {
				Toast.makeText(getBaseContext(), "No data found in file",
						Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "Error: " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	View.OnClickListener listener2 = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			readInstructionIntoMap(map);
		}
	};

	View.OnClickListener listener1 = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// respond to clicks
			if (v.getId() == R.id.scan_button) {
				// scan
				IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
				scanIntegrator.initiateScan();
			}
		}

	};

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// retrieve scan result
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanningResult != null) {
			// we have a result
			String strContent = scanningResult.getContents();
			String strFormat = scanningResult.getFormatName();
			Integer intOrientation = scanningResult.getOrientation();

			formatTxt.setText(strFormat);
			contentTxt.setText(strContent);
			if (intOrientation != null)
				orientationTxt.setText(intOrientation.intValue());
			else
				orientationTxt.setText("0");
			// translate code into instruction
			String instruction = map.get(strContent);
			if(instruction != null) {
				tvInstruction.setText(instruction);
			}
				
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
