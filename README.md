Android-Mobile-Biofeedback
==========================
This project is a simple demonstration of a biofeedback application on a mobile tablet running Android, utilizing the usb port and the USB Host API that exposes the port's read/write capabilities.

It was designed specifically for pelvic floor biofeedback, but can be used in any situation requiring biofeedback.

We've incorporated two third-party libraries for the project.  USB-serial-for-Android, which uses the USB Host API to expose usb endpoints on the tablet to allow basic read/write capabilities, and GraphView, which is a library for Android that allows creation of graphs and diagrams.  Most of the tablet side software is based on these two libraries, and we used the sample application from USB-serial-for-Android as the skeleton for our application.  They are both great libraries - we highly recommend them.

Although we think it is a cool idea, we don't recommend using this in clinical situations - nothing here has been FDA approved, so you have been forwarned.

You can clone this into Eclipse, and then run the application on one of several devices, a list of which we will provide soon and will hopefully grow.

We will also give a detailed list of the hardware needed to complete the project - all coming soon!
