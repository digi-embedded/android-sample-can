/**
 * Copyright (c) 2014-2016 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */

package com.digi.android.sample.can;

import java.util.regex.Pattern;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * CAN frame dialog class.
 *
 * <p>This dialogs allows the creation of a CAN frame.</p>
 */
public class CANFrameDialog extends Dialog implements TextWatcher {

	// Constants.
	private final static String HEX_PATTERN = "[0-9a-fA-F]{0,2}";

	// Variables.
	private String data;
	
	private Button okButton;

	private EditText[] texts;
	
	private CANSampleActivity sampleActivity;
	
	private int interfaceNumber;
	
	/**
	 * Class constructor. Instances a new object of type CAN Frame Dialog
	 * with the given parameters.
	 * 
	 * @param sampleActivity CAN Sample activity.
	 * @param data the frame to represent.
	 * @param interfaceNumber the CAN interface number.
	 */
	public CANFrameDialog(CANSampleActivity sampleActivity, String data, int interfaceNumber) {
		super(sampleActivity);
		setTitle(sampleActivity.getResources().getString(R.string.frame_edit));
		this.sampleActivity = sampleActivity;
		this.data = data;
		this.interfaceNumber = interfaceNumber;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frame_dialog);
		initializeUIComponents();
		fillFields();
	}

	@Override
	public void afterTextChanged(Editable s) {
		updateFrame();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

	/**
	 * Initializes dialog UI components and sets listeners.
	 */
	private void initializeUIComponents() {
		texts = new EditText[]{
				(EditText) findViewById(R.id.byte0),
				(EditText) findViewById(R.id.byte1),
				(EditText) findViewById(R.id.byte2),
				(EditText) findViewById(R.id.byte3),
				(EditText) findViewById(R.id.byte4),
				(EditText) findViewById(R.id.byte5),
				(EditText) findViewById(R.id.byte6),
				(EditText) findViewById(R.id.byte7)};

		for (EditText t: texts)
			t.addTextChangedListener(this);
		
		okButton = (Button)findViewById(R.id.ok_button);
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sampleActivity.updateCANFrame(data, interfaceNumber);
				dismiss();
			}
		});

		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	/**
	 * Fills all the dialog byte fields with the given frame.
	 */
	private void fillFields() {
		byte[] bytes = HexUtils.hexStringToByteArray(data.replace(" ", ""));
		for (int i = 0; i < texts.length; i ++)
			texts[i].setText(HexUtils.byteArrayToHexString(new byte[]{bytes[i]}));
	}
	
	/**
	 * Updates and stores the frame with the text field values.
	 */
	private void updateFrame() {
		// First validate.
		for (int i = 0; i < texts.length; i ++) {
			if (!Pattern.matches(HEX_PATTERN, texts[i].getText())) {
				Toast.makeText(sampleActivity, "Frame has invalid characters", Toast.LENGTH_SHORT).show();
				okButton.setEnabled(false);
				return;
			}
		}

		// Build the frame.
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < texts.length; i++) {
			String fieldText = texts[i].getText().toString();
			int diff = 2 - fieldText.length();
			for (int j = 0; j < diff; j++)
				fieldText = "0" + fieldText;
			sb.append(fieldText);
			if (i < 7)
				sb.append(" ");
		}

		data = sb.toString().toUpperCase();
		okButton.setEnabled(true);
	}
}
