
#ifndef BASELIB_MEMORY_H
#define BASELIB_MEMORY_H

/*
 * Write an 8-bit value into a memory position.
 */
void baselib_memory_poke8(unsigned int address, char value);

/*
 * Read an 8-bit value from a memory position.
 */
char baselib_memory_peek8(unsigned int address);

/*
 * Write a 16-bit value into an aligned memory position.
 */
void baselib_memory_poke16(unsigned int address, short value);

/*
 * Read a 16-bit value from an aligned memory position.
 */
short baselib_memory_peek16(unsigned int address);

/*
 * Write a 32-bit value into an aligned memory position.
 */
void baselib_memory_poke32(unsigned int address, int value);

/*
 * Read a 32-bit value from an aligned memory position.
 */
int baselib_memory_peek32(unsigned int address);

/*
 * Copy n bytes from src to dest and return dest.
 */
void *memcpy(void *dest, const void *src, int n);

/*
 * Copy n bytes from src to dest.
 */
void baselib_memory_copy8(char *dest, const char *src, int n);

/*
 * Copy n double-bytes from src to dest. Both
 * src and dest must be 2-aligned.
 */
void baselib_memory_copy16(short *dest, const short *src, int n);

/*
 * Copy n tetra-bytes from src to dest. Both
 * src and dest must be 4-aligned.
 */
void baselib_memory_copy32(int *dest, const int *src, int n);

/*
 * Fill n bytes with v, starting at dest, and return dest.
 */
void *memset(void *dest, char v, int n);

/*
 * Fill n bytes with v, starting at dest.
 */
void baselib_memory_fill8(char *dest, char v, int n);

/*
 * Fill n double-bytes with v, starting at dest.
 */
void baselib_memory_fill16(short *dest, short v, int n);

/*
 * Fill n tetra-bytes with v, starting at dest.
 */
void baselib_memory_fill32(int *dest, int v, int n);

#endif
