upbracing-codegen
=================

At the UPBracing Team, we use a lot of AT90CAN processors (AVR family, similar to ATmega). Therefore, we have created some tools to keep the software for those processors easy to understand, although we will add more features each year.

We provide these code generators and libraries:
* caRTOS: a small operating system for AVR processors. It support semaphores and queues. It doesn't use dynamic memory management.
* Statemachines: We provide an editor (Eclipse plugin) and a code generator for statemachines.
* CAN: We generate methods for sending and receiving CAN messages based on a protocol description in a DBC file or our custom format (a Ruby DSL).
* Timers: We provide and editor (Eclipse plugin) and a code generator for setting up timers on the AT90CAN.
* caRTOS configuration: Simple generator for caRTOS configuration files.
* EEPROM accessors: Simple generator for EEPROM accessor functions - `read_SOME_VALUE()` and `write_SOME_VALUE(x)`.
* global variables: Simple generator for interrupt-safe access of global variables - `getSomeValue()` and `setSomeValue(x)`.
* pin names: Access GPIO pins by name instead of port and number. You can use `OUTPUT(ERROR_LED_OIL_TEMP)` instead of `DDRA |= (1<<3)`. Pin names can be imported from an EAGLE schematics file.

You can find the complete documentation in the 'documentation' folder. All our tools should run fine on Linux, Mac OS X and Windows. If you have any problems on one of these platforms, please file a bug report.

Authors
=======

This repository contains software that has been developed in the project group "Racing Car IT" at University Paderborn. The software is used by the UPBracing Team. The project group has also developed a CAN monitoring software ("RemoteCockpit") which we won't publish as open source.

Members of the project group who worked on the software in this repository:
* Peer Adelt: Timer editor and generator, operating system
* Benjamin Koch (snowball): Generator framework, statemachine generator, most of the simple generators (EEPROM, global variables, pin names)
* Sven Schönberg: CAN generator
* Rishab Dhar: Statemachine editor and validation part of the generator
* Krishna Sudhakar: Semaphore and queue implementation for the operating system

The project group was kindly supported by Prof. Marco Platzner (University of Paderborn), Tobias Beisel, Sebastian Meisner and Lars Schäfers.

License
=======

The operating systems, the Eclipse plugins, the generator framework and the generators themselves are under LGPL license. This means that you can use them in your program without making it open source, if you only link to our code as a library (i.e. use the provided JAR file).

The generated code is always yours. We don't put any restrictions on it. The code is provided "as is", we disclaim any warranties for it and we are not responsible for whatever you do with that code. Some parts of the templates (which are under LGPL license) are copied to the generated files. This doesn't affect the rights we grant you in this paragraph because we grant those rights in addition to the LGPL.

You can add your own code generators that use our framework. If you don't want to put them under LGPL, you mustn't put them into the same JAR file as our code. For the details, please read the LGPL license.

We have decided on a license after writing the code, so our code doesn't have the usual license headers. If you need them, you must add them yourself.

Tools we use or support
=======================

Eclipse
-------

We use Eclipse to compile our AVR code. Of course, you can use a different IDE, but we provide special support for [Eclipse](http://www.eclipse.org/). Behind the scenes, Eclipse uses the avr-gcc compiler The statemachine and timer editors are implemented as Eclipse plugins, so you definitely need Eclipse to use the editors.

The statemachine editor uses GMF, Eugenia and Emfatic.

JRuby
-----

The configuration files and part of our implementation are written in Ruby and executed by [JRuby](http://jruby.org/). If you don't know Ruby, you should learn it! However, you don't need to know Ruby to use our generators.

DBC files
---------

A file format that describes CAN communication (messages and signals). It is supported by many tools. We have implemented our own parser.

You can use the CANdb++ editor program by Vector. Unfortunately, it is only available in the CANoe software suite which is quite big; fortunately, it's free.

EAGLE schematics editor
-----------------------

We use the [EAGLE editor](http://www.cadsoftusa.com/) for our schematics and boards. Our code generator tools can import pin names from EAGLE schematics.

java-parser-tools
-----------------

[http://code.google.com/p/java-parser-tools/](http://code.google.com/p/java-parser-tools/),
Apache License 2.0

We use it because it is a parser combinator. This means that we can extend the parser at runtime. This means that we can add extensions to the statemachine event syntax by plugins (via Java's ServiceLoader). Unfortunately, we didn't finish that feature. However, that won't be too much work because all the foundations are already there.

We have extended the library and we also improved its performance a lot. It used to be really slow. With our changes, it is fast enough to parse the short strings we use for statemachine actions and transition labels.

There is another parser combinator library for Java. It's faster and cleaner to use, but it is under GPL, so we couldn't use it. I don't remember its name...
