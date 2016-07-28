
.nosyn

start:
	ldhi $1, 0xf0200000
	ldhi $2, 0xf0100000
	add $5, $0, 0
	add $6, $0, 0

waitForKey:
	ldw $3, $1, 0
	and $3, $3, 1
	beq $3, $0, waitForKey
printScanCode:
	ldw $3, $1, 4
	slr $4, $3, 4
	and $4, $4, 0x0f
	jal printHexDigit
	and $4, $3, 0x0f
	jal printHexDigit
	add $4, $0, 32
	jal printCharacter
	j waitForKey
	
printHexDigit:
	add $7, $0, 10
	blt $4, $7, printDecDigit
	sub $4, $4, 10
	add $4, $4, 'a'
	j printCharacter
	
printDecDigit:
	add $4, $4, '0'
	j printCharacter
	
printCharacter:
	sll $7, $5, 2
	sll $8, $6, 9
	add $7, $7, $8
	add $7, $7, $2
	add $4, $4, 0x0f00
	stw $4, $7, 0
	
	add $5, $5, 1
	add $7, $0, 78
	bne $5, $7, return
	add $5, $0, 0
	add $6, $6, 1

return:
	jr $31
