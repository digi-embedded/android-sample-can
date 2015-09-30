package com.example.android.CAN;

import java.io.IOException;
import android.app.Activity;
import android.can.CAN;
import android.can.CANFrame;
import android.can.CANListener;
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

public class CANSample extends Activity implements CANListener, OnClickListener {

	// Constants
	private final static String TAG = "CANSample";
	private final static int ACTION_DRAW_FRAME = 0;
	private final static int ACTION_UPDATE_CAN0_FRAME = 1;
	private final static int ACTION_UPDATE_CAN1_FRAME = 2;
	private final static String TAG_FRAME = "frame";
	private final static String readButtonMessage = "LISTENING - PUSH TO STOP";

	// Variables
	private EditText ID0_tx, ID0_rx, Mask0, ID1_tx, ID1_rx, Mask1;
	private CheckBox ExtIDTx0, RTR0, ExtIDRx0, ExtIDTx1, RTR1, ExtIDRx1;
	private EditText rx0_byte0, rx0_byte1, rx0_byte2, rx0_byte3, rx0_byte4, rx0_byte5, rx0_byte6, rx0_byte7;
	private EditText rx1_byte0, rx1_byte1, rx1_byte2, rx1_byte3, rx1_byte4, rx1_byte5, rx1_byte6, rx1_byte7;
	private TextView tx0Frame;
	private TextView tx1Frame;
	private TextView receivedID0, receivedID1;
	private Button CAN0_send, CAN0_read, CAN1_send, CAN1_read, editFrame0Button, editFrame1Button;
	CAN mCan0, mCan1;
	private CANFrame receivedFrame;
	private boolean mCan0Reading = false, mCan1Reading = false;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ACTION_DRAW_FRAME:
				// Check which interface is the owner of the received frame (bear in mind that both interfaces might have set the same ID).
				// Besides, when checking the frame's ID, it's necessary to filter out the flags (higher ID's bits).
				if ((receivedFrame.iface) == 0) {
					rx0_byte0.setText(byteToString(receivedFrame.data[0]));
					rx0_byte1.setText(byteToString(receivedFrame.data[1]));
					rx0_byte2.setText(byteToString(receivedFrame.data[2]));
					rx0_byte3.setText(byteToString(receivedFrame.data[3]));
					rx0_byte4.setText(byteToString(receivedFrame.data[4]));
					rx0_byte5.setText(byteToString(receivedFrame.data[5]));
					rx0_byte6.setText(byteToString(receivedFrame.data[6]));
					rx0_byte7.setText(byteToString(receivedFrame.data[7]));
					// Filter out flags and show only ID bits
					receivedID0.setText(Integer.toHexString(receivedFrame.id & 0x1FFFFFFF));
				}
				if ((receivedFrame.iface) == 1) {
					rx1_byte0.setText(byteToString(receivedFrame.data[0]));
					rx1_byte1.setText(byteToString(receivedFrame.data[1]));
					rx1_byte2.setText(byteToString(receivedFrame.data[2]));
					rx1_byte3.setText(byteToString(receivedFrame.data[3]));
					rx1_byte4.setText(byteToString(receivedFrame.data[4]));
					rx1_byte5.setText(byteToString(receivedFrame.data[5]));
					rx1_byte6.setText(byteToString(receivedFrame.data[6]));
					rx1_byte7.setText(byteToString(receivedFrame.data[7]));
					// Filter out flags and show only ID bits
					receivedID1.setText(Integer.toHexString(receivedFrame.id & 0x1FFFFFFF));
				}
				break;
			case ACTION_UPDATE_CAN0_FRAME:
				String frame = msg.getData().getString(TAG_FRAME);
				tx0Frame.setText(frame);
				break;
			case ACTION_UPDATE_CAN1_FRAME:
				frame = msg.getData().getString(TAG_FRAME);
				tx1Frame.setText(frame);
				break;
			}
		}
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Instance the elements from layout
        ID0_tx = (EditText)findViewById(R.id.ID0_tx);
        ExtIDTx0 = (CheckBox)findViewById(R.id.ExtIDTx0);
        RTR0 = (CheckBox)findViewById(R.id.RTR0);
        tx0Frame = (TextView)findViewById(R.id.tx0_frame);
        editFrame0Button = (Button)findViewById(R.id.edit_frame0_button);
        editFrame0Button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CANFrameDialog dialog = new CANFrameDialog(CANSample.this, tx0Frame.getText().toString(), 0);
				dialog.show();
			}
		});
        CAN0_send = (Button)findViewById(R.id.tx0_button);
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
        editFrame1Button = (Button)findViewById(R.id.edit_frame1_button);
        editFrame1Button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CANFrameDialog dialog = new CANFrameDialog(CANSample.this, tx1Frame.getText().toString(), 1);
				dialog.show();
			}
		});
        CAN1_send = (Button)findViewById(R.id.tx1_button);
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

        // Show initial values
        ID0_rx.setText("00000000");
        Mask0.setText("00000000");
        ID0_tx.setText("00000000");
        ID1_rx.setText("00000000");
        Mask1.setText("00000000");
        ID1_tx.setText("00000000");
        tx0Frame.setText("00 00 00 00 00 00 00 00");
        tx1Frame.setText("00 00 00 00 00 00 00 00");

        // Set event listeners for layout elements
        CAN0_send.setOnClickListener(this);
        CAN0_read.setOnClickListener(this);
        CAN1_send.setOnClickListener(this);
        CAN1_read.setOnClickListener(this);

        // Initialize CAN objects
        mCan0 = new CAN(0);
        mCan1 = new CAN(1);
    }

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
		}
	}

	private void sendFrame (CAN mCan, EditText ID, CheckBox ExtID, CheckBox RTR, String frame) {
		// Read and check ID
		if (!checkData(ID)) {
			Toast toast = Toast.makeText(getApplicationContext(), "Error: invalid ID. Enter a hexadecimal value.", Toast.LENGTH_LONG);
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
		} catch (IOException e) { }
	}

	private void readFrame (CAN mCan, EditText id, CheckBox ExtID, EditText mask, int iface) {
		if (((iface == 0) && (mCan0Reading)) || ((iface == 1) && (mCan1Reading))) {
			// Update button
			if (iface == 0) {
				CAN0_read.setText("READ DATA");
				mCan0Reading = false;
			} else if (iface == 1) {
				CAN1_read.setText("READ DATA");
				mCan1Reading = false;
			}

			// Update ID
			id.setEnabled(true);
			ExtID.setEnabled(true);
			mask.setEnabled(true);

			// Stop current read process
			try {
				mCan.stopRead();
			} catch (IOException e) { }
		} else {
			// Read and check ID and Mask
			if (!checkData(id) || (editTextToInt(id) >= (1 << 29))) {
				Toast toast = Toast.makeText(getApplicationContext(), "Error: invalid ID. Enter a hexadecimal value with a maximum length of 29 bits.", Toast.LENGTH_LONG);
				toast.show();
				return;
			}
			if (!ExtID.isChecked() && editTextToInt(id) >= (1 << 11)) {
				Toast toast = Toast.makeText(getApplicationContext(), "Error: invalid ID. For standard mode, enter a hexadecimal value with a maximum length of 11 bits.", Toast.LENGTH_LONG);
				toast.show();
				return;				
			}
			if (!checkData(mask) || (editTextToInt(mask) >= (1 << 29))) {
				Toast toast = Toast.makeText(getApplicationContext(), "Error: invalid Mask. Enter a hexadecimal value with a maximum length of 29 bits.", Toast.LENGTH_LONG);
				toast.show();
				return;
			}
			if (!ExtID.isChecked() && editTextToInt(mask) >= (1 << 11)) {
				Toast toast = Toast.makeText(getApplicationContext(), "Error: invalid Mask. For standard mode, enter a hexadecimal value with a maximum length of 11 bits.", Toast.LENGTH_LONG);
				toast.show();
				return;				
			}

			// Clear data
			clearReadData(iface);

			// Update button
			if (iface == 0) {
				CAN0_read.setText(readButtonMessage);
				mCan0Reading = true;
			} else if (iface == 1) {
				CAN1_read.setText(readButtonMessage);
				mCan1Reading = true;
			}

			// Update ID
			id.setEnabled(false);
			ExtID.setEnabled(false);
			mask.setEnabled(false);

			// Start read process. In this example, we only use one ID/Mask for simplicity, but the API allows an array.
			try {
				int[] ids = new int[]{editTextToInt(id)};
				boolean[] extIDs = new boolean[]{ExtID.isChecked()};
				int[] masks = new int[]{editTextToInt(mask)};
				mCan.startRead(ids, extIDs, masks);
			} catch (IOException e) { }
		}
	}

	public void frameReceived(CANFrame frame) {
		receivedFrame = frame;
		handler.sendEmptyMessage(ACTION_DRAW_FRAME);
	}

	private boolean checkData (EditText text) {
		return text.getText().toString().toUpperCase().matches("[0-9A-F]+");
	}

	private void clearReadData (int iface) {
		if (iface == 0) {
	        rx0_byte0.setText("");
	        rx0_byte1.setText("");
	        rx0_byte2.setText("");
	        rx0_byte3.setText("");
	        rx0_byte4.setText("");
	        rx0_byte5.setText("");
	        rx0_byte6.setText("");
	        rx0_byte7.setText("");
	        receivedID0.setText("--------");
		} else if (iface == 1) {
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

	private byte editTextToByte (EditText text) {
		String str = new String(text.getText().toString());
        return (byte)(Integer.parseInt(str, 16) & 0xff);
	}

	private int editTextToInt (EditText text) {
		// Using long because parsing into int causes a NumberFormatException if value is greater than 0x80000000 
		long value = Long.parseLong(text.getText().toString(), 16);
		return (int) value & 0x7FFFFFFF;		
	}

	private String byteToString (byte data) {
		return String.format("%02X", data);
	}

    public void onPause() {
    	super.onPause();

    	// Stop receiving frames
    	try {
			mCan0.stopRead();
			mCan1.stopRead();
		} catch (IOException e) {}

    	// Unsubscribe listeners
    	mCan0.unsubscribeListener(this);
    	mCan1.unsubscribeListener(this);

    	// Close CAN interfaces
    	mCan0.close();
    	mCan1.close();
    }

    public void onResume() {
        super.onResume();

        // Open CAN interfaces
        mCan0.open();
        mCan1.open();

        // Subscribe listeners
        mCan0.subscribeListener(this);
        mCan1.subscribeListener(this);
    }
    
    public void updateCANFrame(String frame, int canID) {
    	Message msg = new Message();
    	Bundle data = new Bundle();
    	data.putString(TAG_FRAME, frame);
    	msg.setData(data);
    	switch(canID) {
    	case 0:
    		msg.what = ACTION_UPDATE_CAN0_FRAME;
    		break;
    	case 1:
    		msg.what = ACTION_UPDATE_CAN1_FRAME;
    		break;
    	}
    	handler.sendMessage(msg);
    }
}