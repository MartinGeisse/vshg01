
.nosyn

start:
	ldhi $1, 0xf0300000
	ldhi $2, messagePrefix
	or $2, $2, messagePrefix
	ldhi $3, messageSuffix
	or $3, $3, messageSuffix

mainLoop:
	jal readByte
	add $29, $20, 0
	add $21, $2, 0
	jal writeString
	add $20, $29, 0
	jal writeByte
	add $21, $3, 0
	jal writeString
	j mainLoop

readByte:
	ldw $8, $1, 0
	and $8, $8, 1
	beq $8, $0, readByte
	ldw $20, $1, 4
	jr $31

writeByte:
	ldw $8, $1, 8
	and $8, $8, 1
	beq $8, $0, writeByte
	stw $20, $1, 12
	jr $31

writeString:
	ldb $20, $21, 0
	bne $20, $0, writeStringContinued
	jr $31
writeStringContinued:
	add $21, $21, 1
	add $30, $31, 0
	jal writeByte
	add $31, $30, 0
	j writeString

hang:
	j hang

messagePrefix:
	.byte "You typed: '"
	.byte 0
	
messageSuffix:
	.byte "'"
	.byte 10
	.byte 0
