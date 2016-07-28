
.nosyn
.code

start:

	; test a: test LDHI
	ldhi $1, 0xf0100000
	
	; test a: test ADDI 0 + imm
	add $2, $0, 0x0f00 + 'a'
	
	; test a: test STW reg + 0
	stw $2, $1, 0
	
	; test b: test STW reg + imm
	add $2, $0, 0x0f00 + 'b'
	stw $2, $1, 4
	
	; test c: test ADDI reg + imm
	add $2, $2, 1
	stw $2, $1, 8
	
	; test d: test ADDI reg + (-imm)
	add $2, $0, 0x0f00 + 'f'
	add $2, $2, -2
	stw $2, $1, 12
	
	; test e: test STW reg + (-imm)
	ldhi $1, 0xf0100000
	add $1, $1, 0x0020
	add $2, $2, 1
	stw $2, $1, -16

	; test f: test ADD reg + reg
	add $2, $0, -12
	add $1, $1, $2
	add $2, $0, 0x0f00 + 'f'
	stw $2, $1, 0
	
	; test g: test SUB reg - imm
	add $2, $0, 0x0f00 + 'i'
	sub $2, $2, 2
	stw $2, $1, 4

	; test h: test SUB reg - (-imm)
	sub $2, $2, -1
	stw $2, $1, 8

	; test i: test SUB reg - reg
	add $3, $3, -1
	sub $2, $2, $3
	stw $2, $1, 12

	; test j: test J forward
	add $2, $2, 1
	stw $2, $1, 16
	j skip_j_1
	add $3, $0, 0x0f00 + '*'
	stw $3, $1, 16
skip_j_1:

	; test k: test J backward
	add $2, $2, 1
	j skip_j_2
	add $2, $0, 0x0f00 + '*'
	add $2, $0, 0x0f00 + '*'
	add $2, $0, 0x0f00 + '*'
skip_j_3:
	j skip_j_4
	add $2, $0, 0x0f00 + '*'
	add $2, $0, 0x0f00 + '*'
	add $2, $0, 0x0f00 + '*'
	j skip_j_4
skip_j_2:
	j skip_j_3
skip_j_4:
	stw $2, $1, 20

	; test l: test BEQ
	add $3, $0, 3
	add $4, $0, 4
	add $5, $0, 5
	add $2, $0, 4
	beq $2, $3, beq_1
	beq $2, $4, beq_2
	beq $2, $5, beq_3
	add $2, $0, 0x0f00 + '*'
	j beq_4
beq_1:
	add $2, $0, 0x0f00 + '*'
	j beq_4
beq_2:
	add $2, $0, 0x0f00 + 'l'
	j beq_4
beq_3:
	add $2, $0, 0x0f00 + '*'
	j beq_4
beq_4:
	stw $2, $1, 24
	
	; test m: test BNE
	add $2, $0, 0x0f00 + 'm'
	add $10, $0, 3
	bne $10, $4, bne_1
	add $2, $0, 0x0f00 + '*'
bne_1:
	stw $2, $1, 28

	; test n: test BNE
	add $2, $0, 0x0f00 + '*'
	add $10, $0, 4
	bne $10, $4, bne_2
	add $2, $0, 0x0f00 + 'n'
bne_2:
	stw $2, $1, 32

	; test o: test BNE
	add $2, $0, 0x0f00 + 'o'
	add $10, $0, 5
	bne $10, $4, bne_3
	add $2, $0, 0x0f00 + '*'
bne_3:
	stw $2, $1, 36

	; test p: test BLT
	add $2, $0, 0x0f00 + 'p'
	add $10, $0, 3
	blt $10, $4, blt_1
	add $2, $0, 0x0f00 + '*'
blt_1:
	stw $2, $1, 40

	; test q: test BLT
	add $2, $0, 0x0f00 + '*'
	add $10, $0, 4
	blt $10, $4, blt_2
	add $2, $0, 0x0f00 + 'q'
blt_2:
	stw $2, $1, 44

	; test r: test BLT
	add $2, $0, 0x0f00 + '*'
	add $10, $0, 5
	blt $10, $4, blt_3
	add $2, $0, 0x0f00 + 'r'
blt_3:
	stw $2, $1, 48

	; test s: test BLE
	add $2, $0, 0x0f00 + 's'
	add $10, $0, 3
	ble $10, $4, ble_1
	add $2, $0, 0x0f00 + '*'
ble_1:
	stw $2, $1, 52

	; test t: test BLE
	add $2, $0, 0x0f00 + 't'
	add $10, $0, 4
	ble $10, $4, ble_2
	add $2, $0, 0x0f00 + '*'
ble_2:
	stw $2, $1, 56

	; test u: test BLE
	add $2, $0, 0x0f00 + '*'
	add $10, $0, 5
	ble $10, $4, ble_3
	add $2, $0, 0x0f00 + 'u'
ble_3:
	stw $2, $1, 60

	; test v: test BGT
	add $2, $0, 0x0f00 + '*'
	add $10, $0, 3
	bgt $10, $4, bgt_1
	add $2, $0, 0x0f00 + 'v'
bgt_1:
	stw $2, $1, 64

	; test w: test BGT
	add $2, $0, 0x0f00 + '*'
	add $10, $0, 4
	bgt $10, $4, bgt_2
	add $2, $0, 0x0f00 + 'w'
bgt_2:
	stw $2, $1, 68

	; test x: test BGT
	add $2, $0, 0x0f00 + 'x'
	add $10, $0, 5
	bgt $10, $4, bgt_3
	add $2, $0, 0x0f00 + '*'
bgt_3:
	stw $2, $1, 72

	; test y: test BGE
	add $2, $0, 0x0f00 + '*'
	add $10, $0, 3
	bge $10, $4, bge_1
	add $2, $0, 0x0f00 + 'y'
bge_1:
	stw $2, $1, 76

	; test z: test BGE
	add $2, $0, 0x0f00 + 'z'
	add $10, $0, 4
	bge $10, $4, bge_2
	add $2, $0, 0x0f00 + '*'
bge_2:
	stw $2, $1, 80

	; test 1: test BGE
	add $2, $0, 0x0f00 + '1'
	add $10, $0, 5
	bge $10, $4, bge_3
	add $2, $0, 0x0f00 + '*'
bge_3:
	stw $2, $1, 84
	
	; cut store address increment chain in case we want to change this test case
	add $1, $1, 88
	add $2, $0, 0x0f00 + '2'
	
	; test 2: test JAL
	jal test_2_sub
	
	; test 3: test JALR (non-31)
	ldhi $10, test_3_sub
	add $10, $10, test_3_sub
	jalr $10

	; test 4: test JR
	add $2, $0, 0x0f00 + '4'
	ldhi $10, test_4_target
	add $10, $10, test_4_target
	jr $10
	add $2, $0, 0x0f00 + '*'
test_4_target:
	stw $2, $1, 8

	; ------------------------------------------------------------
	; test 5: load instructions
	; ------------------------------------------------------------
	add $2, $0, 0x0f00 + '5'
	ldhi $3, test_5_data
	add $3, $3, test_5_data
	
	; LDW +0
	ldhi $5, 0x11220000
	or $5, $5, 0x3344
	ldw $4, $3, 0
	bne $4, $5, test_5_fail

	; LDW +4
	ldhi $5, 0x11220000
	or $5, $5, 0xccdd
	ldw $4, $3, 4
	bne $4, $5, test_5_fail

	; LDW +8
	ldhi $5, 0xaabb0000
	or $5, $5, 0x3344
	ldw $4, $3, 8
	bne $4, $5, test_5_fail

	; LDW +12
	ldhi $5, 0xaabb0000
	or $5, $5, 0xccdd
	ldw $4, $3, 12
	bne $4, $5, test_5_fail

	; LDH +4
	ldhi $5, 0x00000000
	or $5, $5, 0x1122
	ldh $4, $3, 4
	bne $4, $5, test_5_fail

	; LDH +6
	ldhi $5, 0xffff0000
	or $5, $5, 0xccdd
	ldh $4, $3, 6
	bne $4, $5, test_5_fail

	; LDH +8
	ldhi $5, 0xffff0000
	or $5, $5, 0xaabb
	ldh $4, $3, 8
	bne $4, $5, test_5_fail

	; LDH +10
	ldhi $5, 0x00000000
	or $5, $5, 0x3344
	ldh $4, $3, 10
	bne $4, $5, test_5_fail

	; LDHU +4
	ldhi $5, 0x00000000
	or $5, $5, 0x1122
	ldhu $4, $3, 4
	bne $4, $5, test_5_fail

	; LDHU +6
	ldhi $5, 0x00000000
	or $5, $5, 0xccdd
	ldhu $4, $3, 6
	bne $4, $5, test_5_fail

	; LDHU +8
	ldhi $5, 0x00000000
	or $5, $5, 0xaabb
	ldhu $4, $3, 8
	bne $4, $5, test_5_fail

	; LDHU +10
	ldhi $5, 0x00000000
	or $5, $5, 0x3344
	ldhu $4, $3, 10
	bne $4, $5, test_5_fail

	; LDB +4
	ldhi $5, 0x00000000
	or $5, $5, 0x0011
	ldb $4, $3, 4
	bne $4, $5, test_5_fail

	; LDB +5
	ldhi $5, 0x00000000
	or $5, $5, 0x0022
	ldb $4, $3, 5
	bne $4, $5, test_5_fail

	; LDB +6
	ldhi $5, 0xffff0000
	or $5, $5, 0xffcc
	ldb $4, $3, 6
	bne $4, $5, test_5_fail

	; LDB +7
	ldhi $5, 0xffff0000
	or $5, $5, 0xffdd
	ldb $4, $3, 7
	bne $4, $5, test_5_fail

	; LDBU +4
	ldhi $5, 0x00000000
	or $5, $5, 0x0011
	ldbu $4, $3, 4
	bne $4, $5, test_5_fail

	; LDBU +5
	ldhi $5, 0x00000000
	or $5, $5, 0x0022
	ldbu $4, $3, 5
	bne $4, $5, test_5_fail

	; LDBU +6
	ldhi $5, 0x00000000
	or $5, $5, 0x00cc
	ldbu $4, $3, 6
	bne $4, $5, test_5_fail

	; LDBU +7
	ldhi $5, 0x00000000
	or $5, $5, 0x00dd
	ldbu $4, $3, 7
	bne $4, $5, test_5_fail

	j test_5_finish
test_5_fail:
	add $2, $0, 0x0f00 + '*'
test_5_finish:
	stw $2, $1, 12

	; ------------------------------------------------------------
	; end of test 5
	; ------------------------------------------------------------
	
	; tests finished, print end symbol and lock up
	add $2, $0, 0x0f00 + '!'
	stw $2, $1, 16
	
hang:
	j hang

test_2_sub:
	stw $2, $1, 0
	jr $31

test_3_sub:
	add $2, $2, 1
	stw $2, $1, 4
	jr $31

test_5_data:
	.byte 0x11, 0x22, 0x33, 0x44, 0x11, 0x22, 0xcc, 0xdd, 0xaa, 0xbb, 0x33, 0x44, 0xaa, 0xbb, 0xcc, 0xdd
