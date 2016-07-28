<?php

function getSectionLinkWithText ($sectionID, $text)
{
    return "<a href=\"/index.php?section=$sectionID\">$text</a>";
}

function getSectionLink ($sectionID)
{
    global $sectionTable;
    $descriptor = $sectionTable [$sectionID];
    $name = $descriptor ['name'];
    return getSectionLinkWithText ($sectionID, $name);
}

function printSectionLinkWithText ($sectionID, $text)
{
    echo getSectionLinkWithText ($sectionID, $text);
}

function printSectionLink ($sectionID)
{
    echo getSectionLink ($sectionID);
}

function getSubsectionLinkWithText ($sectionID, $subsectionID, $text)
{
    return "<a href=\"/index.php?section=$sectionID&subsection=$subsectionID\">$text</a>";
}

function getSubsectionLink ($sectionID, $subsectionID)
{
    global $sectionTable;
    $sectionDescriptor = $sectionTable [$sectionID];
    if (!isset ($sectionDescriptor ['subsections']))
        return "<b>(invalid subsection link to $sectionID -> $subsectionID (no subsections in this section)</b>";
    $subsectionTable = $sectionDescriptor ['subsections'];
    if (!isset ($subsectionTable [$subsectionID]))
        return "<b>(invalid subsection link to $sectionID -> $subsectionID (no such subsection)</b>";
    $subsectionDescriptor = $subsectionTable [$subsectionID];
    $name = $subsectionDescriptor ['name'];
    
    return getSubsectionLinkWithText ($sectionID, $subsectionID, $name);
}

function printSubsectionLinkWithText ($sectionID, $subsectionID, $text)
{
    echo getSubsectionLinkWithText ($sectionID, $subsectionID, $text);
}

function printSubsectionLink ($sectionID, $subsectionID)
{
    echo getSubsectionLink ($sectionID, $subsectionID);
}

?>
