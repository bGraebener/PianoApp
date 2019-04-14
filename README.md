# PianoApp
>Title: Piano Application using LEAP Motion  
Team: Krisztián Nagy, Bastian Graebener  
Module: Gesture Based UI Development  
Lecturer: Damien Costello  
[Video:](https://drive.google.com/file/d/10eEI6MD_D3n8gAhEU9okkaMCSaRAwmZa/view?usp=sharing)

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

### 3 Leap Motion
Leap Mottion is a device capable of detecting hands and motions of the figures. The device have two built in cameras. These cameras can detect hands above the device. The device is also capable of detecting joints and bones of the fingers on both left and right hands. There is also a possibility to detect the rotation of the hand and various gestures. 

Gestures are a built in feature up to version 3.2. These gestures can be:
* Key Tap
* Swipe
* Circle
### 3.2 Leap Motion in the application
The application relies on Leap Motion version 3.2. The reason to choose and outdated version of Leap Motion development kit is that, 3.2 is the last version to support Java and gestures. Both the gestures and Java was removed from subsequent versions.

The application uses windows native dll files of the Leap Motion's development kit. These files are loaded into Java's system path. A JAR file is also loaded in. This JAR file provides the API for Leap Motion and relies on the native files.

### 3.3 Leap Miton gesture detection
Leap Motion library contains a Controller class which must be instantiated in order to connect to the device. This class can take a set of configuration parameters and a listener for frame changes. The configuration modified in the application is to allow the key tap gesture and to make the gesture detection more suitable for this application.

The listener passed to the controller is an extended version of the original listener in the Leap Motion library. This listener receives updates on every frame change. When a frame changes a custom code checks if the change includes a key tap gesture. If the key tap gesture is present a new gesture is instantiated which then provides a finger from the pointable included in the key tap gesture. This finger is used to determine the fingertip's position.

The listener was designed to allow registration specifically for a key tap event. When a key tap occours the registered key tap listener is notified with the figertip's position form the key tap event. This position is used to determine which key was tapped. 

The keys settings comes from either the default settings or as command line arguments. By default the application is set up to detect the key taps within 300px on the X axis of the device. The device detects this three axes from -150 to 150. This range is devided by the amount of the keys. By default the number of the keys is 12, which is an octet on a piano. The 12 keys over 300px gives 25px for each keys which is about 2-2.5cm on the device's scale. This has been tested with smaller and larger keys. 12 keys over 300px proved to be the most optimal as the hand can be placed 15cm above the device and the taps can be detected moving left and right about 2.5cm.

When a key tap event is received the determined key is passed to the `MidiController` class which is described above in detail.

### 3.4 Difficulties with Leap Motion
Leap Motion does not support Java anymore. Therefore, the developers were required to source an older version of SDK. This proved to be difficult as Leap Motion's website removed the direct references to Java documentation and for older versions. The needed version and sdk was required with the aid of Google, which eventually lead back to Leap Motion archive section.

The lack of documentation was a setback in the beggining as the developers had to experiment with the device to determine how it works and how this application can be implemnted.

The accuracy of the device is another large issues. The tapped position returned by the key tap gesture is inaccurate and inconsistent. Therefore, it was impossible to determine which key was tapped or if it was a key that was tapped. This issue is solved by getting the distal bone's position from the key tap gesture. The distal bone's position is the same as the finger tip's position. This position is proven to be accurate and consistent.

The hardver does not always work as it is intended to. Two devices were used during development. The first devcie had a shifted X axis, meaning the hands had to be positioned beside the device 
## 4 Existance of lib and native folders
Leap Motion does not support Java anymore and has removed every easily accessible documentation and source. 

It is not easy to find documentation how to set up a java project and where to get the needed jar and native files. Therefore I decided to include them in the repository. They are under free licence and can be optained from [Leap Motion's website](https://www.leapmotion.com/setup/desktop/windows/) in a complicated way.

## 5 Possibilities to extend
The application can be turned to any kind of instrument which works with taps. For example if only two keys are defined at start up and a vrtual bongo is loaded in, the application can be used as a bongo, opposed to the original piano. 

The sweipe descute could be implemented to use to shift the octets on the piano. This requires the developers to detect the direction of the swipe and limit the amout of swipe gestures. This is needed as the SDK returns a set of swipe gestures opposed to the key tap gesture when only one gesture is produced.

## 6 How to run
1. Install Maven
1. Clone this appllication
1. Run `mvn install` in the cloned appllication's folder
1. The above command will output a JAR file with dependncies included. The file is in `target` folder.
1. Open loopMidi and create a controller named _PianoApp_
1. Open the virtual piano and in the settings set _PianoApp_ as controller
1. Run `java -jar pianoapp-1.0-SNAPSHOT-jar-with-dependencies.jar`
1. The aaplication will write out _Connected_ on successful run. The key taps are detected until the application is terminated

### 6.1 Running parameters
* `-r` denottes the range detected. Default is 150. The range is always duplicated. Therefore 150 will detect 300px.
* `-k` denotes the number of the keys. Default is 12.
* `-s` denotes the start key for the virtual piano. Default is 59




Example run:
```
java -jar pianoapp-1.0-SNAPSHOT-jar-with-dependencies.jar -r 150 -k 12-s 59
```




## References
1. [MIDI Standard](https://www.midi.org/specifications)
1. [MIDI Driver Explanation](https://www.sweetwater.com/insync/midi-driver/)
1. [MIDI Note On, Note Off Messages](http://tonalsoft.com/pub/pitch-bend/pitch.2005-08-31.17-00.aspx)
1. [Java Midi Package](https://docs.oracle.com/javase/tutorial/sound/overview-MIDI.html)
