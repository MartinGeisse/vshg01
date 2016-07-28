/*
 * list file or directory
 */

#include <sys/tim.h>
#include <sys/off.h>
#include <sys/blk.h>
#include <sys/dev.h>
#include <sys/ino.h>
#include <sys/stat.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <eos32sys.h>

extern char **environ;

void statTest(char *filename) {
	int fd, code;
	struct stat result;
	printf("stat results for %s:\n", filename);
	
	// fstat()
	fd = open(filename, 1);
	if (fd < 0) {
		printf("file not found\n");
		return;
	}
	code = fstat(fd, &result);
	close(fd);
	if (code) {
		printf("could not fstat()\n");
		return;
	}
	
	// stat()
	/*
	if (stat(filename, &result)) {
		printf("file not found\n");
		return;
	}
	*/
	
	printf("deviceId:          %d\n", result.st_dev);
	printf("inodeId:           %d\n", result.st_ino);
	printf("mode:              %d\n", result.st_mode);
	printf("linkCount:         %d\n", result.st_nlink);
	printf("userId:            %d\n", result.st_uid);
	printf("groupId:           %d\n", result.st_gid);
	printf("targetDeviceId:    %d\n", result.st_rdev);
	printf("size:              %d\n", result.st_size);
	printf("lastAccessedTime:  %d\n", result.st_atime);
	printf("lastModifiedTime:  %d\n", result.st_mtime);
	printf("lastChangedTime:   %d\n", result.st_ctime);
}

int main(int argc, char *argv[]) {
	char buf[256];
	int i;
	int cpid = 0;

	/*
	printf("Hi, what's your name?\n");
	gets(buf);
	printf("Hello, %s!", buf);
	*/
	
	// --- child terminates first, parent waits at the end ---
	/*
	for (i=0; i<100; i++) {
		if (i == 20) {
			cpid = fork();
		} else if (i == 40) {
			if (cpid == 0) {
				return 23;
			}
		} else if (i == 80) {
			int pid, status;
			pid = wait(&status);
			printf("--> pid %d, status %d\n", pid, status);
		}
		printf("* %d:%d\n", cpid, i);
	}
	*/

	// --- parent waits first, child terminates later ---
	/*
	for (i=0; i<100; i++) {
		if (i == 20) {
			cpid = fork();
		} else if (i == 40) {
			if (cpid != 0) {
				int pid, status;
				pid = wait(&status);
				printf("--> pid %d, status %d\n", pid, status);
			}
		} else if (i == 80) {
			if (cpid == 0) {
				return 23;
			}
		}
		printf("* %d:%d\n", cpid, i);
	}
	*/

	// --- parent kills child ---
	/*
	for (i=0; i<100; i++) {
		if (i == 20) {
			cpid = fork();
		} else if (i == 40) {
			if (cpid != 0) {
				kill(cpid);
			}
		}
		printf("* %d:%d\n", cpid, i);
	}
	*/
	
	// --- file I/O ---
	/*
	chdir("resource");
	chdir("..");
	{
		int fd = open("test-output.txt", 3);
		char *msg = "Hello World\n";
		char *msg2 = "Foo\n";
		write(fd, msg, strlen(msg));
		lseek(fd, 6, 0);
		write(fd, msg2, strlen(msg2));
		close(fd);
	}
	*/
	
	// --- file times ---
	/*
	int times[2];
	times[0] = 0;
	times[1] = 0;
	utime("test-output.txt", times);
	*/
	
	// --- system time ---
	/*
	int time1, time2;
	time1 = time(&time2);
	printf("time: %d / %d\n", time1, time2);
	*/
	
	// --- formatted system time ---
	/*
	struct timeb timeData;
	ftime(&timeData);
	printf("timestamp: %d\n", timeData.time);
	printf("milliseconds: %d\n", timeData.millitm);
	printf("timezone: %d\n", timeData.timezone);
	printf("DST: %s\n", (timeData.dstflag ? "yes" : "no"));
	*/
	
	// --- file meta-data ---
	/*
	statTest("test-output.txt");
	statTest(".classpath");
	statTest("test-output.txt");
	statTest("foo.txt");
	*/
	
	// --- getpid ---
	/*
	int delay = ((cpid = fork()) ? 100 : 0);
	for (i=0; i<delay; i++) {
		printf("");
	}
	printf("PID: %d, child PID: %d, parent PID: %d\n", getpid(), cpid, getppid());
	*/
	
	// --- break ---
	/*
	int *p = (int*)(0x101000);
	brk((void*)0x101000);
	*p = 0;
	*p */

	/*
	// --- pipeline ---
	int pipe1[2];
	int pipe2[2];
	pipe(pipe1);
	pipe(pipe2);

	// split into lines
	if (!fork()) {
		char c;
		int i = 0;
		while (1) {
			if (read(0, &c, 1) < 1) {
				return 0;
			}
			write(pipe1[1], &c, 1);
			i++;
			if (i == 10) {
				i = 0;
				write(pipe1[1], "\n", 1);
			}
		}
	}

	// apply wrapper
	if (!fork()) {
		char c;
		int newline = 1;
		while (1) {
			if (read(pipe1[0], &c, 1) < 1) {
				return 0;
			}
			if (newline) {
				write(pipe2[1], "line: ", 6);
				newline = 0;
			}
			if (c == '\n') {
				write(pipe2[1], "*", 1);
				newline = 1;
			}
			write(pipe2[1], &c, 1);
		}
	}

	// apply numbering
	if (!fork()) {
		char c;
		int line = 1, newline = 1;
		while (1) {
			if (read(pipe2[0], &c, 1) < 1) {
				return 0;
			}
			if (newline) {
				char buf[256];
				sprintf(buf, "%d...", line);
				write(1, buf, strlen(buf));
				newline = 0;
				line++;
			}
			if (c == '\n') {
				newline = 1;
			}
			write(1, &c, 1);
		}
	}
	*/
	
	// exec
	char *env[] = {
		"foo=hier",
		"bar=da",
		"fupp=woanders",
		NULL
	};
	char *args[] = {
		"eins",
		"zwei",
		"drei",
		NULL
	};
	char *test[] = {
		"foo", "bar", "baz", NULL
	};
	char **t;
	
	printf("--- environ ---");
	for (t = environ; *t != NULL; t++) {
		printf("* %s\n", *t);
	}

	printf("--- info ---\n");
	for (t = test; *t != NULL; t++) {
		char *name = *t;
		char *value = getenv(name);
		printf("%s: %s\n", name, (value != NULL ? value : "(NULL)"));
	}
	
	execve("../ecobuild/build/modules/eos32/userland/coherence/hello/hello", args, env);

	return 0;
}
