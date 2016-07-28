
; -----------------------------------------------
; segment end labels
; -----------------------------------------------

.export _codeSegmentEnd
.export _dataSegmentEnd
.export _bssSegmentEnd

	.code
	.align	4
_codeSegmentEnd:

	.data
	.align	4
_dataSegmentEnd:

	.bss
	.align	4
_bssSegmentEnd:
