.code
.export setPSW
.export getPSW
.export setTLBIndex
.export getTLBIndex
.export setTLBEntryHigh
.export getTLBEntryHigh
.export setTLBEntryLow
.export getTLBEntryLow
.export setTLBBadAddress
.export getTLBBadAddress
.export useROMInterruptHandler
.export useRAMInterruptHandler
.export disableInterrupts
.export enableInterrupts
.export interruptsEnabled
.export disableInterruptChannel
.export enableInterruptChannel
.export interruptChannelEnabled
.export getInterruptPriority
.export setInterruptReturnAddress
.export getInterruptReturnAddress
.export isROMCode
.export getProgramCounter

.set	PSWVectorBit,					0x08000000
.set	PSWUserModeBit,					0x04000000
.set	PSWPreviousUserModeBit,			0x02000000
.set	PSWOldUserModeBit,				0x01000000
.set	PSWInterruptEnableBit,			0x00800000
.set	PSWPreviousInterruptEnableBit,	0x00400000
.set	PSWOldInterruptEnableBit,		0x00200000
.set	PSWPriorityBits,				0x001F0000
.set	PSWInterruptMaskBits,			0x0000FFFF

.set	PSWVectorShift,					27
.set	PSWUserModeShift,				26
.set	PSWPreviousUserModeShift,		25
.set	PSWOldUserModeShift,			24
.set	PSWInterruptEnableShift,		23
.set	PSWPreviousInterruptEnableShift,22
.set	PSWOldInterruptEnableShift,		21
.set	PSWPriorityShift,				16
.set	PSWInterruptMaskShift,			0

setPSW:
	mvts	$4, 0
	jr		$31

getPSW:
	mvfs	$2, 0
	jr $31

setTLBIndex:
	mvts	$4, 1
	jr		$31

getTLBIndex:
	mvfs	$2, 1
	jr $31

setTLBEntryHigh:
	mvts	$4, 2
	jr		$31

getTLBEntryHigh:
	mvfs	$2, 2
	jr $31

setTLBEntryLow:
	mvts	$4, 3
	jr		$31

getTLBEntryLow:
	mvfs	$2, 3
	jr $31

setTLBBadAddress:
	mvts	$4, 4
	jr		$31

getTLBBadAddress:
	mvfs	$2, 4
	jr $31

setInterruptReturnAddress:
	add		$30, $4, 0
	jr		$31

getInterruptReturnAddress:
	add		$2, $30, 0
	jr		$31

useROMInterruptHandler:
	mvfs	$8, 0
	and		$8, $8, ~PSWVectorBit
	mvts	$8, 0
	jr		$31

useRAMInterruptHandler:
	mvfs	$8, 0
	or		$8, $8, PSWVectorBit
	mvts	$8, 0
	jr		$31

disableInterrupts:
	mvfs	$8, 0
	and		$8, $8, ~PSWInterruptEnableBit
	mvts	$8, 0
	jr		$31

enableInterrupts:
	mvfs	$8, 0
	or		$8, $8, PSWInterruptEnableBit
	mvts	$8, 0
	jr		$31

interruptsEnabled:
	mvfs	$2, 0
	and		$2, $2, PSWInterruptEnableBit
	slr		$2, $2, PSWInterruptEnableShift
	jr		$31

disableInterruptChannel:
	add		$8, $0, 16			; max <- 16
	bgeu	$4, $8, return		; if (arg >= max) return
	add		$8, $0, 1			; temp <- 0 + 1 = 1
	sll		$8, $8, $4			; temp <- temp << arg = 1 << arg
	xnor	$8, $8, $0			; temp <- ~temp = ~(1 << arg)
	mvfs	$9, 0				; old <- PSW
	and		$9, $9, $8			; new <- old & temp = PSW & temp
	mvts	$9, 0				; PSW <- new
	jr		$31					; return

enableInterruptChannel:
	add		$8, $0, 16			; max <- 16
	bgeu	$4, $8, return		; if (arg >= max) return
	add		$8, $0, 1			; temp <- 0 + 1 = 1
	sll		$8, $8, $4			; temp <- temp << arg = 1 << arg
	mvfs	$9, 0				; old <- PSW
	or		$9, $9, $8			; new <- old | temp = PSW | temp
	mvts	$9, 0				; PSW <- new
	jr		$31					; return

interruptChannelEnabled:
	add		$8, $0, 16			; max <- 16
	bgeu	$4, $8, return		; if (arg >= max) return
	add		$8, $0, 1			; temp <- 0 + 1 = 1
	sll		$8, $8, $4			; temp <- temp << arg = 1 << arg
	mvfs	$9, 0				; psw <- PSW
	and		$8, $9, $8			; temp <- psw & temp = PSW & temp
	slr		$2, $8, $4			; result <- temp >> arg
	jr		$31					; return: result

return:
	add		$2, $0, -1
	jr $31

getInterruptPriority:
	mvfs	$2, 0
	and		$2, $2, PSWPriorityBits
	slr		$2, $2, PSWPriorityShift
	jr $31

isROMCode:
	slr		$2, $31, 29			; temp <- $31 (29) == returnAddress (29)
	and		$2, $2, 1			;   ...
	jr		$31

getProgramCounter:
	add		$2, $0, $31
	jr		$31
