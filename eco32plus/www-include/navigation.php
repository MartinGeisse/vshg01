<?php

function naviShowToplevel ($whatever)
{
    echo "$whatever <br />\n";
}

function naviShowIndented ($whatever)
{
    echo "&nbsp;&nbsp;&nbsp;$whatever <br />\n";
}

function naviShowInactiveSection ($id)
{
    naviShowToplevel (getSectionLink ($id));
}

function naviShowActiveSection ()
{
    global $activeSectionDescriptor;
    if (isset ($GLOBALS ['activeSubsectionID'])) {
        $id = $activeSectionDescriptor ['id'];
        naviShowInactiveSection ($id);
    } else {
        $name = $activeSectionDescriptor ['name'];
        naviShowToplevel ("<b>$name</b>");
    }
}

function naviShowInactiveSubsection ($id)
{
    global $activeSectionID;
    naviShowIndented (getSubsectionLink ($activeSectionID, $id));
}

function naviShowActiveSubsection ()
{
    global $activeSubsectionDescriptor;
    $name = $activeSubsectionDescriptor ['name'];
    naviShowIndented ("<b>$name</b>");
}

function naviShow ($headerText)
{
    global $sectionTable, $activeSectionID;
    
    echo "<b>$headerText</b><br /><br />\n";
    foreach ($sectionTable as $sectionDescriptor) {
        if ($sectionDescriptor ['id'] === $activeSectionID) {
            naviShowActiveSection ();
            if (isset ($GLOBALS ['subsectionTable'])) {
                global $subsectionTable, $activeSubsectionID;
                foreach ($subsectionTable as $subsectionDescriptor) {
                    if ($subsectionDescriptor ['id'] === $activeSubsectionID)
                        naviShowActiveSubsection ();
                    else
                        naviShowInactiveSubsection ($subsectionDescriptor ['id']);
                }
            }
        } else {
            naviShowInactiveSection ($sectionDescriptor ['id']);
        }
    }
}

?>
