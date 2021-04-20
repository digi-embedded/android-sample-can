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
The example is already configured, so all you need to do is to build and
launch the project.

By default, both CAN interfaces run at a bitrate of 500kbps.

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
* ConnectCore 6 SBC v3
* ConnectCore 8X SBC Pro

License
---------

Copyright (c) 2014-2021, Digi International Inc. <support@digi.com>

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.