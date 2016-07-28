
.nosyn

start:
	ldhi $1, 0xf0100000
	ldhi $2, message
	or $2, $2, message
loop:
	ldb $3, $2, 0
	beq $3, $0, hang
	or $3, $3, 0x0f00
	stw $3, $1, 0
	add $1, $1, 4
	add $2, $2, 1
	j loop

hang:
	j hang

message:
	.byte "Hello World"
	.byte 0
	.byte "foo"
