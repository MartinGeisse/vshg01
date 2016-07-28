/*
 *  StaticGameData.h
 *  baselib
 *
 *  Created by Martin on 8/28/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * This structure is used for game data that is not associated with the
 * running game but exists statically.
 */
struct StaticGameData {
	
	/**
	 * The flat index that causes special sky drawing behaviour
	 */
	int skyFlatIndex;
	
	/**
	 * The texture index of the texture that is used for sky drawing.
	 */
	int skyTextureIndex;
	
	/**
	 * TODO: looks like a texture panning offset
	 */
	// int skytexturemid;
	
};

/**
 * The static game data.
 */
extern struct StaticGameData staticGameData;

/**
 * Initializes the static game data.
 */
void initializeStaticGameData();

/**
 * Disposes of the static game data.
 */
void disposeStaticGameData();
