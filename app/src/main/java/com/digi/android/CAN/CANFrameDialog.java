package com.example.android.CAN;

import java.util.regex.Pattern;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CANFrameDialog extends Dialog implements TextWatcher {

	// Constants
	private final static String HEX_PATTERN = "[0-9a-fA-F]{0,2}";
	
	// Variables
	private EditText byte0;
	private EditText byte1;
	private EditText byte2;
	private EditText byte3;
	private EditText byte4;
	private EditText byte5;
	private EditText byte6;
	private EditText byte7;
	
	private String frame;
	
	private Button okButton;
	private Button cancelButton;
	
	private EditText[] texts;
	
	private CANSample sample;
	
	private int canID;
	
	/**
	 * Class constructor. Instances a new object of type CAN Frame Dialog
	 * with the given parameters.
	 * 
	 * @param context Application context.
	 */
	public CANFrameDialog(CANSample sample, String frame, int canID) {
		super(sample);
		setTitle(sample.getResources().getString(R.string.frame_edit));
		this.sample = sample;
		this.frame = frame;
		this.canID = canID;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frame_dialog);
		initializeUIComponents();
		fillFields();
	}
	
	/**
	 * Initializes dialog UI components and sets listeners.
	 */
	private void initializeUIComponents() {
		byte0 = (EditText)findViewById(R.id.byte0);
		byte1 = (EditText)findViewById(R.id.byte1);
		byte2 = (EditText)findViewById(R.id.byte2);
		byte3 = (EditText)findViewById(R.id.byte3);
		byte4 = (EditText)findViewById(R.id.byte4);
		byte5 = (EditText)findViewById(R.id.byte5);
		byte6 = (EditText)findViewById(R.id.byte6);
		byte7 = (EditText)findViewById(R.id.byte7);
		byte0.addTextChangedListener(this);
		byte1.addTextChangedListener(this);
		byte2.addTextChangedListener(this);
		byte3.addTextChangedListener(this);
		byte4.addTextChangedListener(this);
		byte5.addTextChangedListener(this);
		byte6.addTextChangedListener(this);
		byte7.addTextChangedListener(this);
		texts = new EditText[]{byte0, byte1, byte2, byte3, byte4, byte5, byte6, byte7};
		
		okButton = (Button)findViewById(R.id.ok_button);
		cancelButton = (Button)findViewById(R.id.cancel_button);
		
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sample.updateCANFrame(frame, canID);
				dismiss();
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	/**
	 * Fills all the dialog byte fields with the given frame.
	 */
	private void fillFields() {
		byte[] bytes = HexUtils.hexStringToByteArray(frame.replace(" ", ""));
		for (int i = 0; i < 8; i ++)
			texts[i].setText(HexUtils.byteArrayToHexString(new byte[]{bytes[i]}));
	}
	
	/**
	 * Updates and stores the frame with the text field values.
	 */
	private void updateFrame() {
		// First validate.
		for (int i = 0; i < 8; i ++) {
			if (!Pattern.matches(HEX_PATTERN, texts[i].getText())) {
				Toast.makeText(sample, "Frame has invalid characters", Toast.LENGTH_SHORT).show();
				okButton.setEnabled(false);
				return;
			}
		}
		// Build the frame
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 8; i++) {
			String fieldText = texts[i].getText().toString();
			int diff = 2 - fieldText.length();
			for (int j = 0; j < diff; j++)
				fieldText = "0" + fieldText;
			sb.append(fieldText);
			if (i < 7)
				sb.append(" ");
		}
		frame = sb.toString().toUpperCase();
		okButton.setEnabled(true);
	}
	
	/**
	 * Retrieves the frame.
	 * 
	 * @return Hex frame.
	 */
	public String getFrame() {
		return frame;
	}

	/*
	 * (non-Javadoc)
	 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
	 */
	public void afterTextChanged(Editable s) {
		updateFrame();
	}

	/*
	 * (non-Javadoc)
	 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
	 */
	public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}

	/*
	 * (non-Javadoc)
	 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
	 */
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
