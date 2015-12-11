/**
 * Copyright (c) 2014-2015 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */

package com.digi.android.CAN;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.can.CAN;
import android.can.CANFrame;
import android.can.CANManager;
import android.can.ICANListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.NoSuchInterfaceException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

/**
 * CAN sample application.
 *
 * <p>This example monitors the communication in the two CAN interfaces using
 * the CAN API.</p>
 *
 * <p>For a complete description on the example, refer to the 'README.md' file
 * included in the example directory.</p>
 */
public class CANSample extends Activity implements ICANListener, OnClickListener {

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
	private EditText ID0_tx, ID0_rx, Mask0, ID1_tx, ID1_rx, Mask1;
	private CheckBox ExtIDTx0, RTR0, ExtIDRx0, ExtIDTx1, RTR1, ExtIDRx1;
	private EditText rx0_byte0, rx0_byte1, rx0_byte2, rx0_byte3, rx0_byte4, rx0_byte5, rx0_byte6, rx0_byte7;
	private EditText rx1_byte0, rx1_byte1, rx1_byte2, rx1_byte3, rx1_byte4, rx1_byte5, rx1_byte6, rx1_byte7;
	private TextView tx0Frame;
	private TextView tx1Frame;
	private TextView receivedID0, receivedID1;
	private Button CAN0_read;
	private Button CAN1_read;

	private CAN mCan0, mCan1;
	private CANFrame receivedFrame;
	private boolean mCan0Reading = false, mCan1Reading = false;

	private IncomingHandler handler = new IncomingHandler(this);

	/**
	 * Handler to manage UI calls from different threads.
	 */
	static class IncomingHandler extends Handler {
		private final WeakReference<CANSample> wActivity;

		IncomingHandler(CANSample activity) {
			wActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			CANSample activity = wActivity.get();

			if (activity == null)
				return;

			switch (msg.what) {
				case ACTION_DRAW_FRAME:
					// Check which interface is the owner of the received frame
					// (bear in mind that both interfaces might have set the same ID).
					// Besides, when checking the frame's ID, it's necessary to
					// filter out the flags (higher ID's bits).
					if (activity.receivedFrame.getInterfaceNumber() == 0) {
						activity.rx0_byte0.setText(
								activity.byteToString(activity.receivedFrame.getData()[0]));
						activity.rx0_byte1.setText(
								activity.byteToString(activity.receivedFrame.getData()[1]));
						activity.rx0_byte2.setText(
								activity.byteToString(activity.receivedFrame.getData()[2]));
						activity.rx0_byte3.setText(
								activity.byteToString(activity.receivedFrame.getData()[3]));
						activity.rx0_byte4.setText(
								activity.byteToString(activity.receivedFrame.getData()[4]));
						activity.rx0_byte5.setText(
								activity.byteToString(activity.receivedFrame.getData()[5]));
						activity.rx0_byte6.setText(
								activity.byteToString(activity.receivedFrame.getData()[6]));
						activity.rx0_byte7.setText(
								activity.byteToString(activity.receivedFrame.getData()[7]));
						// Filter out flags and show only ID bits.
						activity.receivedID0.setText(
								Integer.toHexString(activity.receivedFrame.getId() & 0x1FFFFFFF));
					}
					if (activity.receivedFrame.getInterfaceNumber() == 1) {
						activity.rx1_byte0.setText(
								activity.byteToString(activity.receivedFrame.getData()[0]));
						activity.rx1_byte1.setText(
								activity.byteToString(activity.receivedFrame.getData()[1]));
						activity.rx1_byte2.setText(
								activity.byteToString(activity.receivedFrame.getData()[2]));
						activity.rx1_byte3.setText(
								activity.byteToString(activity.receivedFrame.getData()[3]));
						activity.rx1_byte4.setText(
								activity.byteToString(activity.receivedFrame.getData()[4]));
						activity.rx1_byte5.setText(
								activity.byteToString(activity.receivedFrame.getData()[5]));
						activity.rx1_byte6.setText(
								activity.byteToString(activity.receivedFrame.getData()[6]));
						activity.rx1_byte7.setText(
								activity.byteToString(activity.receivedFrame.getData()[7]));
						// Filter out flags and show only ID bits.
						activity.receivedID1.setText(
								Integer.toHexString(activity.receivedFrame.getId() & 0x1FFFFFFF));
					}
					break;
				case ACTION_UPDATE_CAN0_FRAME:
					String frame = msg.getData().getString(TAG_FRAME);
					activity.tx0Frame.setText(frame);
					break;
				case ACTION_UPDATE_CAN1_FRAME:
					frame = msg.getData().getString(TAG_FRAME);
					activity.tx1Frame.setText(frame);
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
		ID0_tx = (EditText)findViewById(R.id.ID0_tx);
		ExtIDTx0 = (CheckBox)findViewById(R.id.ExtIDTx0);
		RTR0 = (CheckBox)findViewById(R.id.RTR0);
		tx0Frame = (TextView)findViewById(R.id.tx0_frame);
		Button editFrame0Button = (Button) findViewById(R.id.edit_frame0_button);
		editFrame0Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CANFrameDialog dialog = new CANFrameDialog(CANSample.this, tx0Frame.getText().toString(), 0);
				dialog.show();
			}
		});
		Button CAN0_send = (Button) findViewById(R.id.tx0_button);
		ID0_rx = (EditText)findViewById(R.id.ID0_rx);
		ExtIDRx0 = (CheckBox)findViewById(R.id.ExtIDRx0);
		Mask0 = (EditText)findViewById(R.id.Mask0);
		rx0_byte0 = (EditText)findViewById(R.id.rx0_byte0);
		rx0_byte1 = (EditText)findViewById(R.id.rx0_byte1);
		rx0_byte2 = (EditText)findViewById(R.id.rx0_byte2);
		rx0_byte3 = (EditText)findViewById(R.id.rx0_byte3);
		rx0_byte4 = (EditText)findViewById(R.id.rx0_byte4);
		rx0_byte5 = (EditText)findViewById(R.id.rx0_byte5);
		rx0_byte6 = (EditText)findViewById(R.id.rx0_byte6);
		rx0_byte7 = (EditText)findViewById(R.id.rx0_byte7);
		receivedID0 = (TextView)findViewById(R.id.ReceivedID0);
		CAN0_read = (Button)findViewById(R.id.rx0_button);

		ID1_tx = (EditText)findViewById(R.id.ID1_tx);
		ExtIDTx1 = (CheckBox)findViewById(R.id.ExtIDTx1);
		RTR1 = (CheckBox)findViewById(R.id.RTR1);
		tx1Frame = (TextView)findViewById(R.id.tx1_frame);
		Button editFrame1Button = (Button) findViewById(R.id.edit_frame1_button);
		editFrame1Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CANFrameDialog dialog = new CANFrameDialog(CANSample.this, tx1Frame.getText().toString(), 1);
				dialog.show();
			}
		});
		Button CAN1_send = (Button) findViewById(R.id.tx1_button);
		ID1_rx = (EditText)findViewById(R.id.ID1_rx);
		ExtIDRx1 = (CheckBox)findViewById(R.id.ExtIDRx1);
		Mask1 = (EditText)findViewById(R.id.Mask1);
		rx1_byte0 = (EditText)findViewById(R.id.rx1_byte0);
		rx1_byte1 = (EditText)findViewById(R.id.rx1_byte1);
		rx1_byte2 = (EditText)findViewById(R.id.rx1_byte2);
		rx1_byte3 = (EditText)findViewById(R.id.rx1_byte3);
		rx1_byte4 = (EditText)findViewById(R.id.rx1_byte4);
		rx1_byte5 = (EditText)findViewById(R.id.rx1_byte5);
		rx1_byte6 = (EditText)findViewById(R.id.rx1_byte6);
		rx1_byte7 = (EditText)findViewById(R.id.rx1_byte7);
		receivedID1 = (TextView)findViewById(R.id.ReceivedID1);
		CAN1_read = (Button)findViewById(R.id.rx1_button);

		// Show initial values.
		ID0_rx.setText(INIT_ID);
		Mask0.setText(INIT_MASK);
		ID0_tx.setText(INIT_ID);
		ID1_rx.setText(INIT_ID);
		Mask1.setText(INIT_MASK);
		ID1_tx.setText(INIT_ID);
		tx0Frame.setText(INIT_TX_FRAME);
		tx1Frame.setText(INIT_TX_FRAME);

		// Set event listeners for layout elements.
		CAN0_send.setOnClickListener(this);
		CAN0_read.setOnClickListener(this);
		CAN1_send.setOnClickListener(this);
		CAN1_read.setOnClickListener(this);

		// Get the CAN manager.
		CANManager canManager = (CANManager) getSystemService(CAN_SERVICE);

		// Initialize CAN objects.
		mCan0 = canManager.createCAN(0);
		mCan1 = canManager.createCAN(1);
	}

	@Override
	public void onPause() {
		super.onPause();

		// Stop receiving frames.
		try {
			mCan0.stopRead();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			mCan1.stopRead();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Unsubscribe listeners.
		mCan0.unsubscribeListener(this);
		mCan1.unsubscribeListener(this);

		// Close CAN interfaces.
		try {
			mCan0.close();
			mCan1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// Open CAN interfaces.
		try {
			mCan0.open();
			mCan1.open();
		} catch (NoSuchInterfaceException | IOException e) {
			e.printStackTrace();
		}

		// Subscribe listeners.
		mCan0.subscribeListener(this);
		mCan1.subscribeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tx0_button:
				sendFrame(mCan0, ID0_tx, ExtIDTx0, RTR0, tx0Frame.getText().toString());
				break;
			case R.id.rx0_button:
				readFrame(mCan0, ID0_rx, ExtIDRx0, Mask0, 0);
				break;
			case R.id.tx1_button:
				sendFrame(mCan1, ID1_tx, ExtIDTx1, RTR1, tx1Frame.getText().toString());
				break;
			case R.id.rx1_button:
				readFrame(mCan1, ID1_rx, ExtIDRx1, Mask1, 1);
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
	 * Writes the given frame to the provided CAN interface.
	 *
	 * @param mCan CAN interface to read from.
	 * @param ID Text field with the frame identifier.
	 * @param ExtID Checkbox field that specifies if extended ID is used.
	 * @param RTR RTR checkbox field.
	 * @param frame The frame to send.
	 */
	private void sendFrame(CAN mCan, EditText ID, CheckBox ExtID, CheckBox RTR, String frame) {
		// Read and check ID.
		if (!checkData(ID)) {
			Toast toast = Toast.makeText(getApplicationContext(), "Error: invalid ID. Enter an hexadecimal value.", Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		int id = editTextToInt(ID);
		if (id >= (1 << 29)) {
			Toast toast = Toast.makeText(getApplicationContext(), "Error: invalid ID for data transmission.", Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		if (!ExtID.isChecked() && (id >= (1 << 11))) {
			Toast toast = Toast.makeText(getApplicationContext(), "Error: invalid ID. For standard mode, enter a hexadecimal value with a maximum length of 11 bits.", Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		byte[] tx_data = HexUtils.hexStringToByteArray(frame.replace(" ", ""));

		try {
			mCan.write(id, ExtID.isChecked(), RTR.isChecked(), tx_data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start/stop the CAN frames read process.
	 *
	 * @param mCan CAN interface to read from.
	 * @param id Text field containing the CAN frame identifier.
	 * @param ExtID Checkbox field that specifies if extended ID is used.
	 * @param mask Text field containing the mask.
	 * @param iFace The identifier the CAN interface to read from.
	 */
	private void readFrame(CAN mCan, EditText id, CheckBox ExtID, EditText mask, int iFace) {
		if (((iFace == 0) && mCan0Reading)
				|| ((iFace == 1) && mCan1Reading)) {
			// Update button.
			if (iFace == 0) {
				CAN0_read.setText(READ_DATA_MSG);
				mCan0Reading = false;
			} else {
				CAN1_read.setText(READ_DATA_MSG);
				mCan1Reading = false;
			}

			// Update ID.
			id.setEnabled(true);
			ExtID.setEnabled(true);
			mask.setEnabled(true);

			// Stop current read process.
			try {
				mCan.stopRead();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// Read and check ID and Mask.
			if (!checkData(id) || (editTextToInt(id) >= (1 << 29))) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Error: invalid ID. Enter a hexadecimal value with a maximum length of 29 bits.", Toast.LENGTH_LONG);
				toast.show();
				return;
			}
			if (!ExtID.isChecked() && editTextToInt(id) >= (1 << 11)) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Error: invalid ID. For standard mode, enter a hexadecimal value with a maximum length of 11 bits.", Toast.LENGTH_LONG);
				toast.show();
				return;
			}
			if (!checkData(mask) || (editTextToInt(mask) >= (1 << 29))) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Error: invalid Mask. Enter a hexadecimal value with a maximum length of 29 bits.", Toast.LENGTH_LONG);
				toast.show();
				return;
			}
			if (!ExtID.isChecked() && editTextToInt(mask) >= (1 << 11)) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Error: invalid Mask. For standard mode, enter a hexadecimal value with a maximum length of 11 bits.", Toast.LENGTH_LONG);
				toast.show();
				return;
			}

			// Clear data.
			clearReadData(iFace);

			// Update button.
			if (iFace == 0) {
				CAN0_read.setText(READ_BUTTON_MSG);
				mCan0Reading = true;
			} else if (iFace == 1) {
				CAN1_read.setText(READ_BUTTON_MSG);
				mCan1Reading = true;
			}

			// Update ID.
			id.setEnabled(false);
			ExtID.setEnabled(false);
			mask.setEnabled(false);

			// Start read process.
			// In this example, we only use one ID/Mask for simplicity, but the API allows an array.
			try {
				int[] ids = new int[]{editTextToInt(id)};
				boolean[] extIDs = new boolean[]{ExtID.isChecked()};
				int[] masks = new int[]{editTextToInt(mask)};
				mCan.startRead(ids, extIDs, masks);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			rx0_byte0.setText("");
			rx0_byte1.setText("");
			rx0_byte2.setText("");
			rx0_byte3.setText("");
			rx0_byte4.setText("");
			rx0_byte5.setText("");
			rx0_byte6.setText("");
			rx0_byte7.setText("");
			receivedID0.setText("--------");
		} else if (iFace == 1) {
			rx1_byte0.setText("");
			rx1_byte1.setText("");
			rx1_byte2.setText("");
			rx1_byte3.setText("");
			rx1_byte4.setText("");
			rx1_byte5.setText("");
			rx1_byte6.setText("");
			rx1_byte7.setText("");
			receivedID1.setText("--------");
		}
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