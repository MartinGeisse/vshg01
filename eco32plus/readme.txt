
-----------------------------------------------------------------------------
--- Introduction
-----------------------------------------------------------------------------

Welcome to ECO32 Plus!

ECO32 Plus is a project to build a complete and understandable computer on
FPGA technology for educational purposes. Our goal is to build a clean,
modern, well-designed, and well-documented computer that can be used to
teach computer system architecture and implementation. This includes:

- the hardware, using a synthesizable description written in a hardware
  description language
- development tools, such as a C compiler and a high-level hardware
  simulator
- the operating system, applications, and other software that runs on
  the computer

Our goal is NOT to build a computer that is competitive on the real-world
market. While we choose to incorporate modern architectural design
decisions, we do not compromise a clean design for quick time-to-market
or similar real-world goals.

ECO32 Plus is a fork of the original ECO32 project (the name is an acronym
for "educational 32-bit computer") from the University of Applied Science,
Giessen. ECO32 Plus includes additional tools and is based on agile
development and project management methods. The original ECO32 project can
be found at

	http://homepages.fh-giessen.de/~hg53/eco32/
	
Both projects are actively developed.

-----------------------------------------------------------------------------
--- System requirements
-----------------------------------------------------------------------------

FPGA Hardware: Although the whole project is intended to be portable to
different hardware architectures, it still runs on just one: The XSA-3S1000
development board with XStend extension board, built by XESS Corp.
(http://www.xess.com).

Development system: It is unclear whether the project can be developed on
other platforms than 32-bit Linux and 64-bit Mac OS X. Intended development
platforms are Linux, Mac OS X, Windows (using Cygwin), and Solaris. Give
it a try.

Tool chain: You need to have a full GCC tool suite installed, period. Linux
systems get this for free. On Mac OS X, you have to install XCode which can
be downloaded from Apple by registered users. For Windows, Cygwin *should*
work (it did work in the past). You will also need Java (JRE and JDK) and
Apache Ant to build the Java-based tools. Eclipse is highly recommended, 
but not strictly necessary.

-----------------------------------------------------------------------------
--- Overview
-----------------------------------------------------------------------------

ECO32. ECO32 Plus still contains the original ECO32 code as one of its main
components. This code is an unchanged copy of ECO32 0.19 except for a few
small patches that improve portability. It can be found in the
eco32-0.19 folder. To build the project, go into that folder and run make.
The hardware for the project must be built separately by unzipping the
most recent version in the "fpga" subfolder and using Xilinx ISE for
synthesis. (Note the special requirements of the XSA-3S1000 board about the
configuration of unused pins - all pins must be configured to "float",
otherwise it won't work. We can't tell for sure that all ISE versions will
understand the settings in the .npl file correctly)

Tetris demo project. This project uses its own hardware design that was
once forked from the ECO32 project, and independent OS-less software. The
HDL files live in the "hardware" subfolder and must be build using Xilinx
ISE -- again check that all unused and programming pins are configured to
"float". The software must be built using the tools from the ECO32 project,
which must thus be built first. Go into the "software" subfolder and run make.

swtlib. This is an SWT utility library used by the ecotools project. Just make
sure that the swtlib folder and the ecotools folder are in the same parent
folder.

ecotools. For now, this project contains a Java-based version of the
ECO32 instruction-level simulator. Go into the ecotools/resource/ant
subfolder. If you are not using Eclipse, you must first compile the Java code
by running ant on the build file "eclipse-build-substitute.xml". Then (Eclipse
or not), run ant on the build file "build.xml". This will create a folder
called "build" in the ecotools project that contains builds of the simulator
for all platforms. You can ignore or delete the "common" folder that is generated
in parallel to the platform-specific folders -- this was used temporarily
during the build process and is not needed afterwards anymore. Run the simulator
using the ecosim.sh script.

