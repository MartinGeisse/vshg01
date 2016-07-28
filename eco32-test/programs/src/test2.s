
.nosyn
.code

start:

	; fill registers
	add $11, $0, 1
	add $12, $0, -1
	add $13, $0, 2
	add $14, $0, -2
	add $15, $0, 123
	add $16, $0, -456
	ldhi $17, 0x2a700000
	or $17, $17, 0xf632
	ldhi $18, 0xa1230000
	or $18, $18, 0x4567
	ldhi $19, 0x80000000

	; --------------------------------------------------------
	; start first row
	; --------------------------------------------------------

	; $26 is the current position in the display memory
	ldhi $26, 0xf0100000
	
	; $27 is the current test output character
	add $27, $0, 0x0f00 + 'a'

	; $28 is the '*' character, used to indicate failed tests
	add $28, $0, 0x0f00 + '*'

	; $29 is the '!' character, used to indicate the end of a test line
	add $29, $0, 0x0f00 + '!'

	; --------------------------------------------------------
	; test mul
	; --------------------------------------------------------
	
	mul $4, $15, $13
	add $5, $0, 246
	jal assertEqual
	
	mul $4, $15, $12
	add $5, $0, -123
	jal assertEqual

	mul $4, $15, 3
	add $5, $0, 369
	jal assertEqual

	mul $4, $15, -1
	add $5, $0, -123
	jal assertEqual

	; --------------------------------------------------------
	; test mulu
	; --------------------------------------------------------

	mulu $4, $15, $13
	add $5, $0, 246
	jal assertEqual

	mulu $4, $15, $12
	add $5, $0, -123
	jal assertEqual

	mulu $4, $15, 3
	add $5, $0, 369
	jal assertEqual

	mulu $4, $15, -1
	ldhi $5, 0x7A0000
	or $5, $5, 0xFF85
	jal assertEqual

	; --------------------------------------------------------
	; test div
	; --------------------------------------------------------

	div $4, $15, $13
	add $5, $0, 61
	jal assertEqual

	div $4, $15, $15
	add $5, $0, 1
	jal assertEqual

	div $4, $15, $12
	add $5, $0, -123
	jal assertEqual

	div $4, $15, 3
	add $5, $0, 41
	jal assertEqual

	div $4, $15, -1
	add $5, $0, -123
	jal assertEqual

	div $4, $18, 0xfff0
	ldhi $5, 0x05ed0000
	or $5, $5, 0xcba9
	jal assertEqual

	; --------------------------------------------------------
	; test divu
	; --------------------------------------------------------

	divu $4, $15, $13
	add $5, $0, 61
	jal assertEqual

	divu $4, $15, $15
	add $5, $0, 1
	jal assertEqual

	divu $4, $15, $12
	add $5, $0, 0
	jal assertEqual

	divu $4, $15, 3
	add $5, $0, 41
	jal assertEqual

	divu $4, $15, -1
	add $5, $0, 0
	jal assertEqual

	divu $4, $18, 0xfff0
	or $5, $0, 0xa12d
	jal assertEqual

	; --------------------------------------------------------
	; start second row
	; --------------------------------------------------------

	jal newline

	; --------------------------------------------------------
	; test rem
	; --------------------------------------------------------

	rem $4, $15, $13
	add $5, $0, 1
	jal assertEqual

	rem $4, $15, $15
	add $5, $0, 0
	jal assertEqual

	rem $4, $15, $14
	add $5, $0, 1
	jal assertEqual

	rem $4, $15, 5
	add $5, $0, 3
	jal assertEqual

	rem $4, $15, -12
	add $5, $0, 3
	jal assertEqual

	rem $4, $18, 0xfff0
	add $5, $0, -9
	jal assertEqual

	; --------------------------------------------------------
	; test remu
	; --------------------------------------------------------

	remu $4, $15, $13
	add $5, $0, 1
	jal assertEqual

	remu $4, $15, $15
	add $5, $0, 0
	jal assertEqual

	remu $4, $15, $14
	add $5, $0, 123
	jal assertEqual

	remu $4, $15, 5
	add $5, $0, 3
	jal assertEqual

	remu $4, $15, -1
	add $5, $0, 123
	jal assertEqual

	remu $4, $18, 0xfff0
	add $5, $0, 0x5837
	jal assertEqual

	; --------------------------------------------------------
	; start third row
	; --------------------------------------------------------

	jal newline

	; --------------------------------------------------------
	; test and
	; --------------------------------------------------------

	and $4, $17, $18
	ldhi $5, 0x20200000
	or $5, $5, 0x4422
	jal assertEqual

	and $4, $18, 0xf5a3
	ldhi $5, 0x00000000
	or $5, $5, 0x4523
	jal assertEqual

	and $4, $18, 0xffff
	ldhi $5, 0x00000000
	or $5, $5, 0x4567
	jal assertEqual

	; --------------------------------------------------------
	; test or
	; --------------------------------------------------------

	or $4, $17, $18
	ldhi $5, 0xab730000
	or $5, $5, 0xf777
	jal assertEqual

	or $4, $18, 0xf5a3
	ldhi $5, 0xa1230000
	or $5, $5, 0xf5e7
	jal assertEqual

	or $4, $18, 0xffff
	ldhi $5, 0xa1230000
	or $5, $5, 0xffff
	jal assertEqual

	; --------------------------------------------------------
	; test xor
	; --------------------------------------------------------

	xor $4, $17, $18
	ldhi $5, 0x8b530000
	or $5, $5, 0xb355
	jal assertEqual

	xor $4, $18, 0xf5a3
	ldhi $5, 0xa1230000
	or $5, $5, 0xb0c4
	jal assertEqual

	xor $4, $18, 0xffff
	ldhi $5, 0xa1230000
	or $5, $5, 0xba98
	jal assertEqual

	; --------------------------------------------------------
	; test xnor
	; --------------------------------------------------------

	xnor $4, $17, $18
	ldhi $5, 0x74ac0000
	or $5, $5, 0x4caa
	jal assertEqual

	xnor $4, $18, 0xf5a3
	ldhi $5, 0x5edc0000
	or $5, $5, 0x4f3b
	jal assertEqual

	xnor $4, $18, 0xffff
	ldhi $5, 0x5edc0000
	or $5, $5, 0x4567
	jal assertEqual

	; --------------------------------------------------------
	; test sll
	; --------------------------------------------------------

	sll $4, $17, $18
	ldhi $5, 0x387b0000
	or $5, $5, 0x1900
	jal assertEqual

	sll $4, $18, 0xf5a3
	ldhi $5, 0x091a0000
	or $5, $5, 0x2b38
	jal assertEqual

	; --------------------------------------------------------
	; test slr
	; --------------------------------------------------------

	slr $4, $17, $18
	ldhi $5, 0x00540000
	or $5, $5, 0xe1ec
	jal assertEqual

	slr $4, $18, $18
	ldhi $5, 0x01420000
	or $5, $5, 0x468a
	jal assertEqual

	slr $4, $17, 0xf5a3
	ldhi $5, 0x054e0000
	or $5, $5, 0x1ec6
	jal assertEqual

	slr $4, $18, 0xf5a3
	ldhi $5, 0x14240000
	or $5, $5, 0x68ac
	jal assertEqual

	; --------------------------------------------------------
	; test sar
	; --------------------------------------------------------

	sar $4, $17, $18
	ldhi $5, 0x00540000
	or $5, $5, 0xe1ec
	jal assertEqual

	sar $4, $18, $18
	ldhi $5, 0xff420000
	or $5, $5, 0x468a
	jal assertEqual

	sar $4, $17, 0xf5a3
	ldhi $5, 0x054e0000
	or $5, $5, 0x1ec6
	jal assertEqual

	sar $4, $18, 0xf5a3
	ldhi $5, 0xf4240000
	or $5, $5, 0x68ac
	jal assertEqual

	; --------------------------------------------------------
	; finish test program
	; --------------------------------------------------------

	jal newline
hang:
	j hang

assertEqual:
	beq $4, $5, assertSuccess
	j assertFailure

assertSuccess:
	stw $27, $26, 0
	j assertFinish

assertFailure:
	stw $28, $26, 0
	j assertFinish

assertFinish:
	add $26, $26, 4
	add $27, $27, 1
	jr $31

newline:
	stw $29, $26, 0
	sub $4, $0, 1
	xor $4, $4, 511
	and $26, $26, $4
	add $26, $26, 512
	add $27, $0, 0x0f00 + 'a'
	jr $31




