
.nosyn

start:
	ldhi $1, 0xc0000000
loop:
	ldw $2, $1, 0
	add $2, $2, 1
	stw $2, $1, 0
	j loop
