/*
 * Copyright (c) 2014-2025, Digi International Inc. <support@digi.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.digi.android.sample.can;

import java.util.regex.Pattern;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

	private final CANSampleActivity sampleActivity;

	private final int interfaceNumber;

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
				findViewById(R.id.byte0),
				findViewById(R.id.byte1),
				findViewById(R.id.byte2),
				findViewById(R.id.byte3),
				findViewById(R.id.byte4),
				findViewById(R.id.byte5),
				findViewById(R.id.byte6),
				findViewById(R.id.byte7)};

		for (EditText t: texts)
			t.addTextChangedListener(this);

		okButton = findViewById(R.id.ok_button);
		okButton.setOnClickListener(v -> {
			sampleActivity.updateCANFrame(data, interfaceNumber);
			dismiss();
		});

		Button cancelButton = findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(v -> dismiss());
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
		for (EditText text : texts) {
			if (!Pattern.matches(HEX_PATTERN, text.getText())) {
				Toast.makeText(sampleActivity, "Frame has invalid characters", Toast.LENGTH_SHORT).show();
				okButton.setEnabled(false);
				return;
			}
		}

		// Build the frame.
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < texts.length; i++) {
			StringBuilder fieldText = new StringBuilder(texts[i].getText().toString());
			int diff = 2 - fieldText.length();
			for (int j = 0; j < diff; j++)
				fieldText.insert(0, "0");
			sb.append(fieldText);
			if (i < 7)
				sb.append(" ");
		}

		data = sb.toString().toUpperCase();
		okButton.setEnabled(true);
	}
}
