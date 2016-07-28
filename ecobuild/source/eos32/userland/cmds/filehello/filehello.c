/*
 * list file or directory
 */

// #include <fcntl.h>
// #include <unistd.h>
#include <eos32.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char pattern[] = {
	'H', 0x0f, 'e', 0x09, 'l', 0x1c, 'l', 0x2d, 'o', 0x0f,
};

char buffer[5];

int main(int argc, char *argv[]) {
	int fd, i;
	
	// check command line argument
	if (argc != 2) {
		printf("usage: filehello targetfile\n");
		return 1;
	}
	
	// open the file
	fd = open(argv[1], O_RDWR);
	if (fd < 0) {
		printf("could not open file\n");
		return 1;
	}
	
	//
	write(fd, pattern, 5);
	write(fd, pattern + 5, 5);
	lseek(fd, 3, SEEK_SET);
	read(fd, buffer, 5);
	close(fd);
	for (i=0; i<5; i++) {
		printf("%d, ", buffer[i]);
	}
	printf("\n");

	// end of test
	close(fd);
	return 0;
	
}
