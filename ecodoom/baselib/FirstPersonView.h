/*
 *  FirstPersonView.h
 *  baselib
 *
 *  Created by Martin on 8/29/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Rendering state. This structure is used so we don't have to
 * pass around a lot of temporary values.
 *
 * Screen start/end X: screen x coordinates of the start and end
 * vertices of the current segment.
 *
 * Floor/ceiling start/end Y: screen y coordinates of the floor
 * and ceiling at the start and end vertices.
 *
 * Upper/lower window border start/end Y: screen y coordinates of
 * the upper and lower window border at the start and end vertices.
 * Only applies to two-sided segments. The front/back sector
 * floor/ceiling width should be used to determine conditions where
 * the upper (lower) window border is higher (lower) than the
 * ceiling (floor), causing the upper (lower) "window wall" to
 * be hidden -- the integer fields listed here are prone to
 * rounding errors.
 */
struct RenderState {

	/**
	 * Whether the current segment is two-sided.
	 */
	int twoSidedFlag;
	
	/**
	 * Screen x coordinate of the segment start.
	 */
	int screenStartX;
	
	/**
	 * Screen y coordinate of the segment end.
	 */
	int screenEndX;

	/**
	 * Screen y coordinate of the floor height at the segment start.
	 */
	int floorStartY;
	
	/**
	 * Screen y coordinate of the floor height at the segment end.
	 */
	int floorEndY;
	
	/**
	 * Screen y coordinate of the ceiling height at the segment start.
	 */
	int ceilingStartY;
	
	/**
	 * Screen y coordinate of the ceiling height at the segment end.
	 */
	int ceilingEndY;
	
	/**
	 * Screen y coordinate of the lower window border at the segment start.
	 */
	int lowerWindowBorderStartY;

	/**
	 * Screen y coordinate of the lower window border at the segment end.
	 */
	int lowerWindowBorderEndY;
	
	/**
	 * Screen y coordinate of the upper window border at the segment start.
	 */
	int upperWindowBorderStartY;
	
	/**
	 * Screen y coordinate of the upper window border at the segment end.
	 */
	int upperWindowBorderEndY;
	
	/**
	 * TODO:
	 */
	int r, g, b;
	int color;

	/**
	 * The length of the current segment in world coordinates. The actual meaning
	 * of this field for the rasterizer is the width of the visible texture area
	 * in texture coordinate space.
	 */
	FixedPointNumber segmentLength;
	
	/**
	 * The distance of the segment start along the view axis
	 */
	FixedPointNumber viewStartX;

	/**
	 * The distance of the segment end along the view axis
	 */
	FixedPointNumber viewEndX;
	
	/**
	 * The height of the front sector ceiling in view space.
	 */
	FixedPointNumber viewFrontSectorCeilingHeight;

	/**
	 * The height of the front sector floor in view space.
	 */
	FixedPointNumber viewFrontSectorFloorHeight;
	
	/**
	 * The height of the back sector ceiling in view space.
	 */
	FixedPointNumber viewBackSectorCeilingHeight;
	
	/**
	 * The height of the back sector floor in view space.
	 */
	FixedPointNumber viewBackSectorFloorHeight;
	
	/**
	 * The texture panning in x direction of the textures of the current segment.
	 */
	FixedPointNumber horizontalTexturePanning;
	
	/**
	 * The texture panning in y direction of the upper texture of the current segment.
	 * This is the position in the texture that is associated with the top of the visible texture area on screen,
	 * before clipping against screen borders.
	 */
	FixedPointNumber upperTexturePanning;
	
	/**
	 * The texture panning in y direction of the middle texture of the current segment.
	 * This is the position in the texture that is associated with the top of the visible texture area on screen,
	 * before clipping against screen borders.
	 */
	FixedPointNumber middleTexturePanning;

	/**
	 * The texture panning in y direction of the lower texture of the current segment.
	 * This is the position in the texture that is associated with the top of the visible texture area on screen,
	 * before clipping against screen borders.
	 */
	FixedPointNumber lowerTexturePanning;
	
	/**
	 * The middle texture for the current segment.
	 */
	struct TextureDescriptor *middleTexture;
	
	/**
	 * The upper texture for the current segment.
	 */
	struct TextureDescriptor *upperTexture;
	
	/**
	 * The lower texture for the current segment.
	 */
	struct TextureDescriptor *lowerTexture;
	
	/**
	 * The flat index of the front sector's ceiling.
	 */
	int frontSectorCeilingFlatIndex;
	
	/**
	 * The flat index of the front sector's floor.
	 */
	int frontSectorFloorFlatIndex;
	
	/**
	 * The view-independent segment lighting parameter.
	 */
	int worldSegmentLight;

	/**
	 * The view-independent sector lighting parameter.
	 */
	int worldSectorLight;
	
};

/**
 * The global RenderState instance.
 */
extern struct RenderState renderState;

/**
 * Draws the player's first person view.
 */
void drawFirstPersonView();

/**
 * This function is called by the rasterizer just before a fragment is drawn. It allows
 * to lazy-initialize information that applies unchanged to all fragments of a segment,
 * but needs to be computed only if at least one fragment is actually visible.
 *
 * Furthermore, this function is also a hook for testing the rasterizer, since it simplifies
 * to call the rasterizer from other contexts than the first-person view.
 */
void onBeforeDrawFragment();
