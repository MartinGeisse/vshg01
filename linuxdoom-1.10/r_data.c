
int		firstspritelump;
int		lastspritelump;
int		numspritelumps;

// needed for texture pegging
fixed_t*		textureheight;		
int*			texturecompositesize;
short**			texturecolumnlump;
unsigned short**	texturecolumnofs;
byte**			texturecomposite;

// for global animation
int*		flattranslation;
int*		texturetranslation;

// needed for pre rendering
fixed_t*	spritewidth;	
fixed_t*	spriteoffset;
fixed_t*	spritetopoffset;

int R_FlatNumForName (char* name) --> int getFlatIndexForName(const char *name)
int	R_CheckTextureNumForName (char *name) --> getTextureIndexForNameSafe(const char *name)
int	R_TextureNumForName (char* name) --> getTextureIndexForName(const char *name)
