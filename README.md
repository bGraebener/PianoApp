# PianoApp
>Title: Piano Application using LEAP Motion  
Team: Krisztián Nagy, Bastian Graebener  
Module: Gesture Based UI Development  
Lecturer: Damien Costello  

## 1. Introduction

This projects aim is to turn the LEAP Motion controller into a MIDI controller.   
The application will allow the user to play a virtual MIDI instrument using the LEAP Motion controller.
The controller picks up finger tap gestures and the data from the controller
is converted into midi note on event. This event is sent to
a virtual midi driver which in turn will control a virtual instrument.

The rationale to use MIDI instead of audio was to keep flexibility as to what instrument the LEAP Motion controller can
be used for. It is left to the user to decide what sounds to produce with the LEAP Motion controller.
 
This readme explains the architecture of the application and discusses strengths and weaknesses of both the software and the hardware.

## 2. MIDI
MIDI is an abbreviation for Musical Instrument Digital Interface. MIDI is a data transfer protocol commonly used for
connecting computers with electronic musical or audio devices. [1]    

In order for the LEAP Motion controller to be used as a MIDI device a MIDI driver is needed. MIDI drivers enable an
operating system to recognise a device as being able to send and receive data using the MIDI protocol.  
The MIDI driver is then used to route the data between devices and applications [2].

Real MIDI interfaces like MIDI keyboards, drums or other MIDI controllers come with a driver specific to the controller 
provided by the manufacturer. 
 
Since the LEAP Motion controller was not intended to be used as a MIDI controller and therefore does not provide a 
MIDI driver, a solution had to be found to enable the operating system and applications like a virtual instrument
to recognise the LEAP Motion controller as a device capable of sending MIDI data.  

The application [**loopMidi**](https://www.tobias-erichsen.de/software/loopmidi.html) creates a virtual MIDI driver that
can be used by applications to communicate using the MIDI protocol.  This virtual MIDI driver can be targeted by both, the 
application that sends MIDI data and by the virtual MIDI instrument that receives MIDI data. A virtual MIDI port called 
__'PianoApp'__  is created in **loopMidi**. 

There are several different types of messages that can be send over MIDI. For the purpose of this application, only
'Note On' and 'Note Off' messages were used [3]. 'Note On' and 'Note Off' events consist both of three bytes. The first byte
is called header byte and contains the MIDI channel number. The last two bytes are the so-called data-bytes. The first 
data-byte is the key-number, i.e the number of the note to be played. The last byte is the velocity which specifies 
the force a note was played.

### 2.1 MIDI in the application
The application was written in Java using the language version 8. The Java core library has good support for working
with MIDI [4].

The MidiController class handles all MIDI related operations. 

The first step in the initialisation is to search the for the MIDI driver in the system with the name '__PianoApp'__. Once
the application found the driver, it attempts to open it and retrieve the MIDI receiver from it. The MIDI receiver is used to send 
'MIDI Note On' events.

The sending of the 'Note On' and 'Note Off' messages is done in the sendMessage() method of the MidiController class.
A MIDI message is created from the note value that was passed and a moderate velocity of 93 was set. That message
is then send to the MIDI receiver. 

In a second step a new thread is created and that thread is put to sleep for one second. After a second of pausing,
a MIDI 'Note Off' message is send to the receiver to signal the virtual instrument that the key was released.

All messages are send to the MIDI channel 1.
 
### 2.2 The virtual Instrument
For the application that receives the MIDI messages and plays the actual sounds, we used the free 
[__Virtual MIDI Piano Keyboard__](http://vmpk.sourceforge.net/) application to test the LEAP Motion MIDI controller application. 

In the MIDI settings the channel was set to 1. As the MIDI 'In Driver' the 'PianoApp' driver was selected. The MIDI 'Out
Driver' was set to the 'Windows Wavetable Synth' driver. 

The application is not restricted to work with only this virtual instrument. Any virtual instrument application or 
VST plugin can be used to produce sound. The virtual instrument must listen on channel 1 and being able to recognise
the 'PianoApp' MIDI driver. The application was also tested with the commercial application 
[__EzKeys__](https://www.toontrack.com/ezkeys-line/) and worked as expected.

### 2.3 MIDI specific Issues 
With the LEAP Motion controller there is no way of specifying when to stop playing a key, since there is no 
'finger-released' event hook only a 'finger-tap' event. To signal the virtual instrument that a note has ended, 
a 'MIDI Note Off' message has to be send. 

For this application we hard-coded a value of one second for all note's duration. The turning off of a note is done on a
different thread which is put to sleep for a second and then sends a MIDI 'Note Off' message to the receiver. This is
done to allow the user to keep pressing keys while another note is still playing.

Another problem we encountered, was using the LEAP Motion controller to register the velocity of a 'finger-tap' event which
could have been used to set the velocity in the MIDI message. There is no easy way to track fingers on the z-axis and 
the speed in which the finger moved. To counter this issue,  we hard-coded a moderate velocity for all 'Note On' events 
with the value of 93.

## Technologies used:
    1. LEAP Motion controller
    2. loopMidi, a virtual midi driver application , see https://www.tobias-erichsen.de/software/loopmidi.html
    3. Tiny Virtual Piano, virtual piano instrument 
   

## Work done so far:
    - installed loopMidi and created virtual midi driver
    - connected virtual piano to midi driver
    - programmed small java application to connect to midi driver and send midi note on and note off 
      events to the virtual piano programatically
    - connected to leap motion device to recognize hands

## Existance of lib and native folders
Leap Motion does not support Java anymore and has removed every easily accessible documentation and source. 

It is not easy to find documentation how to set up a java project and where to get the needed jar and native files. Therefore I decided to include them in the repository. They are under free licence and can be optained from [Leap Motion's website](https://www.leapmotion.com/setup/desktop/windows/) in a complicated way.

## References
1. [MIDI Standard](https://www.midi.org/specifications)
1. [MIDI Driver Explanation](https://www.sweetwater.com/insync/midi-driver/)
1. [MIDI Note On, Note Off Messages](http://tonalsoft.com/pub/pitch-bend/pitch.2005-08-31.17-00.aspx)
1. [Java Midi Package](https://docs.oracle.com/javase/tutorial/sound/overview-MIDI.html)
