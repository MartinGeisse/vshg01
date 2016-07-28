
updateQueueTextFeeder = function(markupId, textToAppend) {
	
	// the (textToAppend == '') case isn't dangerous, but we'd want to avoid it when setting breakpoints in the debugger 
	if (textToAppend != '') {
		var node = $('#' + markupId)[0];
		var scrollPositionFromBottom = (node.scrollHeight - node.scrollTop);
		
		// Insert text or handle erasing one character. Note that "erase" is sent as
		// backspace-space-backspace. This might be a backwards compatibility thing.
		if (textToAppend == '\b \b') {
			if (node.value != '') {
				node.value = node.value.substring(0, node.value.length - 1);
			}
		} else {
			node.value += textToAppend;
		}
		
		node.scrollTop = (node.scrollHeight - scrollPositionFromBottom); 
	}
	
};
