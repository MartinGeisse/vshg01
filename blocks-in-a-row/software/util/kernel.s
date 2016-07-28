
; -----------------------------------------------
; kernel panic
; -----------------------------------------------

.export panic
.export seriousPanic
.align 4

seriousPanic:
	add		$4, $0, _seriousPanicMessage
	j panic

_seriousPanicMessage:
	.byte	"kernel panic "
	.byte	0

; ----------

.align 4
.set _panicMessageDestination, 0xF0100000

panic:
	add		$8, $0, _panicMessageDestination
_panicLoop:
	ldbu	$9, $4, 0
	beq		$9, $0, _panicHang
	add		$9, $9, 0x0f00
	stw		$9, $8, 0
	add		$4, $4, 1
	add		$8, $8, 4
	j		_panicLoop
_panicHang:
	j		_panicHang
