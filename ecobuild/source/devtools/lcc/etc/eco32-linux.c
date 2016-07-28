/* Code for ECO32 under Linux, University of Applied Sciences Giessen */

#include <string.h>
#include <lccdir.h>

char *suffixes[] = { ".c", ".i", ".s", ".o", ".out", 0 };

char inputs[256] = "";

char *cpp[] = {
	LCCDIR "cpp",
	"-D__STDC__=1",
	"-DLANGUAGE_C",
	"-D_LANGUAGE_C",
	"-DECO32",
	"-D_ECO32",
	"-Deco32",
	"-DUNIX",
	"-D_UNIX",
	"-Dunix",
	"$1",			/* preprocessor include directory */
	"$2",			/* preprocessor input file */
	"$3",			/* preprocessor output file */
	0
};

char *com[] =  {
	LCCDIR "rcc",
	"-target=eco32/linux",
	"$1",			/* other options handed through */
	"$2",			/* compiler input file */
	"$3",			/* compiler output file */
	"",
	0
};

char *include[] = { "-I" LCCDIR "../include", 0 };

char *as[] = {
	LCCDIR "as",
	"-o", "$3",		/* assembler output file */
	"$1",			/* other options handed through */
	"$2",			/* assembler input file */
	0
};

char *ld[] = {
	LCCDIR "ld",
	"-o", "$3",		/* linker output file */
	"$1",			/* other options handed through */
	LCCDIR "../lib/c0.o",
	"$2",
	LCCDIR "../lib/setjmp.o",
	LCCDIR "../lib/eos32sys.o",
	LCCDIR "../lib/eos32lib.o",
	LCCDIR "../lib/assert.o",
	LCCDIR "../lib/ctype.o",
	LCCDIR "../lib/errno.o",
	LCCDIR "../lib/math.o",
	LCCDIR "../lib/signal.o",
	LCCDIR "../lib/stdio.o",
	LCCDIR "../lib/stdlib.o",
	LCCDIR "../lib/string.o",
	LCCDIR "../lib/time.o",
	LCCDIR "../lib/c1.o",
	LCCDIR "../lib/tui_core.o",
	0
};

extern char *concat(char *, char *);

int option(char *arg) {
	if (strncmp(arg, "-lccdir=", 8) == 0) {
		cpp[0] = concat(&arg[8], "/cpp");
		include[0] = concat("-I", concat(&arg[8], "/include"));
		com[0] = concat(&arg[8], "/rcc");
		ld[18] = concat("-L", &arg[8]);
	} else if (strcmp(arg, "-nolib") == 0) {
		ld[4] = ld[5];
		ld[5] = 0;
	} else {
		return 0;
	}
	return 1;
}
