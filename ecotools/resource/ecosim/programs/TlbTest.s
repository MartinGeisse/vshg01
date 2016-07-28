
.nosyn

start:
	ldhi $1, 0xabcd0000
	or $1, $1, 0xef01
	mvts $1, 2
	ldhi $1, 0x20000000
	or $1, $1, 1
	mvts $1, 3
	
	tbwr
	tbwr

	ldhi $1, 0xabcd0000
	or $1, $1, 0xe000
	ldw $0, $1, 0
	tbwr
	
	or $1, $0, 13
	mvts $1, 1
	tbwi

	or $1, $0, 14
	mvts $1, 1
	tbri
	mvfs $1, 2
	mvfs $2, 3
	
	ldhi $1, 0x12340000
	or $1, $1, 0x5678
	ldhi $2, 0xdead0000
	or $2, $2, 0xbeef
	stw $2, $1, 0

hang:
	j hang
