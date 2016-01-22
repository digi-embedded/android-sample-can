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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import com.digi.android.can.CAN;
import com.digi.android.can.CANFilter;
import com.digi.android.can.CANFrame;
import com.digi.android.can.CANId;
import com.digi.android.can.CANManager;
import com.digi.android.can.ICANListener;
import com.digi.android.util.NoSuchInterfaceException;

/**
 * CAN sample application.
 *
 * <p>This example monitors the communication in the two CAN interfaces using
 * the CAN API.</p>
 *
 * <p>For a complete description on the example, refer to the 'README.md' file
 * included in the example directory.</p>
 */
public class CANSampleActivity extends Activity implements ICANListener, OnClickListener {

	// Constants.
	private final static int ACTION_DRAW_FRAME = 0;
	private final static int ACTION_UPDATE_CAN0_FRAME = 1;
	private final static int ACTION_UPDATE_CAN1_FRAME = 2;
	private final static String TAG_FRAME = "frame";
	private final static String READ_BUTTON_MSG = "LISTENING - PUSH TO STOP";
	private final static String READ_DATA_MSG = "READ DATA";
	private final static String INIT_ID = "00000000";
	private final static String INIT_MASK = "00000000";
	private final static String INIT_TX_FRAME = "00 00 00 00 00 00 00 00";

	// Variables.
	private EditText[] idRxTexts, idTxTexts, maskTexts;
	private CheckBox[] extIdTxCheckBoxes, extIdRxCheckBoxes, rtrCheckBoxes;
	private EditText[] rx0Bytes;
	private EditText[] rx1Bytes;
	private TextView[] txFrameLabels;
	private TextView[] receivedIDLabels;
	private Button[] CANReadButtons;

	private CAN[] mCans;
	private boolean[] mCanReading = new boolean[] {false, false};
	private CANFrame receivedFrame;

	private IncomingHandler handler = new IncomingHandler(this);

	/**
	 * Handler to manage UI calls from different threads.
	 */
	static class IncomingHandler extends Handler {
		private final WeakReference<CANSampleActivity> wActivity;

		IncomingHandler(CANSampleActivity activity) {
			wActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			CANSampleActivity activity = wActivity.get();

			if (activity == null)
				return;

			switch (msg.what) {
				case ACTION_DRAW_FRAME:
					// Check which interface is the owner of the received frame
					// (bear in mind that both interfaces might have set the same ID).
					// Besides, when checking the frame's ID, it's necessary to
					// filter out the flags (higher ID's bits).
					CANFrame rFrame = activity.receivedFrame;
					int interfaceNumber = rFrame.getCAN().getInterfaceNumber();
					CANId id = rFrame.getId();
					if (interfaceNumber == 0) {
						byte[] data = rFrame.getData();
						for (int i = 0; i < rFrame.getDlc(); i++) {
							activity.rx0Bytes[i].setText(activity.byteToString(data[i]));
						}
						for (int i = activity.rx0Bytes.length - 1; i > rFrame.getDlc() - 1; i--) {
							activity.rx0Bytes[i].setText("-");
						}

						// Filter out flags and show only ID bits.
						activity.receivedIDLabels[interfaceNumber].setText(Integer.toHexString(id.getValue()));
					}
					if (interfaceNumber == 1) {
						byte[] data = rFrame.getData();
						for (int i = 0; i < rFrame.getDlc(); i++) {
							activity.rx1Bytes[i].setText(activity.byteToString(data[i]));
						}
						for (int i = activity.rx1Bytes.length - 1; i > rFrame.getDlc() - 1; i--) {
							activity.rx1Bytes[i].setText("-");
						}

						// Filter out flags and show only ID bits.
						activity.receivedIDLabels[interfaceNumber].setText(Integer.toHexString(id.getValue()));
					}
					break;
				case ACTION_UPDATE_CAN0_FRAME:
					String frame = msg.getData().getString(TAG_FRAME);
					activity.txFrameLabels[0].setText(frame);
					break;
				case ACTION_UPDATE_CAN1_FRAME:
					frame = msg.getData().getString(TAG_FRAME);
					activity.txFrameLabels[1].setText(frame);
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Instantiate the elements from layout.
		idTxTexts = new EditText[] {
				(EditText)findViewById(R.id.ID0_tx),
				(EditText)findViewById(R.id.ID1_tx)};

		extIdTxCheckBoxes = new CheckBox[] {
				(CheckBox)findViewById(R.id.ExtIDTx0),
				(CheckBox)findViewById(R.id.ExtIDTx1)};

		rtrCheckBoxes = new CheckBox[] {
				(CheckBox)findViewById(R.id.RTR0),
				(CheckBox)findViewById(R.id.RTR1)};

		txFrameLabels = new TextView[] {
				(TextView)findViewById(R.id.tx0_frame),
				(TextView)findViewById(R.id.tx1_frame)};

		idRxTexts = new EditText[] {
				(EditText)findViewById(R.id.ID0_rx),
				(EditText)findViewById(R.id.ID1_rx)};

		extIdRxCheckBoxes = new CheckBox[] {
				(CheckBox)findViewById(R.id.ExtIDRx0),
				(CheckBox)findViewById(R.id.ExtIDRx1)};

		maskTexts = new EditText[] {
				(EditText)findViewById(R.id.Mask0),
				(EditText)findViewById(R.id.Mask1)};

		//rx0Bytes = new EditText[8];
		rx0Bytes = new EditText[] {
				(EditText)findViewById(R.id.rx0_byte0),
				(EditText)findViewById(R.id.rx0_byte1),
				(EditText)findViewById(R.id.rx0_byte2),
				(EditText)findViewById(R.id.rx0_byte3),
				(EditText)findViewById(R.id.rx0_byte4),
				(EditText)findViewById(R.id.rx0_byte5),
				(EditText)findViewById(R.id.rx0_byte6),
				(EditText)findViewById(R.id.rx0_byte7)};

		rx1Bytes = new EditText[] {
				(EditText)findViewById(R.id.rx1_byte0),
				(EditText)findViewById(R.id.rx1_byte1),
				(EditText)findViewById(R.id.rx1_byte2),
				(EditText)findViewById(R.id.rx1_byte3),
				(EditText)findViewById(R.id.rx1_byte4),
				(EditText)findViewById(R.id.rx1_byte5),
				(EditText)findViewById(R.id.rx1_byte6),
				(EditText)findViewById(R.id.rx1_byte7)};

		receivedIDLabels = new TextView[] {
				(TextView)findViewById(R.id.ReceivedID0),
				(TextView)findViewById(R.id.ReceivedID1)};

		CANReadButtons = new Button[] {
				(Button)findViewById(R.id.rx0_button),
				(Button)findViewById(R.id.rx1_button)};

		Button editFrame0Button = (Button) findViewById(R.id.edit_frame0_button);
		editFrame0Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CANFrameDialog dialog = new CANFrameDialog(CANSampleActivity.this, txFrameLabels[0].getText().toString(), 0);
				dialog.show();
			}
		});

		Button editFrame1Button = (Button) findViewById(R.id.edit_frame1_button);
		editFrame1Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CANFrameDialog dialog = new CANFrameDialog(CANSampleActivity.this, txFrameLabels[1].getText().toString(), 1);
				dialog.show();
			}
		});
		Button can0SendButton = (Button) findViewById(R.id.tx0_button);
		Button can1SendButton = (Button) findViewById(R.id.tx1_button);

		// Show initial values.
		for (EditText t: maskTexts)
			t.setText(INIT_MASK);
		for (EditText t: idRxTexts)
			t.setText(INIT_ID);
		for (EditText t: idTxTexts)
			t.setText(INIT_ID);
		for (TextView l: txFrameLabels)
			l.setText(INIT_TX_FRAME);

		// Set event listeners for layout elements.
		can0SendButton.setOnClickListener(this);
		can1SendButton.setOnClickListener(this);
		for (Button b: CANReadButtons)
			b.setOnClickListener(this);

		// Get the CAN manager.
		CANManager canManager = new CANManager(this);

		// Initialize CAN objects.
		mCans = new CAN[] {
				canManager.createCAN(0),
				canManager.createCAN(1)};
	}

	@Override
	public void onPause() {
		super.onPause();

		for (CAN can: mCans) {
			// Restore controls.
			mCanReading[can.getInterfaceNumber()] = false;
			CANReadButtons[can.getInterfaceNumber()].setText(READ_DATA_MSG);

			// Stop receiving frames.
			try {
				can.stopRead();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Unregister listener.
			can.unregisterListener(this);

			// Close CAN interface.
			try {
				can.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		for (CAN can: mCans) {
			// Open CAN interface.
			try {
				can.open();
			} catch (NoSuchInterfaceException | IOException e) {
				e.printStackTrace();
			}

			// Register listeners.
			can.registerListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		int iNumber;
		switch (v.getId()) {
			case R.id.tx0_button:
				iNumber = 0;
				handleSendButtonPressed(mCans[iNumber], idTxTexts[iNumber], extIdTxCheckBoxes[iNumber], rtrCheckBoxes[iNumber], txFrameLabels[iNumber].getText().toString());
				break;
			case R.id.rx0_button:
				iNumber = 0;
				handleReadButtonPressed(mCans[iNumber], idRxTexts[iNumber], extIdRxCheckBoxes[iNumber], maskTexts[iNumber]);
				break;
			case R.id.tx1_button:
				iNumber = 1;
				handleSendButtonPressed(mCans[iNumber], idTxTexts[iNumber], extIdTxCheckBoxes[iNumber], rtrCheckBoxes[iNumber], txFrameLabels[iNumber].getText().toString());
				break;
			case R.id.rx1_button:
				iNumber = 1;
				handleReadButtonPressed(mCans[iNumber], idRxTexts[iNumber], extIdRxCheckBoxes[iNumber], maskTexts[iNumber]);
				break;
			default:
				break;
		}
	}

	/**
	 * Updates the activity with the received frame.
	 *
	 * @param frame Received frame.
	 */
	@Override
	public void frameReceived(CANFrame frame) {
		receivedFrame = frame;
		handler.sendEmptyMessage(ACTION_DRAW_FRAME);
	}

	/**
	 * Updates the UI with the given CAN frame to send and the corresponding
	 * interface identifier.
	 *
	 * @param frame Frame to be sent.
	 * @param canID The identifier of the CAN interface to send the given frame.
	 */
	public void updateCANFrame(String frame, int canID) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString(TAG_FRAME, frame);
		msg.setData(data);
		switch(canID) {
			case 0: // CAN0
				msg.what = ACTION_UPDATE_CAN0_FRAME;
				break;
			case 1: // CAN1
				msg.what = ACTION_UPDATE_CAN1_FRAME;
				break;
			default:
				break;
		}
		handler.sendMessage(msg);
	}

	/**
	 * Sends the given data to the provided CAN interface.
	 *
	 * @param mCan CAN interface to read from.
	 * @param idText Text field with the frame identifier.
	 * @param extIDCheckBox Checkbox field that specifies if extended ID is used.
	 * @param rtrCheckBox RTR checkbox field.
	 * @param data String with the data to send (with hexadecimal characters).
	 */
	private void handleSendButtonPressed(CAN mCan, EditText idText, CheckBox extIDCheckBox, CheckBox rtrCheckBox, String data) {
		// Read and check ID.
		if (!checkData(idText)) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Error: invalid ID. Enter an hexadecimal value.", Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		int idValue = editTextToInt(idText);
		if (idValue >= (1 << CANId.ID_EXTENDED_MAX_LENGTH)) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Error: invalid ID for data transmission.", Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		if (!extIDCheckBox.isChecked() && (idValue >= (1 << CANId.ID_MAX_LENGTH))) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Error: invalid ID. For standard mode, enter a hexadecimal value with a maximum length of "
							+ CANId.ID_MAX_LENGTH + " bits.",
					Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		sendFrame(mCan, idValue, extIDCheckBox.isChecked(), rtrCheckBox.isChecked(), data);
	}

	/**
	 * Writes the given frame to the provided CAN interface.
	 *
	 * @param mCan CAN interface to read from.
	 * @param idValue The value of the CAN frame identifier.
	 * @param isExtId {@code true} if the identifier is extended, {@code false} otherwise.
	 * @param isRtr {@code true} if sending a remote transmit request, {@code false} otherwise.
	 * @param data String with the data to send (with hexadecimal characters).
	 */
	private void sendFrame(CAN mCan, int idValue, boolean isExtId, boolean isRtr, String data) {
		CANId id = new CANId(idValue, isExtId);

		// Get data.
		byte[] txData = HexUtils.hexStringToByteArray(data.replace(" ", ""));

		// Write frame.
		CANFrame canFrame = new CANFrame(mCan, id, isRtr, false, txData);
		try {
			mCan.write(canFrame);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start/stop the CAN frames read process.
	 *
	 * @param mCan CAN interface to read from.
	 * @param idText Text field containing the CAN frame identifier.
	 * @param extIDCheckBox Checkbox field that specifies if extended ID is used.
	 * @param maskText Text field containing the mask.
	 */
	private void handleReadButtonPressed(CAN mCan, EditText idText, CheckBox extIDCheckBox, EditText maskText) {
		if (mCanReading[mCan.getInterfaceNumber()])
			stopReading(mCan, idText, extIDCheckBox, maskText);
		else
			startReading(mCan, idText, extIDCheckBox, maskText);
	}

	/**
	 * Stop the CAN read process.
	 *
	 * @param mCan CAN interface to stop reading.
	 * @param idText Text field containing the CAN frame identifier.
	 * @param extIDCheckBox Checkbox field that specifies if extended ID is used.
	 * @param maskText Text field containing the mask.
	 */
	private void stopReading(CAN mCan, EditText idText, CheckBox extIDCheckBox, EditText maskText) {
		// Update button.
		CANReadButtons[mCan.getInterfaceNumber()].setText(READ_DATA_MSG);
		mCanReading[mCan.getInterfaceNumber()] = false;

		// Update ID.
		idText.setEnabled(true);
		extIDCheckBox.setEnabled(true);
		maskText.setEnabled(true);

		// Stop current read process.
		try {
			mCan.stopRead();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start the CAN read process.
	 *
	 * @param mCan CAN interface to read from.
	 * @param idText Text field containing the CAN frame identifier.
	 * @param extIDCheckBox Checkbox field that specifies if extended ID is used.
	 * @param maskText Text field containing the mask.
	 */
	private void startReading(CAN mCan, EditText idText, CheckBox extIDCheckBox, EditText maskText) {
		// Read and check ID and Mask.
		if (!checkData(idText) || (editTextToInt(idText) >= (1 << CANId.ID_EXTENDED_MAX_LENGTH))) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Error: invalid ID. Enter a hexadecimal value with a maximum length of "
							+ CANId.ID_EXTENDED_MAX_LENGTH + " bits.",
					Toast.LENGTH_LONG);
			toast.show();
			return;
		}
		if (!extIDCheckBox.isChecked() && editTextToInt(idText) >= (1 << CANId.ID_MAX_LENGTH)) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Error: invalid ID. For standard mode, enter a hexadecimal value with a maximum length of "
							+ CANId.ID_MAX_LENGTH + " bits.", Toast.LENGTH_LONG);
			toast.show();
			return;
		}
		if (!checkData(maskText) || (editTextToInt(maskText) >= (1 << CANId.ID_EXTENDED_MAX_LENGTH))) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Error: invalid Mask. Enter a hexadecimal value with a maximum length of "
							+ CANId.ID_EXTENDED_MAX_LENGTH + " bits.",
					Toast.LENGTH_LONG);
			toast.show();
			return;
		}
		if (!extIDCheckBox.isChecked() && editTextToInt(maskText) >= (1 << CANId.ID_MAX_LENGTH)) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Error: invalid Mask. For standard mode, enter a hexadecimal value with a maximum length of "
							+ CANId.ID_MAX_LENGTH + " bits.", Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		// Clear data.
		clearReadData(mCan.getInterfaceNumber());

		// Update button.
		CANReadButtons[mCan.getInterfaceNumber()].setText(READ_BUTTON_MSG);
		mCanReading[mCan.getInterfaceNumber()] = true;

		// Update ID.
		idText.setEnabled(false);
		extIDCheckBox.setEnabled(false);
		maskText.setEnabled(false);

		// Create the filter list.
		// In this example, we only use one filter for simplicity, but the API allows a list.
		CANId canId = new CANId(editTextToInt(idText), extIDCheckBox.isChecked());
		CANFilter filter = new CANFilter(canId, editTextToInt(maskText));

		ArrayList<CANFilter> filterList = new ArrayList<>();
		filterList.add(filter);

		// Start read process.
		try {
			mCan.startRead(filterList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks the value in the given text field only contains hexadecimal
	 * characters.
	 *
	 * @param text The text field to check.
	 *
	 * @return {@code true} if the text field value only contains hexadecimal
	 *         characters, {@code false} otherwise.
	 */
	private boolean checkData(EditText text) {
		return text.getText().toString().toUpperCase().matches("[0-9A-F]+");
	}

	/**
	 * Clears all the received data fields of the given CAN interface.
	 *
	 * @param iFace CAN interface identifier to clear received data.
	 */
	private void clearReadData(int iFace) {
		if (iFace == 0) {
			for (EditText rx0Byte : rx0Bytes)
				rx0Byte.setText("");
		} else if (iFace == 1) {
			for (EditText rx1Byte : rx1Bytes)
				rx1Byte.setText("");
		}
		receivedIDLabels[iFace].setText("--------");
	}

	/**
	 * Converts the string in the given text field to an integer.
	 *
	 * @param text The text field to get the value.
	 *
	 * @return Integer value of the text field content.
	 */
	private int editTextToInt(EditText text) {
		// Using long because parsing into int causes a NumberFormatException
		// if value is greater than 0x80000000.
		long value = Long.parseLong(text.getText().toString(), 16);
		return (int) value & 0x7FFFFFFF;
	}

	/**
	 * Converts the given byte to an ASCII representation of its hexadecimal
	 * value.
	 *
	 * @param data The byte to convert.
	 *
	 * @return A ASCII string representing the hexadecimal value of {@code data}.
	 */
	private String byteToString(byte data) {
		return String.format("%02X", data);
	}
}