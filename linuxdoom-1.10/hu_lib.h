
#define HU_MAXLINES		4
#define HU_MAXLINELENGTH	80

//
// Typedefs of widgets
//

// Text Line widget
//  (parent of Scrolling Text and Input Text widgets)
typedef struct
{
    // left-justified position of scrolling text window
    int		x;
    int		y;
    
    patch_t**	f;			// font
    int		sc;			// start character
    char	l[HU_MAXLINELENGTH+1];	// line of text
    int		len;		      	// current line length

} hu_textline_t;



// Scrolling Text window widget
//  (child of Text Line widget)
typedef struct
{
    hu_textline_t	l[HU_MAXLINES];	// text lines to draw
    int			h;		// height in lines
    int			cl;		// current line number

    // pointer to boolean stating whether to update window
    boolean*		on;

} hu_stext_t;



//
// Widget creation, access, and update routines
//

// initializes heads-up widget library
void HUlib_init(void);

//
// textline code
//

// clear a line of text
void	HUlib_clearTextLine(hu_textline_t *t);

void	HUlib_initTextLine(hu_textline_t *t, int x, int y, patch_t **f, int sc);

// returns success
boolean HUlib_addCharToTextLine(hu_textline_t *t, char ch);

// returns success
boolean HUlib_delCharFromTextLine(hu_textline_t *t);

// draws tline
void	HUlib_drawTextLine(hu_textline_t *l, boolean drawcursor);



//
// Scrolling Text window widget routines
//

// ?
void
HUlib_initSText
( hu_stext_t*	s,
  int		x,
  int		y,
  int		h,
  patch_t**	font,
  int		startchar,
  boolean*	on );

// add a new line
void HUlib_addLineToSText(hu_stext_t* s);  

// ?
void
HUlib_addMessageToSText
( hu_stext_t*	s,
  char*		prefix,
  char*		msg );

// draws stext
void HUlib_drawSText(hu_stext_t* s);
