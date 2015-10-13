CAN Sample Application
======================

This application demonstrates the usage of the CAN API by monitoring the 
communications in the two CAN interfaces while allowing the user to configure
some parameters related to transmission and reception.

Demo requirements
-----------------

To run this example you need:

* One compatible device to host the application.
* A USB connection between the device and the host PC in order to transfer and
  launch the application.
* CAN cable to connect the Digi device to a CAN device or host PC with a 
  CAN protocol interpreter. Alternatively, the two CAN ports in the device 
  could be connected together to run this sample application.

Demo setup
----------

Make sure the hardware is set up correctly:

1. The device is powered on.
2. The device is connected directly to the PC by the micro USB cable.
3. The CAN interfaces are properly connected.

Demo run
--------

Before executing the application, the CAN interface bitrate needs to be
configured. By default, it is set to 500 kbps.

If other bitrate is desired, set it by executing the `init.can.sh` script from
the device's console with the following command:

    # . /etc/init.can.sh {CAN0_BITRATE} {CAN1_BITRATE}

Use '0' to maintain the default bitrate.

Here are some examples:

    # . /etc/init.can.sh 125000 125000
    # . /etc/init.can.sh 250000 0

In the first example, both CAN interfaces are set to 125K baud. In the second
example only CAN0 bitrate is changed, CAN1 uses the default baud rate.

Once bitrates are properly set, launch the application. CAN interfaces use the
configured baud rate. Baud rate is persistent through device reboots, CAN
interfaces are configured with the latest baud rate setup by the `init.can.sh`
script.

The application displays an area for each CAN interface available in the device.
Within each area, transmission and reception functionality are clearly
separated.

Transmission allows setting the device ID and the 8-byte data to send.
Click **SEND DATA** to start the transmission of the CAN frame.

Reception allows the configuration of the device ID. Click **READ DATA**  
to wait for the reception of a CAN frame with a matching ID. Click the button 
again to stop this listening process.

Compatible with
---------------

* ConnectCore 6 SBC
* ConnectCore 6 SBC v2

License
---------

This software is open-source software. Copyright Digi International, 2014-2015.

This Source Code Form is subject to the terms of the Mozilla Public License,
v. 2.0. If a copy of the MPL was not distributed with this file, you can obtain
one at http://mozilla.org/MPL/2.0/.