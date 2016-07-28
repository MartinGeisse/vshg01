
#ifndef BASELIB_CPU_H
#define BASELIB_CPU_H

#define PswVectorBit					0x08000000
#define PswUserModeBit					0x04000000
#define PswPreviousUserModeBit			0x02000000
#define PswOldUserModeBit				0x01000000
#define PswInterruptEnableBit			0x00800000
#define PswPreviousInterruptEnableBit	0x00400000
#define PswOldInterruptEnableBit		0x00200000
#define PswPriorityBits					0x001F0000
#define PswInterruptMaskBits			0x0000FFFF

#define PswVectorShift					27
#define PswUserModeShift				26
#define PswPreviousUserModeShift		25
#define PswOldUserModeShift				24
#define PswInterruptEnableShift			23
#define PswPreviousInterruptEnableShift	22
#define PswOldInterruptEnableShift		21
#define PswPriorityShift				16
#define PswInterruptMaskShift			0

void baselib_cpu_setPsw (unsigned int value);
unsigned int baselib_cpu_getPsw (void);
void baselib_cpu_setTlbIndex (unsigned int value);
unsigned int baselib_cpu_getTlbIndex (void);
void baselib_cpu_setTlbEntryHigh (unsigned int value);
unsigned int baselib_cpu_getTlbEntryHigh (void);
void baselib_cpu_setTlbEntryLow (unsigned int value);
unsigned int baselib_cpu_getTlbEntryLow (void);
void baselib_cpu_setTlbBadAddress (unsigned int value);
unsigned int baselib_cpu_getTlbBadAddress (void);
void baselib_cpu_setInterruptReturnAddress (unsigned int value);
unsigned int baselib_cpu_getInterruptReturnAddress (void);

void baselib_cpu_useRomInterruptHandler (void);
void baselib_cpu_useRamInterruptHandler (void);
void baselib_cpu_disableInterrupts (void);
void baselib_cpu_enableInterrupts (void);
int baselib_cpu_interruptsEnabled (void);
void baselib_cpu_disableInterruptChannel (unsigned int channel);
void baselib_cpu_enableInterruptChannel (unsigned int channel);
int baselib_cpu_interruptChannelEnabled (unsigned int channel);
unsigned int baselib_cpu_getInterruptPriority (void);
unsigned int baselib_cpu_getProgramCounter (void);

// returns 1 if the calling function is located in ROM, 0 if in RAM
int baselib_cpu_isRomCode (void);

#endif
