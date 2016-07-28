visplane_t*		floorplane;
visplane_t*		ceilingplane;


seg_t*		curline;
side_t*		sidedef;
line_t*		linedef;
sector_t*	frontsector;
sector_t*	backsector;

drawseg_t	drawsegs[MAXDRAWSEGS];
drawseg_t*	ds_p;

//
// R_ClearDrawSegs
//
void R_ClearDrawSegs (void)
{
    ds_p = drawsegs;
}







// OPTIMIZE: closed two sided lines as single sided

// True if any of the segs textures might be visible.
static boolean		segtextured;	

// False if the back side is the same plane.
// note: could still require splitting for concave?
static boolean		markfloor;	
static boolean		markceiling;

static boolean		maskedtexture;
static int		toptexture;
static int		bottomtexture;
static int		midtexture;


angle_t		rw_normalangle;
// angle to line origin
static int		rw_angle1;	

//
// regular wall
//
static int		rw_x;
static int		rw_stopx;
static angle_t		rw_centerangle;
static fixed_t		rw_offset;
fixed_t		rw_distance;
static fixed_t		rw_scale;
static fixed_t		rw_scalestep;
static fixed_t		rw_midtexturemid;
static fixed_t		rw_toptexturemid;
static fixed_t		rw_bottomtexturemid;

/**
 * Texture y coordinates.
 */
static fixed_t		pixhigh;
static fixed_t		pixlow;
static fixed_t		pixhighstep;
static fixed_t		pixlowstep;

static fixed_t		topfrac;
static fixed_t		topstep;

static fixed_t		bottomfrac;
static fixed_t		bottomstep;


lighttable_t**	walllights;

static short*		maskedtexturecol;



//
// R_RenderMaskedSegRange
//
void
R_RenderMaskedSegRange
( drawseg_t*	ds,
  int		x1,
  int		x2 )
{
    unsigned	index;
    column_t*	col;
    int		lightnum;
    int		texnum;
    
    // Calculate light table.
    // Use different light tables
    //   for horizontal / vertical / diagonal. Diagonal?
    // OPTIMIZE: get rid of LIGHTSEGSHIFT globally
    curline = ds->curline;
    frontsector = curline->frontsector;
    backsector = curline->backsector;
    texnum = texturetranslation[curline->sidedef->midtexture];
	
    lightnum = (frontsector->lightlevel >> LIGHTSEGSHIFT)+extralight;

    if (curline->v1->y == curline->v2->y)
	lightnum--;
    else if (curline->v1->x == curline->v2->x)
	lightnum++;

    if (lightnum < 0)		
	walllights = scalelight[0];
    else if (lightnum >= LIGHTLEVELS)
	walllights = scalelight[LIGHTLEVELS-1];
    else
	walllights = scalelight[lightnum];

    maskedtexturecol = ds->maskedtexturecol;

    rw_scalestep = ds->scalestep;		
    spryscale = ds->scale1 + (x1 - ds->x1)*rw_scalestep;
    mfloorclip = ds->sprbottomclip;
    mceilingclip = ds->sprtopclip;
    
    // find positioning
    if (curline->linedef->flags & ML_DONTPEGBOTTOM)
    {
	dc_texturemid = frontsector->floorheight > backsector->floorheight
	    ? frontsector->floorheight : backsector->floorheight;
	dc_texturemid = dc_texturemid + textureheight[texnum] - viewz;
    }
    else
    {
	dc_texturemid =frontsector->ceilingheight<backsector->ceilingheight
	    ? frontsector->ceilingheight : backsector->ceilingheight;
	dc_texturemid = dc_texturemid - viewz;
    }
    dc_texturemid += curline->sidedef->rowoffset;
			
    if (fixedcolormap)
	dc_colormap = fixedcolormap;
    
    // draw the columns
    for (dc_x = x1 ; dc_x <= x2 ; dc_x++)
    {
	// calculate lighting
	if (maskedtexturecol[dc_x] != MAXSHORT)
	{
	    if (!fixedcolormap)
	    {
		index = spryscale>>LIGHTSCALESHIFT;

		if (index >=  MAXLIGHTSCALE )
		    index = MAXLIGHTSCALE-1;

		dc_colormap = walllights[index];
	    }
			
	    sprtopscreen = centeryfrac - FixedMul(dc_texturemid, spryscale);
	    dc_iscale = 0xffffffffu / (unsigned)spryscale;
	    
	    // draw the texture
	    col = (column_t *)( 
		(byte *)R_GetColumn(texnum,maskedtexturecol[dc_x]) -3);
			
	    R_DrawMaskedColumn (col);
	    maskedtexturecol[dc_x] = MAXSHORT;
	}
	spryscale += rw_scalestep;
    }
	
}




//
// R_RenderSegLoop
// Draws zero, one, or two textures (and possibly a masked
//  texture) for walls.
// Can draw or mark the starting pixel of floor and ceiling
//  textures.
// CALLED: CORE LOOPING ROUTINE.
//

static void R_RenderSegLoop (void)
{
    angle_t		angle;
    unsigned		index;
    int			yl;
    int			yh;
    int			mid;
    fixed_t		texturecolumn;
    int			top;
    int			bottom;

    //texturecolumn = 0;				// shut up compiler warning
	
    for ( ; rw_x < rw_stopx ; rw_x++) {

		if (markceiling) {/** fill ceiling visplane **/}
		if (markfloor) {/** fill floor visplane **/}
	
		// draw the wall tiers
		if (midtexture) {
			...
		} else {
			...
			
		    if (maskedtexture) {
				// save texturecol
				//  for backdrawing of masked mid texture
				maskedtexturecol[rw_x] = texturecolumn;
		    }
		}
    }
}




//
// R_StoreWallRange
// A wall segment will be drawn
//  between start and stop pixels (inclusive).
//
// ecodoom: SOCM fragment handler
//
void
R_StoreWallRange
( int	start,
  int	stop )
{
    fixed_t		hyp;
    fixed_t		sineval;
    angle_t		distangle, offsetangle;
    int			lightnum;

    // don't overflow and crash
    if (ds_p == &drawsegs[MAXDRAWSEGS])
		return;		
		
    // rw_normalangle = curline->angle + ANG90;
    // offsetangle = abs(rw_normalangle-rw_angle1);
    
    // if (offsetangle > ANG90)
	// 	offsetangle = ANG90;

//     distangle = ANG90 - offsetangle;
//     hyp = vectorNorm (curline->v1->x, curline->v1->y);
//     sineval = finesine[distangle>>ANGLETOFINESHIFT];
//     rw_distance = FixedMul (hyp, sineval);
		
	/**
	 * ECODOOM: Store the fragment for later use -- for what exactly?
	 */
    ds_p->x1 = rw_x = start;
    ds_p->x2 = stop;
    ds_p->curline = curline;
    rw_stopx = stop+1;
    
    /**
     * Compute the "scale" at both ends of the fragment -- probably related to
     * perspective texture correction.
     */
    ds_p->scale1 = rw_scale =  R_ScaleFromGlobalAngle (viewangle + xtoviewangle[start]);
    if (stop > start) {
		ds_p->scale2 = R_ScaleFromGlobalAngle (viewangle + xtoviewangle[stop]);
		ds_p->scalestep = rw_scalestep = (ds_p->scale2 - rw_scale) / (stop-start);
    } else {
		ds_p->scale2 = ds_p->scale1;
    }

	/**
	 * Compute texture coordinates in view space, i.e. before projection and
	 * screen mapping but already shifted by the player's height and with larger
	 * values going downwards.
	 *
	 * ECODOOM: Only compute texture anchor here; compute (position := anchor - viewz) later.
	 * Can still be pre-cached for the whole segment (or even line).
	 *
	 * Also decide if floor / ceiling marks are needed.
	 */    
    int worldtop = frontsector->ceilingheight - viewz;
    int worldbottom = frontsector->floorheight - viewz;
	
    midtexture = toptexture = bottomtexture = maskedtexture = 0;
    ds_p->maskedtexturecol = NULL;
	
    if (!backsector)
    {
		// single sided line
		midtexture = texturetranslation[sidedef->midtexture];
		// a single sided line is terminal, so it must mark ends
		markfloor = markceiling = true;
		
		...

		/**
		 * Probably related to sprite clipping
		 */
		ds_p->silhouette = SIL_BOTH;
		ds_p->sprtopclip = screenheightarray;
		ds_p->sprbottomclip = negonearray;
		ds_p->bsilheight = MAXINT;
		ds_p->tsilheight = MININT;
    }
    else
    {
		// two sided line
		
		/**
		 * Probably related to sprite clipping
		 */
		ds_p->sprtopclip = ds_p->sprbottomclip = NULL;
		ds_p->silhouette = 0;
		if (frontsector->floorheight > backsector->floorheight)
		{
		    ds_p->silhouette = SIL_BOTTOM;
		    ds_p->bsilheight = frontsector->floorheight;
		}
		else if (backsector->floorheight > viewz)
		{
		    ds_p->silhouette = SIL_BOTTOM;
		    ds_p->bsilheight = MAXINT;
		    // ds_p->sprbottomclip = negonearray;
		}
	
		/**
		 * Probably related to sprite clipping
		 */
		if (frontsector->ceilingheight < backsector->ceilingheight)
		{
		    ds_p->silhouette |= SIL_TOP;
		    ds_p->tsilheight = frontsector->ceilingheight;
		}
		else if (backsector->ceilingheight < viewz)
		{
		    ds_p->silhouette |= SIL_TOP;
		    ds_p->tsilheight = MININT;
		    // ds_p->sprtopclip = screenheightarray;
		}
		
		/**
		 * Probably related to sprite clipping
		 */
		if (backsector->ceilingheight <= frontsector->floorheight)
		{
		    ds_p->sprbottomclip = negonearray;
		    ds_p->bsilheight = MAXINT;
		    ds_p->silhouette |= SIL_BOTTOM;
		}
		
		if (backsector->floorheight >= frontsector->ceilingheight)
		{
		    ds_p->sprtopclip = screenheightarray;
		    ds_p->tsilheight = MININT;
		    ds_p->silhouette |= SIL_TOP;
		}
	
		int worldhigh = backsector->ceilingheight - viewz;
		int worldlow = backsector->floorheight - viewz;
		
		/**
		 * If the ceiling flat index indicates an outdoor area, we adjust the
		 * back sector's ceiling height to the front sector's ceiling height
		 * FOR THIS SEGMENT, effectively hiding the "ceiling wall".
		 *
		 * We can't just skip the segment: The ceiling height of the back sector might be below
		 * the player, and then that ceiling (and thus the whole sky) isn't drawn.
		 *
		 * TODO: How does this affect ceiling marking?
		 */
		if (frontsector->ceilingpic == skyflatnum  && backsector->ceilingpic == skyflatnum)
		{
		    worldtop = worldhigh;
		}
	
		/**
		 * Mark the floor/ceiling iff there is a visible change in floor/ceiling properties (height, flat, lighting).
		 */
		markfloor = (worldlow != worldbottom  || backsector->floorpic != frontsector->floorpic || backsector->lightlevel != frontsector->lightlevel);
		markceiling = (worldhigh != worldtop || backsector->ceilingpic != frontsector->ceilingpic || backsector->lightlevel != frontsector->lightlevel);
	
		/**
		 * Enforce marking if there is no visible gap -- this means that even if there is no visible change in floor/ceiling
		 * properties, the backside floor/ceiling is invisible, thus drawing must stop at the segment.
		 */
		if (backsector->ceilingheight <= frontsector->floorheight || backsector->floorheight >= frontsector->ceilingheight) {
		    markceiling = markfloor = true;
		}
	
		/**
		 * Check if there is a visible "ceiling wall" from this side of the line.
		 */
		if (worldhigh < worldtop) {
		    // top texture
		    toptexture = texturetranslation[sidedef->toptexture];
		    if (linedef->flags & ML_DONTPEGTOP)
		    {
			// top of texture at top
			rw_toptexturemid = worldtop;
		    }
		    else
		    {
				// bottom of texture
				rw_toptexturemid = backsector->ceilingheight + textureheight[sidedef->toptexture] - viewz;	
		    }
		}
		
		/**
		 * Check if there is a visible "floor wall" from this side of the line.
		 */
		if (worldlow > worldbottom) {
		    // bottom texture
		    bottomtexture = texturetranslation[sidedef->bottomtexture];
	
		    if (linedef->flags & ML_DONTPEGBOTTOM )
		    {
			// bottom of texture at bottom
			// top of texture at top
			rw_bottomtexturemid = worldtop;
		    }
		    else	// top of texture at top
			rw_bottomtexturemid = worldlow;
		}
		
		/**
		 * Apply y texture panning from the sidedef.
		 */
		rw_toptexturemid += sidedef->rowoffset;
		rw_bottomtexturemid += sidedef->rowoffset;
	
		// TODO: ??? allocate space for masked texture tables
		if (sidedef->midtexture)
		{
		    // masked midtexture
		    maskedtexture = true;
		    ds_p->maskedtexturecol = maskedtexturecol = lastopening - rw_x;
		    lastopening += rw_stopx - rw_x;
		}
    }
    
    /**
     * non-textured: e.g. no changes in ceiling and floor height, no mid texture
     * --> no texture computations necessary
     */
    segtextured = midtexture | toptexture | bottomtexture | maskedtexture;

    if (segtextured)
    {
    	/**
    	 * ECODOOM: texturing stuff. Apply x texture panning from both the line
    	 * (user-visible texture panning) and the segment (offset of the BSP split
    	 * point). Later also add the offset of the fragment within the segment,
    	 * and apply perspective correction.
    	 */
//		offsetangle = rw_normalangle-rw_angle1;
	
//		if (offsetangle > ANG180)
//		    offsetangle = -offsetangle;

//		if (offsetangle > ANG90)
//		    offsetangle = ANG90;

//		sineval = finesine[offsetangle >>ANGLETOFINESHIFT];
//		rw_offset = FixedMul (hyp, sineval);

//		if (rw_normalangle-rw_angle1 < ANG180)
//		    rw_offset = -rw_offset;

//		rw_offset += sidedef->textureoffset + curline->offset;
//		rw_centerangle = ANG90 + viewangle - rw_normalangle;
	
		// calculate light table
		//  use different light tables
		//  for horizontal / vertical / diagonal
		// OPTIMIZE: get rid of LIGHTSEGSHIFT globally
		// --> ???
		if (!fixedcolormap)
		{
		    lightnum = (frontsector->lightlevel >> LIGHTSEGSHIFT)+extralight;

		    if (curline->v1->y == curline->v2->y)
				lightnum--;
		    else if (curline->v1->x == curline->v2->x)
				lightnum++;

		    if (lightnum < 0)		
				walllights = scalelight[0];
		    else if (lightnum >= LIGHTLEVELS)
				walllights = scalelight[LIGHTLEVELS-1];
		    else
				walllights = scalelight[lightnum];
		}
    }
    
    // if a floor / ceiling plane is on the wrong side
    //  of the view plane, it is definitely invisible
    //  and doesn't need to be marked.
    
  	/**
  	 * Note: Front sector is usually not the one the player is in,
  	 * just the "front" sector of the current segment. Due to backface
  	 * culling, this is still the sector facing the player (going from the
  	 * segment), but not necessarily the one the player is in.
  	 *
  	 * Marking could mean to mark where drawing the floor/ceiling of that
  	 * sector should stop, which is, then, the current segment. So this
  	 * makes sence.
  	 */
    if (frontsector->floorheight >= viewz)
    {
		// above view plane
		markfloor = false;
    }
    if (frontsector->ceilingheight <= viewz && frontsector->ceilingpic != skyflatnum)
    {
		// below view plane
		markceiling = false;
    }

    
    // calculate incremental stepping values for texture edges
    worldtop >>= 4;
    worldbottom >>= 4;
	
	/**
	 * NOTE: centeryfrac is the y center of the 3d view area ((200 - 32) / 2),
	 * expressed as a fixed-point number.
	 */
    topstep = -FixedMul (rw_scalestep, worldtop);
    topfrac = (centeryfrac>>4) - FixedMul (worldtop, rw_scale);

    bottomstep = -FixedMul (rw_scalestep,worldbottom);
    bottomfrac = (centeryfrac>>4) - FixedMul (worldbottom, rw_scale);
	
    if (backsector)
    {	
		worldhigh >>= 4;
		worldlow >>= 4;

		if (worldhigh < worldtop)
		{
		    pixhigh = (centeryfrac>>4) - FixedMul (worldhigh, rw_scale);
		    pixhighstep = -FixedMul (rw_scalestep,worldhigh);
		}
	
		if (worldlow > worldbottom)
		{
		    pixlow = (centeryfrac>>4) - FixedMul (worldlow, rw_scale);
		    pixlowstep = -FixedMul (rw_scalestep,worldlow);
		}
    }
    
    // render it
    if (markceiling)
		ceilingplane = R_CheckPlane (ceilingplane, rw_x, rw_stopx-1);
    
    if (markfloor)
		floorplane = R_CheckPlane (floorplane, rw_x, rw_stopx-1);

    R_RenderSegLoop ();

	/*********************************************************************************/
	/* sprite stuff */
	/*********************************************************************************/
    
    // save sprite clipping info
    if ( ((ds_p->silhouette & SIL_TOP) || maskedtexture) && !ds_p->sprtopclip)
    {
		memcpy (lastopening, ceilingclip+start, 2*(rw_stopx-start));
		ds_p->sprtopclip = lastopening - start;
		lastopening += rw_stopx - start;
    }
    
    if ( ((ds_p->silhouette & SIL_BOTTOM) || maskedtexture) && !ds_p->sprbottomclip)
    {
		memcpy (lastopening, floorclip+start, 2*(rw_stopx-start));
		ds_p->sprbottomclip = lastopening - start;
		lastopening += rw_stopx - start;	
    }

    if (maskedtexture && !(ds_p->silhouette&SIL_TOP))
    {
		ds_p->silhouette |= SIL_TOP;
		ds_p->tsilheight = MININT;
    }
    if (maskedtexture && !(ds_p->silhouette&SIL_BOTTOM))
    {
		ds_p->silhouette |= SIL_BOTTOM;
		ds_p->bsilheight = MAXINT;
    }
    
	/*********************************************************************************/
	/* Finish saving this segment */
	/*********************************************************************************/

    ds_p++;
}

