# PianoApp
>Title: Piano Application using LEAP Motion  
Team: Krisztián Nagy, Bastian Graebener

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
    

In order for the LEAP Motion controller to be used as a MIDI device a MIDI driver is needed. The MIDI drivers enable an
operating system to recognise a device as a device able to send and receive data using the MIDI protocol.  
The MIDI driver is used to route the data between devices and applications [2].

Real MIDI interfaces like MIDI keyboards, drums or other MIDI controllers come with a driver specific to the controller 
provided by the manufacturer. 
 
Since the LEAP Motion controller was not intended to be used as a MIDI controller and therefore does not provide a 
MIDI driver, a solution had to be found to enable the operating system and applications like a virtual instrument
 to recognise the LEAP Motion controller as a device capable of sending MIDI data.    

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
1. [MIDI Driver](https://www.sweetwater.com/insync/midi-driver/)