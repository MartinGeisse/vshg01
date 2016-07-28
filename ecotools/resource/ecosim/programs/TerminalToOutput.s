
.nosyn

start:
	ldhi $1, 0xf0300000
	ldhi $2, 0xff000000

mainLoop:
	ldw $3, $1, 0
	and $3, $3, 1
	beq $3, $0, mainLoop
	ldw $3, $1, 4
	stw $3, $2, 0
	j mainLoop
