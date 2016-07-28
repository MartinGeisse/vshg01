
#ifndef __KERNEL_POKE_H__
#define __KERNEL_POKE_H__

/**
 * Writes an 8-bit value into a memory position.
 */
void writeMemory8 (unsigned int address, char value);

/**
 * Reads an 8-bit value from a memory position.
 */
char readMemory8 (unsigned int address);

/**
 * Writes a 16-bit value into an aligned memory position.
 */
void writeMemory16 (unsigned int address, short value);

/**
 * Reads a 16-bit value from an aligned memory position.
 */
short readMemory16 (unsigned int address);

/**
 * Writes a 32-bit value into an aligned memory position.
 */
void writeMemory32 (unsigned int address, int value);

/**
 * Reads a 32-bit value from an aligned memory position.
 */
int readMemory32 (unsigned int address);

/**
 * Copies n bytes from src to dest.
 */
void copyMemory8 (char *dest, const char *src, int n);

/**
 * Copies n double-bytes from src to dest. Both
 * src and dest must be 2-aligned.
 */
void copyMemory16 (short *dest, const short *src, int n);

/**
 * Copies n tetra-bytes from src to dest. Both
 * src and dest must be 4-aligned.
 */
void copyMemory32 (int *dest, const int *src, int n);

/**
 * Fills n bytes with v, starting at dest.
 */
void fillMemory8 (char *dest, char v, int n);

/**
 * Fills n double-bytes with v, starting at dest, which must be 2-aligned.
 */
void fillMemory16 (short *dest, short v, int n);

/**
 * Fills n tetra-bytes with v, starting at dest, which must be 4-aligned.
 */
void fillMemory32 (int *dest, int v, int n);

#endif
