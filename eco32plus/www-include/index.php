<html>
<?php

// add some helper functions
include ('misc.php');
include ('navigation.php');

// gather config information
include ('sections.php');
include ('moderators.php');

// determine active section
$defaultSection = 'home';

if (isset ($_GET ['section'])) {
	$activeSectionID = $_GET ['section'];
	if (!isset ($sectionTable [$activeSectionID])) {
		$activeSectionID = $defaultSection;
	}
} else {
	$activeSectionID = $defaultSection;
}
$activeSectionDescriptor = $sectionTable [$activeSectionID];

// determine active subsection (selecting none is possible and default)
if (isset ($activeSectionDescriptor ['subsections'])) {
	$subsectionTable = $activeSectionDescriptor ['subsections'];
	if (isset ($_GET ['subsection'])) {
		if (isset ($subsectionTable [$_GET ['subsection']])) {
			$activeSubsectionID = $_GET ['subsection'];
			$activeSubsectionDescriptor = $subsectionTable [$activeSubsectionID];
		}
	}
}

// generate header
echo "<head>\n";
echo "<title>" . $activeSectionDescriptor ['name'] . "</title>\n";
echo "<link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\">";
echo "</head>\n";
echo "<body>\n";
echo "<table class=\"LayoutTable\"><tr><td class=\"NavigationLayoutCell\">\n";

// generate navigation menu
naviShow ("Navigation");

// generate menu / content separator
echo "</td>\n";
echo "<td rowspan=\"2\" class=\"SeparatorLayoutCell\">&nbsp;</td>\n";
echo "<td rowspan=\"2\" class=\"ContentLayoutCell\">\n";

// generate content
if (isset ($activeSubsectionID)) {
	require ("sections/" . $activeSectionID . "/" . $activeSubsectionID . ".php");
} else {
	require ("sections/" . $activeSectionID . "/content.php");
}

// generate footer
echo "</td></tr><tr><td class=\"BottomLeftLayoutCell\">&nbsp;</td></tr></table>\n";
echo "</body>\n";

?>
</html>
