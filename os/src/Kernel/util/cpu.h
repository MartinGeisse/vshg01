
#ifndef CPU_H
#define CPU_H

#define PSWVectorBit					0x08000000
#define PSWUserModeBit					0x04000000
#define PSWPreviousUserModeBit			0x02000000
#define PSWOldUserModeBit				0x01000000
#define PSWInterruptEnableBit			0x00800000
#define PSWPreviousInterruptEnableBit	0x00400000
#define PSWOldInterruptEnableBit		0x00200000
#define PSWPriorityBits					0x001F0000
#define PSWInterruptMaskBits			0x0000FFFF

#define PSWVectorShift					27
#define PSWUserModeShift				26
#define PSWPreviousUserModeShift		25
#define PSWOldUserModeShift				24
#define PSWInterruptEnableShift			23
#define PSWPreviousInterruptEnableShift	22
#define PSWOldInterruptEnableShift		21
#define PSWPriorityShift				16
#define PSWInterruptMaskShift			0

void setPSW (unsigned int value);
unsigned int getPSW (void);
void setTLBIndex (unsigned int value);
unsigned int getTLBIndex (void);
void setTLBEntryHigh (unsigned int value);
unsigned int getTLBEntryHigh (void);
void setTLBEntryLow (unsigned int value);
unsigned int getTLBEntryLow (void);
void setTLBBadAddress (unsigned int value);
unsigned int getTLBBadAddress (void);
void setInterruptReturnAddress (unsigned int value);
unsigned int getInterruptReturnAddress (void);

void useROMInterruptHandler (void);
void useRAMInterruptHandler (void);
void disableInterrupts (void);
void enableInterrupts (void);
int interruptsEnabled (void);
void disableInterruptChannel (unsigned int channel);
void enableInterruptChannel (unsigned int channel);
int interruptChannelEnabled (unsigned int channel);
unsigned int getInterruptPriority (void);
unsigned int getProgramCounter (void);

// returns 1 if the calling function is located in ROM, 0 if in RAM
int isROMCode (void);

#endif
