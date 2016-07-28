// Emacs style mode select   -*- C++ -*- 
//-----------------------------------------------------------------------------
//
// $Id:$
//
// Copyright (C) 1993-1996 by id Software, Inc.
//
// This source is available for distribution and/or modification
// only under the terms of the DOOM Source Code License as
// published by id Software. All rights reserved.
//
// The source is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// FITNESS FOR A PARTICULAR PURPOSE. See the DOOM Source Code License
// for more details.
//
// $Log:$
//
// DESCRIPTION:
//	DOOM main program (D_DoomMain) and game loop (D_DoomLoop),
//	plus functions to determine game mode (shareware, registered),
//	parse command line parameters, configure game parameters (turbo),
//	and call the startup functions.
//
//-----------------------------------------------------------------------------


static const char rcsid[] = "$Id: d_main.c,v 1.8 1997/02/03 22:45:09 b1 Exp $";

#define	BGCOLOR		7
#define	FGCOLOR		8


#ifdef NORMALUNIX
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#endif


#include "doomdef.h"
#include "doomstat.h"

#include "dstrings.h"
#include "sounds.h"


#include "z_zone.h"
#include "w_wad.h"
#include "s_sound.h"
#include "v_video.h"

#include "f_finale.h"

#include "m_argv.h"
#include "m_misc.h"
#include "m_menu.h"

#include "i_system.h"
#include "i_sound.h"
#include "i_video.h"

#include "g_game.h"

#include "hu_stuff.h"
#include "wi_stuff.h"
#include "st_stuff.h"
#include "am_map.h"

#include "p_setup.h"
#include "r_local.h"


#include "d_main.h"

//
// D-DoomLoop()
// Not a globally visible function,
//  just included for source reference,
//  called by D_DoomMain, never exits.
// Manages timing and IO,
//  calls all ?_Responder, ?_Ticker, and ?_Drawer,
//  calls I_GetTime
//
void D_DoomLoop (void);


char*		wadfiles[MAXWADFILES];


boolean		singletics = false; // debug flag to cancel adaptiveness



//extern int soundVolume;
//extern  int	sfxVolume;
//extern  int	musicVolume;

extern  boolean	inhelpscreens;

skill_t		startskill;
int		startmap;
boolean		autostart;

boolean		advancedemo;




char		wadfile[1024];		// primary wad file
char		mapdir[1024];           // directory of development maps

// detected configuration file name
char		basedefault[1024];      // default file


void D_ProcessEvents (void);
void G_BuildTiccmd (ticcmd_t* cmd);
void D_DoAdvanceDemo (void);


//
// EVENT HANDLING
//
// Events are asynchronous inputs generally generated by the game user.
// Events can be discarded if no responder claims them
//
event_t         events[MAXEVENTS];
int             eventhead;
int 		eventtail;


//
// D_PostEvent
// Called by the I/O functions when input is detected
//
void D_PostEvent (event_t* ev)
{
    events[eventhead] = *ev;
    eventhead = (++eventhead)&(MAXEVENTS-1);
}


//
// D_ProcessEvents
// Send all the events of the given timestamp down the responder chain
//
void D_ProcessEvents (void)
{
    event_t*	ev;
	
    for ( ; eventtail != eventhead ; eventtail = (++eventtail)&(MAXEVENTS-1) )
    {
	ev = &events[eventtail];
	if (M_Responder (ev))
	    continue;               // menu ate the event
	G_Responder (ev);
    }
}




//
// D_Display
//  draw current display, possibly wiping it from the previous
//

extern  int             showMessages;

void D_Display (void)
{
    static  boolean		viewactivestate = false;
    static  boolean		menuactivestate = false;
    static  boolean		inhelpscreensstate = false;
    static  boolean		fullscreen = false;
    static  gamestate_t		oldgamestate = -1;
    int				nowtime;
    int				tics;
    int				y;
    boolean			done;
    boolean			redrawsbar;

    redrawsbar = false;
    
    // do buffered drawing
    switch (gamestate)
    {
      case GS_LEVEL:
	if (!gametic)
	    break;
	if (automapactive)
	    AM_Drawer ();
	if (viewheight != 200 && fullscreen)
	    redrawsbar = true;
	if (inhelpscreensstate && !inhelpscreens)
	    redrawsbar = true;              // just put away the help screen
	ST_Drawer (viewheight == 200, redrawsbar );
	fullscreen = viewheight == 200;
	break;

      case GS_INTERMISSION:
	WI_Drawer ();
	break;

      case GS_FINALE:
	F_Drawer ();
	break;

      case GS_DEMOSCREEN:
	D_PageDrawer ();
	break;
    }
    
    // draw the view directly
    if (gamestate == GS_LEVEL && !automapactive && gametic)
	R_RenderPlayerView (&players[0]);

    if (gamestate == GS_LEVEL && gametic)
	HU_Drawer ();
    
    // clean up border stuff
    if (gamestate != oldgamestate && gamestate != GS_LEVEL)
	I_SetPalette (W_CacheLumpName ("PLAYPAL",PU_CACHE));

    // see if the border needs to be initially drawn
    if (gamestate == GS_LEVEL && oldgamestate != GS_LEVEL)
    {
	viewactivestate = false;        // view was not active
    }

    menuactivestate = menuactive;
    viewactivestate = viewactive;
    inhelpscreensstate = inhelpscreens;
    oldgamestate = gamestate;
    
    // draw pause pic
    if (paused)
    {
	    y = 4;
		V_DrawPatchDirect((scaledviewwidth-68)/2, y, 0, W_CacheLumpName ("M_PAUSE", PU_CACHE));
    }


    // menus go directly to the screen
    M_Drawer ();          // menu is drawn even on top of everything


    // page flip or blit buffer
	I_FinishUpdate ();     
}



//
//  D_DoomLoop
//

void D_DoomLoop (void)
{
    I_InitGraphics ();

    while (1)
    {
	    /** run M_Responder or G_Responder on each event **/
	    D_ProcessEvents ();
		    
	    /**
	     * Create a game tic command from the key states, demo recording, or whatever.
	     * I guess that this affects only the in-game state, not e.g. the menu.
	     */
	    G_BuildTiccmd (&currentTicCommand);
		    
	    /** TODO: not looked at this closely **/
	    if (advancedemo)
			D_DoAdvanceDemo ();
		
		/** advance menu (animation only) **/
	    M_Ticker ();
		    
	    /** advance game logic **/
	    G_Ticker ();
		    
	    /** count tics **/
	    gametic++;

		/** ??? **/		
		S_UpdateSounds (players[0].mo);// move positional sounds

		/** ??? **/		
		// Update display, next frame, with current state.
		D_Display ();

#ifndef SNDSERV
		/** ??? **/		
		// Sound mixing for the buffer is snychronous.
		I_UpdateSound();
#endif	
		/** ??? **/		
		// Synchronous sound output is explicitly called.
#ifndef SNDINTR
		/** ??? **/		
		// Update sound output.
		I_SubmitSound();
#endif
    }
}



//
//  DEMO LOOP
//
int             demosequence;
int             pagetic;
char                    *pagename;


//
// D_PageTicker
// Handles timing for warped projection
//
void D_PageTicker (void)
{
    if (--pagetic < 0)
	D_AdvanceDemo ();
}



//
// D_PageDrawer
//
void D_PageDrawer (void)
{
    V_DrawPatch (0,0, 0, W_CacheLumpName(pagename, PU_CACHE));
}


//
// D_AdvanceDemo
// Called after each demo or intro demosequence finishes
//
void D_AdvanceDemo (void)
{
    advancedemo = true;
}


//
// This cycles through the demo sequences.
// FIXME - version dependend demo numbers?
//
 void D_DoAdvanceDemo (void)
{
    players[0].playerstate = PST_LIVE;  // not reborn
    advancedemo = false;
    usergame = false;               // no save / end game here
    paused = false;
    gameaction = ga_nothing;

    /**
     * this would be %7 for the registered version
     */
    demosequence = (demosequence+1)%6;
    
    switch (demosequence)
    {
      case 0:
	    pagetic = 170;
	gamestate = GS_DEMOSCREEN;
	pagename = "TITLEPIC";
	  S_StartMusic (mus_intro);
	break;
      case 1:
	G_DeferedPlayDemo ("demo1");
	break;
      case 2:
	pagetic = 200;
	gamestate = GS_DEMOSCREEN;
	pagename = "CREDIT";
	break;
      case 3:
	G_DeferedPlayDemo ("demo2");
	break;
      case 4:
	gamestate = GS_DEMOSCREEN;
	pagetic = 200;
    pagename = "HELP2";
	break;
      case 5:
	G_DeferedPlayDemo ("demo3");
	break;
    }
}



//
// D_StartTitle
//
void D_StartTitle (void)
{
    gameaction = ga_nothing;
    demosequence = -1;
    D_AdvanceDemo ();
}




//
// D_AddFile
//
void D_AddFile (char *file)
{
    int     numwadfiles;
    char    *newfile;
	
    for (numwadfiles = 0 ; wadfiles[numwadfiles] ; numwadfiles++)
	;

    newfile = malloc (strlen(file)+1);
    strcpy (newfile, file);
	
    wadfiles[numwadfiles] = newfile;
}

//
// D_DoomMain
//
void D_DoomMain (void)
{
    int             p;
    char                    file[256];

    setbuf(stdout, NULL);
    printf("Welcome to EcoDoom!");
	D_AddFile(***doom1wad***);

    // get skill / map from parms
    startskill = sk_medium;
    startmap = 1;
    autostart = false;

    // init subsystems
    printf ("V_Init: allocate screens.\n");
    V_Init ();

    printf ("Z_Init: Init zone memory allocation daemon. \n");
    Z_Init ();

    printf ("W_Init: Init WADfiles.\n");
    W_InitMultipleFiles (wadfiles);

    printf ("M_Init: Init miscellaneous info.\n");
    M_Init ();

    printf ("R_Init: Init DOOM refresh daemon - ");
    R_Init ();

    printf ("\nP_Init: Init Playloop state.\n");
    P_Init ();

    printf ("I_Init: Setting up machine state.\n");
    I_Init ();

    printf ("S_Init: Setting up sound.\n");
    S_Init (snd_SfxVolume /* *8 */, snd_MusicVolume /* *8*/ );

    printf ("HU_Init: Setting up heads up display.\n");
    HU_Init ();

    printf ("ST_Init: Init status bar.\n");
    ST_Init ();

    if ( gameaction != ga_loadgame )
    {
	if (autostart)
	    G_InitNew (startskill, startmap);
	else
	    D_StartTitle ();                // start up intro loop

    }

    D_DoomLoop ();  // never returns
}