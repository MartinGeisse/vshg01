/*
 *  MapData.h
 *  baselib
 *
 *  Created by Martin on 8/17/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Forward definition of all structures, so we can define pointers to them.
 */
struct Thing;
struct Vertex;
struct Linedef;
struct Sidedef;
struct Sector;
struct CurrentMapData;
struct MapObject;

/**
 * This enumeration defines the various data lumps that belong to a single
 * map. The value of each enumeration constant is the offset (in number of
 * lumps) from the map marker lump.
 */
enum MapDataLump {
	
	/**
	 * The marker lump itself
	 */
	MAP_DATA_LUMP_MARKER = 0,
	
	/**
	 * Thing definitions
	 */
	MAP_DATA_LUMP_THINGS = 1,
	
	/**
	 * Line definitions
	 */
	MAP_DATA_LUMP_LINEDEFS = 2,
	
	/**
	 * Line side definitions
	 */
	MAP_DATA_LUMP_SIDEDEFS = 3,
	
	/**
	 * Vertex definitions
	 */
	MAP_DATA_LUMP_VERTEXES = 4,
	
	/**
	 * Segment definitions
	 */
	MAP_DATA_LUMP_SEGS = 5,
	
	/**
	 * Subsector definitions
	 */
	MAP_DATA_LUMP_SSECTORS = 6,
	
	/**
	 * Node definitions
	 */
	MAP_DATA_LUMP_NODES = 7,
	
	/**
	 * Sector definitions
	 */
	MAP_DATA_LUMP_SECTORS = 8,
	
	/**
	 * Reject map (potentially visible set)
	 */
	MAP_DATA_LUMP_REJECT = 9,
	
	/**
	 * Collision detection block map
	 */
	MAP_DATA_LUMP_BLOCKMAP = 10,
	
};

/**
 * This thing appears in the easy skill levels "too young to die" and "not too rough".
 */
#define THING_FLAG_SKILL_EASY 0x0001

/**
 * This thing appears in the medium skill level "hurt me plenty".
 */
#define THING_FLAG_SKILL_MEDIUM 0x0002

/**
 * This thing appears in the hard skill levels "ultra-violence" and "nightmare".
 */
#define THING_FLAG_SKILL_HARD 0x0004

/**
 * Flag for a deaf thing.
 */
#define THING_FLAG_DEAF 0x0008

/**
 * Flag for a multiplayer-only thing.
 */
#define THING_FLAG_MULTIPLAYER_ONLY 0x0010

/**
 * This structure defines a "thing" (player spawn point, monster, item, etc.)
 */
struct Thing {
	
	/**
	 * The X coordinate of the thing's position.
	 */
	FixedPointNumber x;
	
	/**
	 * The Y coordinate of the thing's position.
	 */
	FixedPointNumber y;
	
	/**
	 * The angle where the thing is "looking"
	 */
	Angle angle;
	
	/**
	 * The thing type. Describes what kind of thing this is. This number does not directly
	 * refer to a map object type, but instead refers to the index used in DOOMED. The
	 * map object type descritor table must be searched for this index to find the
	 * matching map object type.
	 */
	unsigned short doomedTypeIndex;
	
	/**
	 * Flags that further specify the thing's behavior. Note that all flags except
	 * THING_FLAG_DEAF are honored at map loading time and ignored afterwards.
	 */
	unsigned short flags;
	
};

/**
 * This structure defines a vertex used by line and segments.
 */
struct Vertex {
	
	/**
	 * The X coordinate of the vertex.
	 */
	FixedPointNumber x;
	
	/**
	 * The Y coordinate of the vertex.
	 */
	FixedPointNumber y;
	
};

/**
 * TODO: description
 */
#define LINE_FLAG_BLOCK_PLAYER_AND_MONSTERS		0x0001

/**
 * TODO: description
 */
#define LINE_FLAG_BLOCK_MONSTERS				0x0002

/**
 * TODO: description
 */
#define LINE_FLAG_TWO_SIDED						0x0004

/**
 * TODO: description
 */
#define LINE_FLAG_UPPER_TEXTURE_UNPEGGED		0x0008

/**
 * TODO: description
 */
#define LINE_FLAG_LOWER_TEXTURE_UNPEGGED		0x0010

/**
 * TODO: description
 */
#define LINE_FLAG_SECRET						0x0020

/**
 * TODO: description
 */
#define LINE_FLAG_BLOCK_SOUND					0x0040

/**
 * TODO: description
 */
#define LINE_FLAG_NEVER_ON_AUTOMAP				0x0080

/**
 * This flag controls whether a line is visible on the automap. It is typically set to 0
 * for all walls in a map file. It is explicitly set to 1 for each wall that is drawn
 * in the 3d view.
 */
#define LINE_FLAG_MAPPED						0x0100

/**
 * This structure defines a line in the map.
 */
struct Linedef {
	
	/**
	 * The start vertex
	 */
	struct Vertex *startVertex;

	/**
	 * The end vertex
	 */
	struct Vertex *endVertex;
	
	/**
	 * Modifier flags
	 */
	unsigned short flags;
	
	/**
	 * Line type (special effects)
	 */
	unsigned short type;

	/**
	 * Sector tag, used to locate sectors affected by special effects
	 */
	unsigned short sectorTag;
	
	/**
	 * Right side of this line
	 */
	struct Sidedef *rightSide;

	/**
	 * Left side of this line
	 */
	struct Sidedef *leftSide;
	
};

/**
 * This structure defines a side of a line.
 */
struct Sidedef {

	/**
	 * texture offset along the x axis
	 */
	FixedPointNumber textureOffsetX;

	/**
	 * texture offset along the y axis
	 */
	FixedPointNumber textureOffsetY;
	
	/**
	 * texture index of the upper texture
	 */
	int upperTextureIndex;

	/**
	 * texture index of the lower texture
	 */
	int lowerTextureIndex;

	/**
	 * texture index of the middle texture
	 */
	int middleTextureIndex;
	
	/**
	 * The sector which this side faces
	 */
	struct Sector *sector;
	
};

/**
 * This structure defines a sector in the map.
 */
struct Sector {
	
	/**
	 * The height of the floor in this sector.
	 */
	FixedPointNumber floorHeight;
	
	/**
	 * The height of the ceiling in this sector.
	 */
	FixedPointNumber ceilingHeight;
	
	/**
	 * The flat index of the flat used for the floor.
	 */
	int floorFlatIndex;
	
	/**
	 * The flat index of the flat used for the ceiling.
	 */
	int ceilingFlatIndex;
	
	/**
	 * The light level (brightness) of this sector.
	 */
	short lightLevel;
	
	/**
	 * The special sector type, if any
	 */
	unsigned short type;
	
	/**
	 * The sector tag used to select this sector from a line
	 */
	unsigned short sectorTag;
	
	/**
	 * A linked list of map objects.
	 */
	struct MapObject *mapObjects;
	
};

/**
 * This structure defines a segment, i.e. part of a line
 * that results from line splitting during the BSP build process.
 */
struct Segment {
	
	/**
	 * The start vertex
	 */
	struct Vertex *startVertex;
	
	/**
	 * The end vertex
	 */
	struct Vertex *endVertex;

	/**
	 * TODO: ???
	 */
	// Angle angle;
	
	/**
	 * The line from which this segment was created
	 */
	struct Linedef *line;
	
	/**
	 * The side of the line on which this segment lies: 0 for right, 1 for left.
	 */
	unsigned short side;
	
	/**
	 * The sidedef from which this segment was created
	 */
	struct Sidedef *sidedef;
	
	/**
	 * The offset of the segment start from the line. This value is effectively added to the texture
	 * offset already specified in the sidedef.
	 */
	FixedPointNumber additionalTextureOffsetX;
	
	/**
	 * The sector on the other side of this segment, or NULL if one-sided. This data is not loaded
	 * but precomputed.
	 */
	struct Sector *backSector;
	
	/**
	 * Pre-computed length of this segment
	 */
	FixedPointNumber length;
	
};

/**
 * This structure defines a subsector, i.e. part of a sector
 * that results from sector splitting during the BSP build process.
 */
struct Subsector {
	
	/**
	 * The sector to which this subsector belongs
	 */
	struct Sector *sector;
	
	/**
	 * Number of segments in this subsector
	 */
	int segmentCount;
	
	/**
	 * Pointer to the first segment of this subsector.
	 */
	struct Segment *segments;
	
};

/**
 * A BSP node.
 */
struct Node {
	
	/**
	 * The x coordinate of the origin of the partition line.
	 */
	FixedPointNumber partitionLineOriginX;

	/**
	 * The y coordinate of the origin of the partition line.
	 */
	FixedPointNumber partitionLineOriginY;

	/**
	 * The x delta of the partition line.
	 */
	FixedPointNumber partitionLineDeltaX;
	
	/**
	 * The y delta of the partition line.
	 */
	FixedPointNumber partitionLineDeltaY;

	/**
	 * The bounding box of the right child of this node.
	 */
	struct BoundingBox rightChildBoundingBox;
	
	/**
	 * The bounding box of the left child of this node.
	 */
	struct BoundingBox leftChildBoundingBox;

	/**
	 * A number that identifies the right child of this node. If bit 15 of this
	 * number of cleared, the child is a subnode using the number as the node index.
	 * If bit 15 is set, the child is a subsector and the corresponding value with
	 * bit 15 cleared gives the subsector index.
	 */
	unsigned short rightChild;

	/**
	 * A number that identifies the right child of this node. See rightChild for
	 * an explanation of this number.
	 */
	unsigned short leftChild;
	
	/**
	 * The split line for this node. This data structure is actually used to
	 * determine which points are on which side.
	 */
	struct SplitLine splitLine;

};

/**
 * This structure defines the data for the current map. This data is initially
 * loaded from the WAD file and may change during the course of the map.
 */
struct CurrentMapData {
	
	/**
	 * This flag indicates whether a map is currently loaded.
	 */
	int loaded;
	
	/**
	 * The number of things in the map.
	 */
	int thingCount;
	
	/**
	 * The things.
	 */
	struct Thing *things;
	
	/**
	 * The number of vertices in the map.
	 */
	int vertexCount;
	
	/**
	 * The vertices.
	 */
	struct Vertex *vertices;
	
	/**
	 * The number of linedefs in the map.
	 */
	int linedefCount;
	
	/**
	 * The linedefs
	 */
	struct Linedef *linedefs;

	/**
	 * The number of sidedefs in the map.
	 */
	int sidedefCount;
	
	/**
	 * The sidedefs
	 */
	struct Sidedef *sidedefs;
	
	/**
	 * The number of sectors in the map.
	 */
	int sectorCount;
	
	/**
	 * The sectors
	 */
	struct Sector *sectors;
	
	/**
	 * The number of segments
	 */
	int segmentCount;
	
	/**
	 * The segments
	 */
	struct Segment *segments;

	/**
	 * The number of subsectors
	 */
	int subsectorCount;
	
	/**
	 * The subsectors
	 */
	struct Subsector *subsectors;

	/**
	 * The number of nodes
	 */
	int nodeCount;
	
	/**
	 * The nodes
	 */
	struct Node *nodes;
	
};

/**
 * The globally shared instance of CurrentMapData.
 */
extern struct CurrentMapData currentMapData;

/**
 * Loads the specified map from the shareware episode. If a map is already
 * loaded, it is disposed before the new map is loaded.
 */
void loadMap(int mapNumber);

/**
 * Disposes of all data for the current map. This function does nothing if
 * no map is currently loaded.
 */
void disposeMap();

/**
 * Returns the subsector that covers the specified position. If the position
 * is "in the void", this function still returns a subsector as determined
 * by the node structure, which is usually "near" the specified position.
 */
struct Subsector *getSubsectorForPosition(FixedPointNumber x, FixedPointNumber y);
