
.import panic

; -----------------------------------------------
; segment start labels
; -----------------------------------------------

.export _codeSegmentStart
.export _dataSegmentStart
.export _bssSegmentStart

	.code
_codeSegmentStart:

	.data
_dataSegmentStart:

	.bss
_bssSegmentStart:

	.code

; -----------------------------------------------
; entry points
; -----------------------------------------------

.align 4
.nosyn

_bootEntryPoint:
	j _startup
	
_interruptEntryPoint:
	j interruptEntryHandler

_userTLBHandlerEntryPoint:
	; fall through

; -----------------------------------------------
; user TLB miss handler
; -----------------------------------------------

.align 4
.syn

_userTLBMiss:
	add		$4, $0, _tlbMissMessage
	j panic

_tlbMissMessage:
	.byte	"TLB miss "
	.byte	0

; -----------------------------------------------
; interrupt handler
; -----------------------------------------------

.align 4
.syn

interruptEntryHandler:
	add		$4, $0, _interruptMessage
	j panic

_interruptMessage:
	.byte	"interrupt "
	.byte	0

; -----------------------------------------------
; kernel startup
; -----------------------------------------------

.import main
.import _codeSegmentEnd
.import _dataSegmentEnd
.import _bssSegmentEnd
.align 4
.syn

_startup:
	mvts	$0, 0
	mvts	$0, 1
	mvts	$0, 2
	mvts	$0, 3
	mvts	$0, 4
	add		$29, $0, 0xC0000000 + 2*1024*1024

	; copy data segment
	add		$10, $0, _dataSegmentStart	; lowest dst addr to be written to
	add		$8, $0, _dataSegmentEnd		; one above the top dst addr
	sub		$9, $8, $10					; $9 = size of data segment
	add		$9,	$9, _codeSegmentEnd		; data is waiting right after code
	j		cpytest
cpyloop:
	ldw		$11, $9, 0					; src addr in $9
	stw		$11, $8, 0					; dst addr in $8
cpytest:
	sub		$8, $8, 4					; downward
	sub		$9, $9, 4
	bgeu	$8, $10, cpyloop

	; clear bss segment
	add		$8, $0, _bssSegmentStart	; start with first word of bss
	add		$9, $0, _bssSegmentEnd		; this is one above the top
	j		clrtest
clrloop:
	stw		$0, $8, 0					; dst addr in $8
	add		$8, $8, 4					; upward
clrtest:
	bltu	$8, $9, clrloop

	jal		main
	add		$4, $0, _shutdownMessage
	j		panic

_shutdownMessage:
	.byte	"kernel shutdown "
	.byte	0


.align 	4
.syn
