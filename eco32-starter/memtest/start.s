;
; start.s -- standalone startup
;

	.set	stacktop,0xC0010000 + 0x2000
	.set	conout,0xC0000018

	.import	_ecode
	.import	_edata
	.import	_ebss

	.import	main

	.export	_bcode
	.export	_bdata
	.export	_bbss

	.export	putChar

	.code
_bcode:

	.data
_bdata:

	.bss
_bbss:

	.code
	.align	4

	;
	; C startup code
	;
start:
	; copy data segment
	add	$10,$0,_bdata		; lowest dst addr to be written to
	add	$8,$0,_edata		; one above the top dst addr
	sub	$9,$8,$10		; $9 = size of data segment
	add	$9,$9,_ecode		; data is waiting right after code
	j	cpytest
cpyloop:
	ldw	$11,$9,0		; src addr in $9
	stw	$11,$8,0		; dst addr in $8
cpytest:
	sub	$8,$8,4			; downward
	sub	$9,$9,4
	bgeu	$8,$10,cpyloop

	; clear bss segment
	add	$8,$0,_bbss		; start with first word of bss
	add	$9,$0,_ebss		; this is one above the top
	j	clrtest
clrloop:
	stw	$0,$8,0			; dst addr in $8
	add	$8,$8,4			; upward
clrtest:
	bltu	$8,$9,clrloop

	; call main
	add	$29,$0,stacktop
	jal	main

	; stop execution
stop:
	j	stop

	;
	; output character to console
	;
putChar:
	sub	$29,$29,32
	stw	$31,$29,16
	add	$8,$0,conout
	jalr	$8
	ldw	$31,$29,16
	add	$29,$29,32
	jr	$31
