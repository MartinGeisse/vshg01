
ticcmd_t	emptycmd;
ticcmd_t*	I_BaseTiccmd(void)
{
    return &emptycmd;
}



//
// I_GetTime: Returns the number of ticks (1/35th second) since the first call
//
int  I_GetTime (void) ...



//
// I_Init
//
void I_Init (void)
{
    I_InitSound();
    //  I_InitGraphics();
}

//
// I_Quit
//
void I_Quit (void)
{
    I_ShutdownSound();
    I_ShutdownMusic();
    I_ShutdownGraphics();
    exit(0);
}

void I_WaitVBL(int count)
{
    usleep (count * (1000000/70) );                                
}
