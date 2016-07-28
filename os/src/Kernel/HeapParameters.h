
#ifndef __KERNEL_HEAPPARAMETERS_H__
#define __KERNEL_HEAPPARAMETERS_H__

/**
 * This module controls the dynamically managed memory range used by the
 * kernel. This memory range does not contain the kernel code, data,
 * and BSS. In this sense, it is the "heap" used by the kernel,
 * alllocated on a page basis though.
 */


/**
 * This function may be used before initializeHeapParameters() is called
 * to configure the heap size. It must not be called once the heap is
 * initialized. The size must be a multiple of 4k (the page size).
 */
void configureHeapParameters(unsigned int size);

/**
 * This function must be called before the heap parameter functions are used.
 */
void initializeHeapParameters(void);

/**
 * This function cleans up al runtime data of the heap parameters system.
 */
void finalizeHeapParameters(void);

/**
 * Returns the start address of the heap range to use by the kernel.
 * This address is always aligned to a 4k-page boundary.
 */
unsigned int getHeapStartAddress(void);

/**
 * Returns the number of bytes the kernel can use, beginning at the address
 * returned by getHeapStartAddress(). The return value is always a
 * multiple of 4k (the page size) and does not "wrap around" the 32-bit
 * address space.
 */
unsigned int getHeapSize(void);

#endif
