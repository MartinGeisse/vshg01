<?php beginNewsEntry ('martin', 'Tue, Aug 10, 2010', 'Abnormal Release Cycle Termination'); ?>
	<p>
		The idea that a development cycle could be started without a
		definite goal was a complete failure. Without a goal, development
		cannot happen in a directed way, leading to almost no work being
		done. This was complemented by the fact that I had little time
		to work on the project at all. 
	</p>
	<p>
		On the positive side, I have started a bigger project using the
		ECO32. This will hopefully demonstrate its usefulness and also
		result in some nice features being added. I am talking about a
		port of the back-then popular game DOOM that runs on the ECO32.
		This will be a tough one, mostly due to the FPGA having too little
		Block RAM for a frame buffer, but I expect to be able to solve
		this problem one way or the other.
	</p>
	<p>
		The first step will be to clean up or rewrite the DOOM source code
		to run it on Mac OS X. The intention is to get to know the code and
		at the same time prepare it for the actual port. I do not expect
		other DOOM source ports to be useful here, because this port will
		require massive hacking that defeats the portability for which
		other source-level DOOM-based projects are made. I will likely
		use SDL or Allegro for the system-dependent code, but only as
		much as needed to simulate the hardware it will actually run on.
	</p>
	<p>
		I will yet have to see in what way an agile development cycle can
		be applied here. The main problem is that a partial DOOM port
		helps no one, so it makes little sense to define the desired
		features for an upcoming cycle. This might change when the code
		is closer to being finished.
	</p>
<?php endNewsEntry (); ?>

<?php beginNewsEntry ('martin', 'Sun, Jul 11, 2010', 'Release 3'); ?>
	<p>
		Release 3 of the ECO32 Plus project is ready and can be downloaded
		on the <?php printSectionLink('download') ?> page. Releases 1 and 2 have
		been taken offline -- there is little value in these old releases.
	</p>
	<p>
		The rewritten CPU is ready and boots the EOS32 operating system from
		the original ECO32 project (a Unix v7 port) without problems. This means
		we now have a well-documented second implementation of the ECO32 CPU.
	</p>
	<p>
		One of the bigger goals in the future will be to port a modern OS
		to our computer, and the next release will work towards that goal
		by improving the simulator. There will be many small improvements,
		so it makes little sense to list them here. Big improvements have
		to wait for a future development cycle.
	</p>
	<p>
		The next release is due on Sunday, July 25.
	</p>
<?php endNewsEntry (); ?>

<?php beginNewsEntry ('martin', 'Sun, Jun 27, 2010', 'Release 2'); ?>
	<p>
		Release 2 of the ECO32 Plus project is ready and can be downloaded
		on the <?php printSectionLink('download') ?> page.
	</p>
	<p>
		This release features a rewritten version of the HDL code of the ECO32 CPU.
		The intention behind this is that the original code is poorly documented,
		uses unintuitive signal and module names, and has no test cases. 
	</p>
	<p>
		I found that the synthesis tools are even less reliable that I thought.
		Although an initial version of the CPU was ready quickly, I had to start
		all over again because the design was not synthesizable, the coding style
		I used made it difficult to guess where the problem was, and the synthesis
		tool gave no information at all. Fortunately, the agile development style
		allowed me to re-use the unit test cases with a few changes and at least
		get the new CPU correct in simulation for this release.
	</p>
	<p>
		Roadmap for the next release, due on Sunday, July 11:
		<ul>
			<li>clean up the code and improve documentation</li>
			<li>synthesize the new CPU</li>
		</ul>
	</p>
<?php endNewsEntry (); ?>

<?php beginNewsEntry ('martin', 'Sun, Jun 13, 2010', 'Release 1'); ?>
	<p>
		Release 1 of the ECO32 Plus project is ready and can be downloaded
		on the <?php printSectionLink('download') ?> page.
	</p>
	<p>
		Roadmap for the next release, due on Sunday, June 27:
		<ul>
			<li>finish re-writing the CPU hardware description</li>
			<li>create a new project for the HDL files separate from eco32-0.19</li>
		</ul>
	</p>
<?php endNewsEntry (); ?>
