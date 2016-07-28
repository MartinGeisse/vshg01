
<h1>Welcome to ECO32 Plus!</h1>

<p>
ECO32 Plus is a project to build a complete and understandable computer on
FPGA technology for educational purposes. Our goal is to build a clean,
modern, well-designed, and well-documented computer that can be used to
teach computer system architecture and implementation. This includes:
<ul>
<li>the hardware, using a synthesizable description written in a hardware
description language</li>
<li>development tools, such as a C compiler and a high-level hardware
simulator</li>
<li>the operating system, applications, and other software that runs on
the computer</li>
</ul>
</p>

<p>
Our goal is NOT to build a computer that is competitive on the real-world
market. While we choose to incorporate modern architectural design
decisions, we do not compromise a clean design for quick time-to-market
or similar real-world goals.
</p>

<p>
ECO32 Plus is a fork of the original ECO32 project (the name is an acronym
for "educational 32-bit computer") from the University of Applied Science,
Giessen. ECO32 Plus includes additional tools and is based on agile
development and project management methods. The original ECO32 project can
be found at
<pre>
	http://homepages.fh-giessen.de/~hg53/eco32/
</pre>
Both projects are being actively developed.
</p>

<h1>News</h1>
<?php

function beginNewsEntry ($moderatorID, $timetext, $headline)
{
    global $moderatorTable;
    $modname = $moderatorTable [$moderatorID]['name'];
    echo "<div class=\"NewsEntry\"><b>$headline</b> - $modname on $timetext<br /><br />\n";
}

function endNewsEntry ()
{
    echo "</div>\n";
}

require ('news-entries.php');

?>
