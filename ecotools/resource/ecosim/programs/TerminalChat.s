
.nosyn

start:
	ldhi $1, 0xf0300000

mainLoop:
	ldw $2, $1, 0
	and $2, $2, 1
	bne $2, $0, from0to1
	ldw $2, $1, 16
	and $2, $2, 1
	bne $2, $0, from1to0
	j mainLoop
	
from0to1:
	ldw $2, $1, 24
	and $2, $2, 1
	beq $2, $0, from0to1
	ldw $2, $1, 4
	stw $2, $1, 28
	j mainLoop

from1to0:
	ldw $2, $1, 8
	and $2, $2, 1
	beq $2, $0, from1to0
	ldw $2, $1, 20
	stw $2, $1, 12
	j mainLoop
