/*
 * memtest.c -- ECO32 memory test
 */


#define MEM_ADDR	((void *) (0xC0000000 + 0x10000 + 0x2000))
#define MEM_SIZE	(0x04000000 - 0x10000 - 0x2000)
#define PASSES		3


/**************************************************************/


void putChar(int c);


void printChr(char c) {
  if (c == '\n') {
    putChar('\r');
  }
  putChar(c);
}


void printStr(char *s) {
  while (*s != '\0') {
    printChr(*s++);
  }
}


void printInt(unsigned int n) {
  unsigned int a;

  a = n / 10;
  if (a != 0) {
    printInt(a);
  }
  printChr(n % 10 + '0');
}


/**************************************************************/


unsigned int randomNumber;


void setRandomNumber(unsigned int seed) {
  randomNumber = seed;
}


unsigned int getRandomNumber(void) {
  randomNumber = randomNumber * (unsigned) 1103515245 + (unsigned) 12345;
  return randomNumber;
}


/**************************************************************/


void writeWords(void *start,
                unsigned int numWords,
                unsigned int seed) {
  unsigned int *addr;
  unsigned int rn;

  setRandomNumber(seed);
  addr = (unsigned int *) start;
  while (numWords -= 1) {
    rn = getRandomNumber();
    *addr++ = rn;
  }
}


void writeHalfs(void *start,
                unsigned int numHalfs,
                unsigned int seed) {
  unsigned short *addr;
  unsigned int rn;

  setRandomNumber(seed);
  addr = (unsigned short *) start;
  while (numHalfs -= 2) {
    rn = getRandomNumber();
    *addr++ = (rn >> 16) & 0x0000FFFF;
    *addr++ = (rn >>  0) & 0x0000FFFF;
  }
}


void writeBytes(void *start,
                unsigned int numBytes,
                unsigned int seed) {
  unsigned char *addr;
  unsigned int rn;

  setRandomNumber(seed);
  addr = (unsigned char *) start;
  while (numBytes -= 4) {
    rn = getRandomNumber();
    *addr++ = (rn >> 24) & 0x000000FF;
    *addr++ = (rn >> 16) & 0x000000FF;
    *addr++ = (rn >>  8) & 0x000000FF;
    *addr++ = (rn >>  0) & 0x000000FF;
  }
}


unsigned int readWords(void *start,
                       unsigned int numWords,
                       unsigned int seed) {
  unsigned int errors;
  unsigned int *addr;
  unsigned int rn;

  errors = 0;
  setRandomNumber(seed);
  addr = (unsigned int *) start;
  while (numWords -= 1) {
    rn = getRandomNumber();
    if (*addr++ != rn) {
      errors++;
    }
  }
  return errors;
}


unsigned int readHalfs(void *start,
                       unsigned int numHalfs,
                       unsigned int seed) {
  unsigned int errors;
  unsigned short *addr;
  unsigned int rn;

  errors = 0;
  setRandomNumber(seed);
  addr = (unsigned short *) start;
  while (numHalfs -= 2) {
    rn = getRandomNumber();
    if (*addr++ != ((rn >> 16) & 0x0000FFFF)) {
      errors++;
    }
    if (*addr++ != ((rn >>  0) & 0x0000FFFF)) {
      errors++;
    }
  }
  return errors;
}


unsigned int readBytes(void *start,
                       unsigned int numBytes,
                       unsigned int seed) {
  unsigned int errors;
  unsigned char *addr;
  unsigned int rn;

  errors = 0;
  setRandomNumber(seed);
  addr = (unsigned char *) start;
  while (numBytes -= 4) {
    rn = getRandomNumber();
    if (*addr++ != ((rn >> 24) & 0x000000FF)) {
      errors++;
    }
    if (*addr++ != ((rn >> 16) & 0x000000FF)) {
      errors++;
    }
    if (*addr++ != ((rn >>  8) & 0x000000FF)) {
      errors++;
    }
    if (*addr++ != ((rn >>  0) & 0x000000FF)) {
      errors++;
    }
  }
  return errors;
}


/**************************************************************/


void memtest(void *start, unsigned int size, int passes) {
  int i;
  int j;
  unsigned int seed;

  setRandomNumber(10007);
  for (i = 1; i <= passes; i++) {
    printStr("Pass ");
    printInt(i);
    printStr("\n");
    /* -------------------- */
    printStr("  writing words...\n");
    for (j = 0; j < i; j++) {
      getRandomNumber();
    }
    seed = getRandomNumber();
    writeWords(start, size / 4, seed);
    printStr("    reading words: ");
    printInt(readWords(start, size / 4, seed));
    printStr(" errors\n");
    printStr("    reading halfs: ");
    printInt(readHalfs(start, size / 2, seed));
    printStr(" errors\n");
    printStr("    reading bytes: ");
    printInt(readBytes(start, size / 1, seed));
    printStr(" errors\n");
    /* -------------------- */
    printStr("  writing halfs...\n");
    for (j = 0; j < i; j++) {
      getRandomNumber();
    }
    seed = getRandomNumber();
    writeHalfs(start, size / 2, seed);
    printStr("    reading words: ");
    printInt(readWords(start, size / 4, seed));
    printStr(" errors\n");
    printStr("    reading halfs: ");
    printInt(readHalfs(start, size / 2, seed));
    printStr(" errors\n");
    printStr("    reading bytes: ");
    printInt(readBytes(start, size / 1, seed));
    printStr(" errors\n");
    /* -------------------- */
    printStr("  writing bytes...\n");
    for (j = 0; j < i; j++) {
      getRandomNumber();
    }
    seed = getRandomNumber();
    writeBytes(start, size / 1, seed);
    printStr("    reading words: ");
    printInt(readWords(start, size / 4, seed));
    printStr(" errors\n");
    printStr("    reading halfs: ");
    printInt(readHalfs(start, size / 2, seed));
    printStr(" errors\n");
    printStr("    reading bytes: ");
    printInt(readBytes(start, size / 1, seed));
    printStr(" errors\n");
  }
}


/**************************************************************/


int main(void) {
  printStr("\nECO32 memory test started\n\n");
  memtest(MEM_ADDR, MEM_SIZE, PASSES);
  printStr("\nECO32 memory test finished\n");
  return 0;
}
