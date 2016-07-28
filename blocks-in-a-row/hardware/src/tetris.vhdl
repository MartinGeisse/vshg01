-- Generated from model: Test.Bus@BusHeaderCompiler

library IEEE;
use IEEE.std_logic_1164.all;

entity blocks_in_a_row is
	port (
		clk_in : in std_logic;
		reset_inout_n : inout std_logic;
		sdram_clk : out std_logic;
		sdram_fb : in std_logic;
		
		display_b : out std_logic_vector (2 downto 0);
		display_g : out std_logic_vector (2 downto 0);
		display_hsync : out std_logic;
		display_r : out std_logic_vector (2 downto 0);
		display_vsync : out std_logic;

		keyboard_ps2_clk : in std_logic;
		keyboard_ps2_data : in std_logic;

		ram_sdram_a : out std_logic_vector (12 downto 0);
		ram_sdram_ba : out std_logic_vector (1 downto 0);
		ram_sdram_cas_n : out std_logic;
		ram_sdram_cke : out std_logic;
		ram_sdram_cs_n : out std_logic;
		ram_sdram_dq : inout std_logic_vector (15 downto 0);
		ram_sdram_ldqm : out std_logic;
		ram_sdram_ras_n : out std_logic;
		ram_sdram_udqm : out std_logic;
		ram_sdram_we_n : out std_logic;

		rom_a : out std_logic_vector (19 downto 0);
		rom_byte_n : out std_logic;
		rom_ce_n : out std_logic;
		rom_d : in std_logic_vector (15 downto 0);
		rom_oe_n : out std_logic;
		rom_rst_n : out std_logic;
		rom_we_n : out std_logic;

		sound_mclk : out std_logic;
		sound_sclk : out std_logic;
		sound_lrck : out std_logic;
		sound_sdti : out std_logic
	);
end entity blocks_in_a_row;

architecture structural of blocks_in_a_row is

	signal clk, clk_ok, reset : std_logic;
	
	component clk_reset is
		port (
			clk_in : in std_logic;
			reset_inout_n : inout std_logic;
			sdram_clk : out std_logic;
			sdram_fb : in std_logic;
			clk : out std_logic;
			clk_ok : out std_logic;
			reset : out std_logic
		);
	end component;

	component busmap is
		port (
			clk, reset : in std_logic;

			mctdisp_b : out std_logic_vector (2 downto 0);
			mctdisp_g : out std_logic_vector (2 downto 0);
			mctdisp_hsync : out std_logic;
			mctdisp_r : out std_logic_vector (2 downto 0);
			mctdisp_vsync : out std_logic;

			keyboard_ps2_clk : in std_logic;
			keyboard_ps2_data : in std_logic;

			ram_clk_ok : in std_logic;
			ram_sdram_a : out std_logic_vector (12 downto 0);
			ram_sdram_ba : out std_logic_vector (1 downto 0);
			ram_sdram_cas_n : out std_logic;
			ram_sdram_cke : out std_logic;
			ram_sdram_cs_n : out std_logic;
			ram_sdram_dq : inout std_logic_vector (15 downto 0);
			ram_sdram_ldqm : out std_logic;
			ram_sdram_ras_n : out std_logic;
			ram_sdram_udqm : out std_logic;
			ram_sdram_we_n : out std_logic;

			rom_a : out std_logic_vector (19 downto 0);
			rom_byte_n : out std_logic;
			rom_ce_n : out std_logic;
			rom_d : in std_logic_vector (15 downto 0);
			rom_oe_n : out std_logic;
			rom_rst_n : out std_logic;
			rom_we_n : out std_logic;

			sound_mclk : out std_logic;
			sound_sclk : out std_logic;
			sound_lrck : out std_logic;
			sound_sdti : out std_logic
		);
	end component;

begin
	clk_reset1 : clk_reset port map (
		clk_in => clk_in,
		reset_inout_n => reset_inout_n,
		sdram_clk => sdram_clk,
		sdram_fb => sdram_fb,
		clk => clk,
		clk_ok => clk_ok,
		reset => reset
	);

	wrappee : busmap port map (
		clk => clk,
		reset => reset,

		mctdisp_b => display_b,
		mctdisp_g => display_g,
		mctdisp_hsync => display_hsync,
		mctdisp_r => display_r,
		mctdisp_vsync => display_vsync,

		keyboard_ps2_clk => keyboard_ps2_clk,
		keyboard_ps2_data => keyboard_ps2_data,

		ram_clk_ok => clk_ok,
		ram_sdram_a => ram_sdram_a,
		ram_sdram_ba => ram_sdram_ba,
		ram_sdram_cas_n => ram_sdram_cas_n,
		ram_sdram_cke => ram_sdram_cke,
		ram_sdram_cs_n => ram_sdram_cs_n,
		ram_sdram_dq => ram_sdram_dq,
		ram_sdram_ldqm => ram_sdram_ldqm,
		ram_sdram_ras_n => ram_sdram_ras_n,
		ram_sdram_udqm => ram_sdram_udqm,
		ram_sdram_we_n => ram_sdram_we_n,

		rom_a => rom_a,
		rom_byte_n => rom_byte_n,
		rom_ce_n => rom_ce_n,
		rom_d => rom_d,
		rom_oe_n => rom_oe_n,
		rom_rst_n => rom_rst_n,
		rom_we_n => rom_we_n,

		sound_mclk => sound_mclk,
		sound_sclk => sound_sclk,
		sound_lrck => sound_lrck,
		sound_sdti => sound_sdti
	);
	
end architecture structural; -- of blocks_in_a_row

