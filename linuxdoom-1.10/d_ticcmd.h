
// The data sampled per tick (single player)
// and transmitted to other peers (multiplayer).
// Mainly movements/button commands per game tick,
// plus a checksum for internal state consistency.
typedef struct
{
    char	forwardmove;	// *2048 for move
    char	sidemove;	// *2048 for move
    short	angleturn;	// <<16 for angle delta
    short	consistancy;	// checks for net game
    byte	chaatchar; // renamed to prevent false positives in text searches
    byte	buttons;
} ticcmd_t;
