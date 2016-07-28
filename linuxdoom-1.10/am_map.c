

boolean    	automapactive = false;

extern boolean viewactive;

//
//
//
void AM_Stop (void)
{
    automapactive = false;
}

//
//
//
void AM_Start (void)
{
    automapactive = true;
}


//
// Updates on Game Tick
//
void AM_Ticker (void)
{

	/** automap an (edge-triggered) **/
//	if (ev->type == ev_keydown && ev->data1 == AM_STARTKEY)
//	{
//	    AM_Start ();
//	    viewactive = false;
//	    rc = true;
//	}

	/** automap aus (edge-triggered) **/
//	    bigstate = 0;
//	    viewactive = true;
//	    AM_Stop ();
	
    if (!automapactive)
	return;

}

void AM_Drawer (void)
{
    if (!automapactive) return;

    AM_clearFB(BACKGROUND) --> umgesetzt
    AM_drawWalls() --> umgesetzt
    AM_drawPlayers() --> umgesetzt
    AM_drawCrosshair() --> umgesetzt

}
