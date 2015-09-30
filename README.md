CAN Sample Application
======================

This application demonstrates the usage of the CAN API by monitoring the 
communications in the two CAN interfaces while allowing the user to configure
some parameters related to transmission and reception.

Demo requeriments
-----------------

To run this example you will need:
    - One compatible device to host the application.
    - Network connection between the device and the host PC in order to
      transfer and launch the application.
    - Establish remote target connection to your Digi hardware before running
      this application.
    - CAN cable to connect the Digi device to a CAN device or host PC with a 
	  CAN protocol interpreter. Alternatively, the two CAN ports in the device 
	  could be connected together to run this sample application.

Demo setup
----------

Make sure the hardware is set up correctly:
    - The device is powered on.
    - The device is connected directly to the PC or to the Local
      Area Network (LAN) by the Ethernet cable.
    - The CAN interfaces are properly connected.

Demo run
--------

Previously to the application execution, the CAN interface bitrate has to be
configured. By default, it is set to 500 kbps.

If other bitrate is desired, it can be set by execution of the init.can.sh file 
from the target's console with the following command:
       # . /etc/init.can.sh {CAN0_BITRATE} {CAN1_BITRATE}

The bitrate that shouldn't be changed should be set to 0. Here are some 
examples:
       # . /etc/init.can.sh 125000 125000
       # . /etc/init.can.sh 250000 0

In the first example both CAN interface are set to 125K baud and in the second 
only CAN0 will be changed CAN1 will be unchnaged using the default baud rate. 
Then the application can be launched and the CAN interface will use the setup 
baud rate. The baud rate is persistent through reboots of the target and the 
CAN interface will be configured with the latest baud rate setup by the script.

While it is running, the application will display two separate areas 
corresponding to the two CAN interfaces available in the device. Within each 
area, transmission and reception functionalities are clearly separated.

Transmission allows setting the device ID and the 8-byte data to be sent. 
Pushing the "SEND DATA" button will start the transmission of the CAN frame.

Reception allows the configuration of the device ID. Once "READ DATA" button is 
pressed, the device will wait until a CAN frame matching the ID is received. 
This listening process can be stopped by pressing the button again.

Tested on
---------

ConnectCore Wi-i.MX53
ConnectCard for i.MX28
ConnectCore 6 Adapter Board
ConnectCore 6 SBC
ConnectCore 6 SBC v2