Android-Mobile-Biofeedback
==========================

This is an Android-based mobile application that allows 
the user to read an input signal from an EMG signal capture 
device and represent it in real-time on screen.

It was designed as part of a project to create a mobile alternative
to hospital-based biofeedback equipment, but it is definitely NOT
for medical use.  Although the risk of injury is probably really low, this
hasn't been tested on humans, so USE AT YOUR OWN RISK

THE IDEA
============================

Biofeedback is a decades-old method used to help retrain certain types
of behaviors under both conscious and unconscious control.  In its simplest
form, biofeedback is just an electromyograph, or a muscle tracing.
And that's what this application reads - a simple emg of any muscle group you
want

THE HARDWARE
============================
The EMG capture device:
1 Differential voltage sensor
1 preamplifier (captures voltage signal and amplifies it - also helps if it smooths it out)
1 Analog to digital converter - I used the Arduino Mega ADK, but any board with a CDC/ACM 
  serial driver will work
1 Micro USB OTG to USB 2.0 Adapter 
1 USB 2.0 A male to USB 2.0 B male cable
Jumper cables


THE SOFTWARE
============================
Aside from writing a simple sketch to write to the appropriate output pin on 
the Arduino, there's no software for the EMG capture device.  However, the 
Android application requires both the usb-serial-for-android library (which works with
CDC/ACM virtual serial port on Arduino) as well as Graphview, an
Android-based library that lets the application draw real time graphs 
of the EMG signal.

This ultimately results in a skin resolution of 10 microvolts, but this can 
be increased simply by increasing your processor bit count

Author
=============================
Android-Mobile-Biofeedback is an application written by jeremy wiygul

