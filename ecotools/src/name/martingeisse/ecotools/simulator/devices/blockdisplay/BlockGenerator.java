/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.devices.blockdisplay;

/**
 * This class contains the static block images.
 */
public class BlockGenerator {

	/**
	 * The block image specifications. Each element of this array defines
	 * the looks of a single block. The contents of the array element must
	 * be interpreted as the texels of the block, line by line. Each texel
	 * is specified as a hexadecimal digit that specifies a BIOS-style
	 * color.
	 */
	public static final String[] BLOCK_DATA = {
		"0000000000000000000000000000000000000000000000000000000000000000",
		"9999999991111119911111199119911991199119911111199111111999999999",
		"aaaaaaaaa222222aa222222aa22aa22aa22aa22aa222222aa222222aaaaaaaaa",
		"bbbbbbbbb333333bb333333bb33bb33bb33bb33bb333333bb333333bbbbbbbbb",
		"ccccccccc444444cc444444cc44cc44cc44cc44cc444444cc444444ccccccccc",
		"ddddddddd555555dd555555dd55dd55dd55dd55dd555555dd555555ddddddddd",
		"eeeeeeeee666666ee666666ee66ee66ee66ee66ee666666ee666666eeeeeeeee",
		"fffffffff777777ff777777ff77ff77ff77ff77ff777777ff777777fffffffff",
		"7777777778888887788888877887788778877887788888877888888777777777",
		"2222222222222222222222222222222222222222222222222222222222222222",
		"2222222222222222222222227777777777777777222222222222222222222222",
		"2227722222277222222772222227722222277222222772222227722222277222",
		"2227722222277222227777227777777777777777227777222227722222277222",
		"2222222222222222222222227777222277777222227772222227722222277222",
		"2222222222222222222222222222777722277777222777222227722222277222",
		"2227722222277222227772227777722277772222222222222222222222222222",
		"2227722222277222222777222227777722227777222222222222222222222222",
		"0ef00ef00ef00ef00eeeeef00eeffef00ef00ef00eeeeef000ffff0000000000",
		"00eeeef00eeffef00ef00ef000effef00ef00ef00eeeeef000fffff000000000",
		"00eeee000efffef00ef00ef000000ef00ef00ef00eeeeef000ffff0000000000",
		"00eeeef00efffef00ef00ef00ef00ef00ef00ef00eeeeef000fffff000000000",
		"0eeeeef00ffffef000000ef000fffef000000ef00eeeeef00ffffff000000000",
		"00000ef000000ef000000ef000fffef000000ef00eeeeef00ffffff000000000",
		"00eeee000efffef00ef00ef00fff0ef000000ef00eeeeef000ffff0000000000",
		"0ef00ef00ef00ef00ef00ef00eeeeef00efffef00ef00ef00ef00ef000000000",
		"00eeef0000feef00000ef000000ef000000ef00000eeef0000ffff0000000000",
		"00eeee000eeffef00ef00ff00ef000000ef000000eeeeef00ffffff000000000",
		"0ee00ef00eee0ef000eeeef0000eeef000eefef00eef0ef00ff00ef000000000",
		"0eeeeef00ffffef000000ef000000ef000000ef000000ef000000ff000000000",
		"ee000ef0ee000ef0ee0e0ef0eeeeeef0eeefeef0eef0fef0ff000ff000000000",
		"ee000ef0eee00ef0eeee0ef0eefeeef0ef0feef0ef00fef0ff000ff000000000",
		"00eeee000eeffef00ef00ef00ef00ef00ef00ef00eeeeef000ffff0000000000",
		"00000ef000000ef000eeeef00eeffef00ee00ef00eeeeef000fffff000000000",
		"0eeeee000eeeeef00eef0ef00ee00ef00ee00ef00eeeeef000ffff0000000000",
		"0ee00ef000ee0ef000eeeef00eeffef00ee00ef00eeeeef000fffff000000000",
		"00eeeef00efffff00ef0000000ffff0000000ef00eeeeef00fffff0000000000",
		"000ef000000ef000000ef000000ef000000ef0000eeeeee00ffffff000000000",
		"00eeee000eeffef00ef00ef00ef00ef00ef00ef00ef00ef00ff00ff000000000",
		"000ef00000eeef0000eeef0000efff000ef00ef00ef00ef00ff00ff000000000",
		"0ee0ef00eefefef0ef0f0ef0ef0f0ef0ef000ef0ef000ef0ff000ff000000000",
		"ee000ef0eee0eef00eeeef0000eef0000eefef00eef0fef0ff000ff000000000",
		"000ef000000ef000000ef00000eeef000efffef00ef00ef00ff00ff000000000",
		"eeeeeef0fffeeef0000eef0000eef0000eef0000eeeeeef0fffffff000000000",
		"00eeee000eefeef00ef0eff00eeefef00eef0ef00eeeeef000ffff0000000000",
		"000ef000000ef000000ef000000ef000000eeef0000eef00000ff00000000000",
		"0eeeeef00ffeeef0000eef0000eef0000eef0ef00feeeef000ffff0000000000",
		"00eeee000eeffff00ff000f000eff0000ef000f00feeeef000ffff0000000000",
		"0ef000000ef000000ef000000eeeeef00eeffef00ef00ef00ff00ff000000000",
		"00eeeef00eeffff00ef0000000fffff0000000f00eeeeef00ffffff000000000",
		"00eeee000eeffef00ef00ef000fffef000000ef00feeeef000ffff0000000000",
		"0000ef000000ef00000eef0000eef0000eef00000eeeeef00ffffff000000000",
		"00eeee000eeffef00ef00ef000efff000ef00ef00eeeeef000ffff0000000000",
		"00eeee000eeffff00ef000000eeffe000ef00ef00eeeeef000ffff0000000000",
		"00000000000ef000000ff00000000000000ef000000ff0000000000000000000",
		"000ef000000ff00000000000000ef000000ef000000ef000000ff00000000000",
		"0000000000000000000000000eeeeef00ffffff0000000000000000000000000",
		"000ef000000ff000000000000000000000000000000000000000000000000000",
		"0000000000000000000000000000000000000000000000000000000000000000",
		"0000000000000000000000000000000000000000000000000000000000000000",
		"0000000000000000000000000000000000000000000000000000000000000000",
		"0000000000000000000000000000000000000000000000000000000000000000",
		"0000000000000000000000000000000000000000000000000000000000000000",
		"0000000000000000000000000000000000000000000000000000000000000000",
		"0000000000000000000000000000000000000000000000000000000000000000"
	};
	
}