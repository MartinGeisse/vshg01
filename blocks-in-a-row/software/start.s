
.import panic

; -----------------------------------------------
; constants and definitions
; -----------------------------------------------

.set	BIO_OUT,0xF1000000	; board I/O output port
.set	SPI_EN,0x80000000	; SPI bus enable ctrl bit

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

.import handleInterrupt

.data
.align	4
.nosyn

interruptContextBuffer:
	.word	0		; register 00
	.word	0		; register 01
	.word	0		; register 02
	.word	0		; register 03
	.word	0		; register 04
	.word	0		; register 05
	.word	0		; register 06
	.word	0		; register 07
	.word	0		; register 08
	.word	0		; register 09
	.word	0		; register 10
	.word	0		; register 11
	.word	0		; register 12
	.word	0		; register 13
	.word	0		; register 14
	.word	0		; register 15
	.word	0		; register 16
	.word	0		; register 17
	.word	0		; register 18
	.word	0		; register 19
	.word	0		; register 20
	.word	0		; register 21
	.word	0		; register 22
	.word	0		; register 23
	.word	0		; register 24
	.word	0		; register 25
	.word	0		; register 26
	.word	0		; register 27
	.word	0		; register 28
	.word	0		; register 29
	.word	0		; register 30
	.word	0		; register 31

.code
.align	4
.nosyn

interruptEntryHandler:
	ldhi	$26, interruptContextBuffer
	or		$26, $26, interruptContextBuffer
;	stw		$0, $26, 0 * 4
	stw		$1, $26, 1 * 4
	stw		$2, $26, 2 * 4
	stw		$3, $26, 3 * 4
	stw		$4, $26, 4 * 4
	stw		$5, $26, 5 * 4
	stw		$6, $26, 6 * 4
	stw		$7, $26, 7 * 4
	stw		$8, $26, 8 * 4
	stw		$9, $26, 9 * 4
	stw		$10, $26, 10 * 4
	stw		$11, $26, 11 * 4
	stw		$12, $26, 12 * 4
	stw		$13, $26, 13 * 4
	stw		$14, $26, 14 * 4
	stw		$15, $26, 15 * 4
	stw		$16, $26, 16 * 4
	stw		$17, $26, 17 * 4
	stw		$18, $26, 18 * 4
	stw		$19, $26, 19 * 4
	stw		$20, $26, 20 * 4
	stw		$21, $26, 21 * 4
	stw		$22, $26, 22 * 4
	stw		$23, $26, 23 * 4
	stw		$24, $26, 24 * 4
	stw		$25, $26, 25 * 4
;	stw		$26, $26, 26 * 4
;	stw		$27, $26, 27 * 4
;	stw		$28, $26, 28 * 4
	stw		$29, $26, 29 * 4
	stw		$30, $26, 30 * 4
	stw		$31, $26, 31 * 4
.syn
	add		$29, $0, 0xC0000000 + 3*1024*1024
	jal		handleInterrupt
.nosyn
	ldhi	$26, interruptContextBuffer
	or		$26, $26, interruptContextBuffer
;	ldw		$0, $26, 0 * 4
	ldw		$1, $26, 1 * 4
	ldw		$2, $26, 2 * 4
	ldw		$3, $26, 3 * 4
	ldw		$4, $26, 4 * 4
	ldw		$5, $26, 5 * 4
	ldw		$6, $26, 6 * 4
	ldw		$7, $26, 7 * 4
	ldw		$8, $26, 8 * 4
	ldw		$9, $26, 9 * 4
	ldw		$10, $26, 10 * 4
	ldw		$11, $26, 11 * 4
	ldw		$12, $26, 12 * 4
	ldw		$13, $26, 13 * 4
	ldw		$14, $26, 14 * 4
	ldw		$15, $26, 15 * 4
	ldw		$16, $26, 16 * 4
	ldw		$17, $26, 17 * 4
	ldw		$18, $26, 18 * 4
	ldw		$19, $26, 19 * 4
	ldw		$20, $26, 20 * 4
	ldw		$21, $26, 21 * 4
	ldw		$22, $26, 22 * 4
	ldw		$23, $26, 23 * 4
	ldw		$24, $26, 24 * 4
	ldw		$25, $26, 25 * 4
;	ldw		$26, $26, 26 * 4
;	ldw		$27, $26, 27 * 4
;	ldw		$28, $26, 28 * 4
	ldw		$29, $26, 29 * 4
	ldw		$30, $26, 30 * 4
	ldw		$31, $26, 31 * 4
	rfx

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

	; initialize the CPU
	mvts	$0, 0
	mvts	$0, 1
	mvts	$0, 2
	mvts	$0, 3
	mvts	$0, 4

	; initialize the stack
	add		$29, $0, 0xC0000000 + 2*1024*1024

	; copy code from 0xE0000000 (image start == code image start) to 0xC0000000 (code segment start)
	add		$8, $0, 0xE0000000			; current source address
	add		$9, $0, _codeSegmentStart	; current destination address
	add		$10, $0, _codeSegmentEnd	; destination stop address
codeCopyLoop:
	; copy one word and increment pointers
	ldw		$11, $8, 0
	add		$8, $8, 4
	stw		$11, $9, 0
	add		$9, $9, 4
	; loop until the current destination reaches the destination stop
	bltu	$9, $10, codeCopyLoop

	; copy data from (data image start, now in $8) to (data segment start)
	add		$9, $0, _dataSegmentStart	; current destination address
	add		$10, $0, _dataSegmentEnd	; destination stop address
dataCopyLoop:
	; copy one word and increment pointers
	ldw		$11, $8, 0
	add		$8, $8, 4
	stw		$11, $9, 0
	add		$9, $9, 4
	; loop until the current destination reaches the destination stop
	bltu	$9, $10, dataCopyLoop

	; switch to code in RAM
.nosyn
	jal _romToRamSwitch_target
_romToRamSwitch_target:
	ldhi	$8, 0x20000000
	sub		$31, $31, $8
	add		$31, $31, 16
	jr		$31
.syn

	; clear bss segment
	add		$8, $0, _bssSegmentStart	; start with first word of bss
	add		$9, $0, _bssSegmentEnd		; this is one above the top
	j		clrtest
clrloop:
	stw		$0, $8, 0					; dst addr in $8
	add		$8, $8, 4					; upward
clrtest:
	bltu	$8, $9, clrloop

	; disable flash ROM, enable SPI bus
	add		$8, $0, BIO_OUT
	add		$9, $0, SPI_EN
	stw		$9, $8, 0

	; run the main function, then hang
	jal		main
	add		$4, $0, _shutdownMessage
	j		panic

_shutdownMessage:
	.byte	"kernel shutdown "
	.byte	0


.align 	4
.syn
