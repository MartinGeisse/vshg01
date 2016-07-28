
#
# system tests
#

COMMON_TEST_SRC := baselib/UnitTestSupport.c baselib/CommonTest.c
ZONE_ALLOCATOR_TEST_SRC := baselib/UnitTestSupport.c baselib/Common.c baselib/SystemDebugMock.c baselib/ZoneAllocatorTest.c

#
# system sources
#

SYSTEM_SRC := baselib/Common.c baselib/SystemDebug.c baselib/SystemMemory.c baselib/ZoneAllocator.c baselib/SystemKeyboard.c baselib/SystemGraphics.c

#
# math tests
#

FIXED_POINT_NUMBER_TEST_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) baselib/FixedPointNumberTest.c
ANGLE_TEST_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) baselib/FixedPointNumber.c baselib/AngleTest.c
BOUNDING_BOX_TEST_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) baselib/FixedPointNumber.c baselib/BoundingBoxTest.c
SPLIT_LINE_TEST_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) baselib/FixedPointNumber.c baselib/SplitLineTest.c

#
# math sources
#

MATH_SRC := baselib/FixedPointNumber.c baselib/Angle.c baselib/BoundingBox.c baselib/SplitLine.c

#
# static data / graphics tests
#

WAD_FILE_TEST_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) baselib/WadFileTest.c
STATIC_GRAPHICS_DATA_TEST_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) $(MATH_SRC) baselib/WadFile.c baselib/StaticGraphicsDataTest.c
SHOW_FLATS_MAIN_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) baselib/WadFile.c baselib/StaticGraphicsData.c baselib/TestMainCommon.c baselib/ShowFlatsMain.c
SHOW_TEXTURES_MAIN_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) baselib/WadFile.c baselib/StaticGraphicsData.c baselib/TestMainCommon.c baselib/ShowTexturesMain.c
LOWLEVEL_GRAPHICS_TEST_MAIN_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) baselib/WadFile.c baselib/StaticGraphicsData.c baselib/TestMainCommon.c baselib/LowlevelGraphics.c baselib/LowlevelGraphicsTestMain.c

#
# static data / graphics sources
#

STATIC_DATA_SRC := baselib/WadFile.c baselib/StaticGraphicsData.c baselib/LowlevelGraphics.c baselib/StaticGameData.c

#
# other tests
#

SOLID_OCCLUSION_CULLING_MAP_TEST_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) baselib/SolidOcclusionCullingMapTest.c
SHOW_MAP_MAIN_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) $(MATH_SRC) $(STATIC_DATA_SRC) baselib/TestMainCommon.c baselib/MapData.c baselib/ShowMapMain.c
AUTOMAP_MAIN_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) $(MATH_SRC) $(STATIC_DATA_SRC) baselib/TestMainCommon.c baselib/MapData.c baselib/Automap.c baselib/AutomapMain.c
WIDGET_TEST_MAIN_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) $(STATIC_DATA_SRC) baselib/TestMainCommon.c baselib/Widget.c baselib/WidgetTestMain.c
STATUS_BAR_TEST_MAIN_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) $(STATIC_DATA_SRC) baselib/TestMainCommon.c baselib/Widget.c baselib/StatusBar.c baselib/StatusBarTestMain.c
INTERMISSION_TEST_MAIN_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) $(STATIC_DATA_SRC) baselib/TestMainCommon.c baselib/Widget.c baselib/Intermission.c baselib/Player.c baselib/IntermissionTestMain.c
BSP_TEST_MAIN_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) $(MATH_SRC) $(STATIC_DATA_SRC) baselib/TestMainCommon.c baselib/MapData.c baselib/Automap.c baselib/BspTestMain.c
FIRST_PERSON_VIEW_TEST_MAIN_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) $(STATIC_DATA_SRC) $(MATH_SRC) baselib/TestMainCommon.c baselib/Widget.c baselib/Intermission.c baselib/Player.c baselib/MapData.c baselib/Automap.c baselib/FirstPersonView.c baselib/SolidOcclusionCullingMap.c baselib/ViewRasterizer.c baselib/Visplane.c baselib/FirstPersonViewTestMain.c baselib/StaticGameTables.c baselib/Thinker.c baselib/MapObject.c
VIEW_RASTERIZER_TEST_MAIN_SRC := baselib/UnitTestSupport.c $(SYSTEM_SRC) $(STATIC_DATA_SRC) $(MATH_SRC) baselib/TestMainCommon.c baselib/ViewRasterizer.c baselib/ViewRasterizerTestMain.c

HR := @echo -------------------------------------------------------------------------------------------------------

testCurrent: clean current
testAll: clean all run

bin/CommonTest: $(COMMON_TEST_SRC)
	gcc -Wall -m32 -o bin/CommonTest $(COMMON_TEST_SRC) `allegro-config --libs`

bin/FixedPointNumberTest: $(FIXED_POINT_NUMBER_TEST_SRC)
	gcc -Wall -m32 -o bin/FixedPointNumberTest $(FIXED_POINT_NUMBER_TEST_SRC) `allegro-config --libs`

bin/ZoneAllocatorTest: $(ZONE_ALLOCATOR_TEST_SRC)
	gcc -Wall -m32 -o bin/ZoneAllocatorTest $(ZONE_ALLOCATOR_TEST_SRC) `allegro-config --libs`

bin/WadFileTest: $(WAD_FILE_TEST_SRC)
	gcc -Wall -m32 -o bin/WadFileTest $(WAD_FILE_TEST_SRC) `allegro-config --libs`

bin/AngleTest: $(ANGLE_TEST_SRC)
	gcc -Wall -m32 -o bin/AngleTest $(ANGLE_TEST_SRC) `allegro-config --libs`

bin/ShowFlatsMain: $(SHOW_FLATS_MAIN_SRC)
	gcc -Wall -m32 -o bin/ShowFlatsMain $(SHOW_FLATS_MAIN_SRC) `allegro-config --libs`

bin/ShowTexturesMain: $(SHOW_TEXTURES_MAIN_SRC)
	gcc -Wall -m32 -o bin/ShowTexturesMain $(SHOW_TEXTURES_MAIN_SRC) `allegro-config --libs`

bin/StaticGraphicsDataTest: $(STATIC_GRAPHICS_DATA_TEST_SRC)
	gcc -Wall -m32 -o bin/StaticGraphicsDataTest $(STATIC_GRAPHICS_DATA_TEST_SRC) `allegro-config --libs`

bin/BoundingBoxTest: $(BOUNDING_BOX_TEST_SRC)
	gcc -Wall -m32 -o bin/BoundingBoxTest $(BOUNDING_BOX_TEST_SRC) `allegro-config --libs`

bin/LowlevelGraphicsTestMain: $(LOWLEVEL_GRAPHICS_TEST_MAIN_SRC)
	gcc -Wall -m32 -o bin/LowlevelGraphicsTestMain $(LOWLEVEL_GRAPHICS_TEST_MAIN_SRC) `allegro-config --libs`

bin/ShowMapMain: $(SHOW_MAP_MAIN_SRC)
	gcc -Wall -m32 -o bin/ShowMapMain $(SHOW_MAP_MAIN_SRC) `allegro-config --libs`

bin/AutomapMain: $(AUTOMAP_MAIN_SRC)
	gcc -Wall -m32 -o bin/AutomapMain $(AUTOMAP_MAIN_SRC) `allegro-config --libs`

bin/WidgetTestMain: $(WIDGET_TEST_MAIN_SRC)
	gcc -Wall -m32 -o bin/WidgetTestMain $(WIDGET_TEST_MAIN_SRC) `allegro-config --libs`

bin/StatusBarTestMain: $(STATUS_BAR_TEST_MAIN_SRC)
	gcc -Wall -m32 -o bin/StatusBarTestMain $(STATUS_BAR_TEST_MAIN_SRC) `allegro-config --libs`

bin/IntermissionTestMain: $(INTERMISSION_TEST_MAIN_SRC)
	gcc -Wall -m32 -o bin/IntermissionTestMain $(INTERMISSION_TEST_MAIN_SRC) `allegro-config --libs`

bin/BspTestMain: $(BSP_TEST_MAIN_SRC)
	gcc -Wall -m32 -o bin/BspTestMain $(BSP_TEST_MAIN_SRC) `allegro-config --libs`

bin/FirstPersonViewTestMain: $(FIRST_PERSON_VIEW_TEST_MAIN_SRC)
	gcc -Wall -m32 -o bin/FirstPersonViewTestMain $(FIRST_PERSON_VIEW_TEST_MAIN_SRC) `allegro-config --libs`

bin/SolidOcclusionCullingMapTest: $(SOLID_OCCLUSION_CULLING_MAP_TEST_SRC)
	gcc -Wall -m32 -o bin/SolidOcclusionCullingMapTest $(SOLID_OCCLUSION_CULLING_MAP_TEST_SRC) `allegro-config --libs`

bin/SplitLineTest: $(SPLIT_LINE_TEST_SRC)
	gcc -Wall -m32 -o bin/SplitLineTest $(SPLIT_LINE_TEST_SRC) `allegro-config --libs`

bin/ViewRasterizerTestMain: $(VIEW_RASTERIZER_TEST_MAIN_SRC)
	gcc -Wall -m32 -o bin/ViewRasterizerTestMain $(VIEW_RASTERIZER_TEST_MAIN_SRC) `allegro-config --libs`

all: bin/CommonTest bin/FixedPointNumberTest bin/ZoneAllocatorTest bin/WadFileTest bin/AngleTest bin/ShowFlatsMain bin/ShowTexturesMain bin/StaticGraphicsDataTest bin/LowlevelGraphicsTestMain bin/ShowMapMain bin/AutomapMain bin/WidgetTestMain bin/StatusBarTestMain bin/BoundingBoxTest bin/IntermissionTestMain bin/SolidOcclusionCullingMapTest bin/SplitLineTest bin/FirstPersonViewTestMain bin/BspTestMain bin/ViewRasterizerTestMain

current: bin/FirstPersonViewTestMain
	bin/FirstPersonViewTestMain

run:
	$(HR)
	bin/CommonTest	
	$(HR)
	bin/FixedPointNumberTest	
	$(HR)
	bin/ZoneAllocatorTest	
	$(HR)
	bin/WadFileTest
	$(HR)
	bin/AngleTest
	$(HR)
	bin/StaticGraphicsDataTest
	$(HR)
	bin/BoundingBoxTest
	$(HR)
	bin/SolidOcclusionCullingMapTest
	$(HR)
	bin/SplitLineTest
	$(HR)

clean:
	rm -f bin/*
