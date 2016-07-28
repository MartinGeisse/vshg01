.code
.export baselib_cpu_baselib_cpu_setPsw
.export baselib_cpu_getPsw
.export baselib_cpu_setTlbIndex
.export baselib_cpu_getTlbIndex
.export baselib_cpu_setTlbEntryHigh
.export baselib_cpu_getTlbEntryHigh
.export baselib_cpu_setTlbEntryLow
.export baselib_cpu_getTlbEntryLow
.export baselib_cpu_setTlbBadAddress
.export baselib_cpu_getTlbBadAddress
.export baselib_cpu_useRomInterruptHandler
.export baselib_cpu_useRamInterruptHandler
.export baselib_cpu_disableInterrupts
.export baselib_cpu_enableInterrupts
.export baselib_cpu_interruptsEnabled
.export baselib_cpu_disableInterruptChannel
.export baselib_cpu_enableInterruptChannel
.export baselib_cpu_interruptChannelEnabled
.export baselib_cpu_getInterruptPriority
.export baselib_cpu_setInterruptReturnAddress
.export baselib_cpu_getInterruptReturnAddress
.export baselib_cpu_isRomCode
.export baselib_cpu_getProgramCounter

.set	PswVectorBit,					0x08000000
.set	PswUserModeBit,					0x04000000
.set	PswPreviousUserModeBit,			0x02000000
.set	PswOldUserModeBit,				0x01000000
.set	PswInterruptEnableBit,			0x00800000
.set	PswPreviousInterruptEnableBit,	0x00400000
.set	PswOldInterruptEnableBit,		0x00200000
.set	PswPriorityBits,				0x001F0000
.set	PswInterruptMaskBits,			0x0000FFFF

.set	PswVectorShift,					27
.set	PswUserModeShift,				26
.set	PswPreviousUserModeShift,		25
.set	PswOldUserModeShift,			24
.set	PswInterruptEnableShift,		23
.set	PswPreviousInterruptEnableShift,22
.set	PswOldInterruptEnableShift,		21
.set	PswPriorityShift,				16
.set	PswInterruptMaskShift,			0

baselib_cpu_setPsw:
	mvts	$4, 0
	jr		$31

baselib_cpu_getPsw:
	mvfs	$2, 0
	jr $31

baselib_cpu_setTlbIndex:
	mvts	$4, 1
	jr		$31

baselib_cpu_getTlbIndex:
	mvfs	$2, 1
	jr $31

baselib_cpu_setTlbEntryHigh:
	mvts	$4, 2
	jr		$31

baselib_cpu_getTlbEntryHigh:
	mvfs	$2, 2
	jr $31

baselib_cpu_setTlbEntryLow:
	mvts	$4, 3
	jr		$31

baselib_cpu_getTlbEntryLow:
	mvfs	$2, 3
	jr $31

baselib_cpu_setTlbBadAddress:
	mvts	$4, 4
	jr		$31

baselib_cpu_getTlbBadAddress:
	mvfs	$2, 4
	jr $31

baselib_cpu_setInterruptReturnAddress:
	add		$30, $4, 0
	jr		$31

baselib_cpu_getInterruptReturnAddress:
	add		$2, $30, 0
	jr		$31

baselib_cpu_useRomInterruptHandler:
	mvfs	$8, 0
	and		$8, $8, ~PswVectorBit
	mvts	$8, 0
	jr		$31

baselib_cpu_useRamInterruptHandler:
	mvfs	$8, 0
	or		$8, $8, PswVectorBit
	mvts	$8, 0
	jr		$31

baselib_cpu_disableInterrupts:
	mvfs	$8, 0
	and		$8, $8, ~PswInterruptEnableBit
	mvts	$8, 0
	jr		$31

baselib_cpu_enableInterrupts:
	mvfs	$8, 0
	or		$8, $8, PswInterruptEnableBit
	mvts	$8, 0
	jr		$31

baselib_cpu_interruptsEnabled:
	mvfs	$2, 0
	and		$2, $2, PswInterruptEnableBit
	slr		$2, $2, PswInterruptEnableShift
	jr		$31

baselib_cpu_disableInterruptChannel:
	add		$8, $0, 16			; max <- 16
	bgeu	$4, $8, _return		; if (arg >= max) return
	add		$8, $0, 1			; temp <- 0 + 1 = 1
	sll		$8, $8, $4			; temp <- temp << arg = 1 << arg
	xnor	$8, $8, $0			; temp <- ~temp = ~(1 << arg)
	mvfs	$9, 0				; old <- Psw
	and		$9, $9, $8			; new <- old & temp = Psw & temp
	mvts	$9, 0				; Psw <- new
	jr		$31					; return

baselib_cpu_enableInterruptChannel:
	add		$8, $0, 16			; max <- 16
	bgeu	$4, $8, _return		; if (arg >= max) return
	add		$8, $0, 1			; temp <- 0 + 1 = 1
	sll		$8, $8, $4			; temp <- temp << arg = 1 << arg
	mvfs	$9, 0				; old <- Psw
	or		$9, $9, $8			; new <- old | temp = Psw | temp
	mvts	$9, 0				; Psw <- new
	jr		$31					; return

baselib_cpu_interruptChannelEnabled:
	add		$8, $0, 16			; max <- 16
	bgeu	$4, $8, _return		; if (arg >= max) return
	add		$8, $0, 1			; temp <- 0 + 1 = 1
	sll		$8, $8, $4			; temp <- temp << arg = 1 << arg
	mvfs	$9, 0				; Psw <- Psw
	and		$8, $9, $8			; temp <- Psw & temp = Psw & temp
	slr		$2, $8, $4			; result <- temp >> arg
	jr		$31					; return: result

_return:
	add		$2, $0, -1
	jr $31

baselib_cpu_getInterruptPriority:
	mvfs	$2, 0
	and		$2, $2, PswPriorityBits
	slr		$2, $2, PswPriorityShift
	jr $31

baselib_cpu_isRomCode:
	slr		$2, $31, 29			; temp <- $31 (29) == returnAddress (29)
	and		$2, $2, 1			;   ...
	jr		$31

baselib_cpu_getProgramCounter:
	add		$2, $0, $31
	jr		$31
