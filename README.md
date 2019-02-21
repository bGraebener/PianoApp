# PianoApp

>Title: Piano Application using LEAP Motion  
Team: Krisztián Nagy, Bastian Graebener

## Outline: 
    The application will allow the user to play piano using the LEAP Motion controller.
    The controller will pick up finger tap gestures. The data from the controller
    will be converted into midi note on and midi note off events which are sent to
    a virtual midi driver which in turn will control a virtual piano instrument.
    If time permits a graphical representation of a piano keyboard will be presented
    to the user to give a frame of reference.


## Technologies used:
    1. LEAP Motion controller
    2. loopMidi, a virtual midi driver application , see https://www.tobias-erichsen.de/software/loopmidi.html
    3. Tiny Virtual Piano, virtual piano instrument 
   

## Work done so far:
    - installed loopMidi and created virtual midi driver
    - connected virtual piano to midi driver
    - programmed small java application to connect to midi driver and send midi note on and note off 
      events to the virtual piano programatically
