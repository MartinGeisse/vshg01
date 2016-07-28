----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    10:53:20 12/02/2007 
-- Design Name: 
-- Module Name:    mctdisp - Behavioral 
-- Project Name: 
-- Target Devices: 
-- Tool versions: 
-- Description: 
--
-- Dependencies: 
--
-- Revision: 
-- Revision 0.01 - File Created
-- Additional Comments: 
--
----------------------------------------------------------------------------------
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

---- Uncomment the following library declaration if instantiating
---- any Xilinx primitives in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity mctdisp is
	port (
		clk, reset : in std_logic;
		bus_en, bus_wr : in std_logic;
		bus_addr : in std_logic_vector (12 downto 2);
		wdata : in std_logic_vector (7 downto 0);
		bus_wt : out std_logic;
		r, g, b : out std_logic;
		hsync, vsync : out std_logic
	);
end mctdisp;

architecture Behavioral of mctdisp is
	signal writeToMatrix : std_logic;
	
	signal stage0_x_integer : integer range 0 to 799;
	signal stage0_y_integer : integer range 0 to 524;
	signal stage0_x : std_logic_vector (9 downto 0);
	signal stage0_y : std_logic_vector (8 downto 0);
	signal stage0_hsync, stage0_vsync, stage0_active : std_logic;
	signal stage0_charMatrixAddress : std_logic_vector (10 downto 0);
	
	signal stage1_storedCharCode : std_logic_vector (7 downto 0);
	signal stage1_x, stage1_y : std_logic_vector (3 downto 1);
	signal stage1_odd : std_logic;
	signal stage1_hsync, stage1_vsync, stage1_active : std_logic;
	signal stage1_addr : std_logic_vector (11 downto 0);

	signal stage2_colorIndex : std_logic_vector (3 downto 0);
	signal stage2_hsync, stage2_vsync, stage2_active : std_logic;
	signal stage2_odd : std_logic;
	signal stage2_colorCode : std_logic_vector (5 downto 0);
	signal stage2_color : std_logic_vector (2 downto 0);
	signal stage2_activatedColor : std_logic_vector (2 downto 0);

begin
	writeToMatrix <= bus_en and bus_wr;
	bus_wt <= '0';

	vgaTimer : entity vga_timer generic map (
		clocks_per_pixel => 2,
		width => 640,
		height => 480,
		hfp => 16,
		vfp => 10,
		hbp => 48,
		vbp => 2,
		hsp => 96,
		vsp => 33,
		hpol => '0',
		vpol => '0'
	) port map (
		clk => clk,
		reset => reset,
		x => stage0_x_integer,
		y => stage0_y_integer,
		vs => stage0_vsync,
		hs => stage0_hsync,
		active => stage0_active
	);
	stage0_x <= conv_std_logic_vector (stage0_x_integer, 10) (9 downto 0);
	stage0_y <= conv_std_logic_vector (stage0_y_integer, 10) (8 downto 0);
	stage0_charMatrixAddress <= stage0_y (8 downto 4) & stage0_x (9 downto 4);
	
	charMatrix : entity matrix port map (
		clk => clk,
		client_write => writeToMatrix,
		client_addr => bus_addr,
		client_wdata => wdata,
		output_addr => stage0_charMatrixAddress,
		output_data => stage1_storedCharCode
	);
	process (clk) begin
		if clk'event and clk='1' then
			if reset = '1' then
				stage1_x <= "000";
				stage1_y <= "000";
				stage1_odd <= '0';
				stage1_hsync <= '1';
				stage1_vsync <= '1';
				stage1_active <= '0';
			else
				stage1_x (3 downto 1) <= stage0_x (3 downto 1);
				stage1_y (3 downto 1) <= stage0_y (3 downto 1);
				stage1_odd <= (stage0_x(0) xor stage0_y(0));
				stage1_hsync <= stage0_hsync;
				stage1_vsync <= stage0_vsync;
				stage1_active <= stage0_active;
			end if;
		end if;
	end process;
	stage1_addr (11 downto 6) <= stage1_storedCharCode (5 downto 0);
	stage1_addr (5 downto 3) <= stage1_y (3 downto 1);
	stage1_addr (2 downto 0) <= stage1_x (3 downto 1);
	
	charGenerator : entity chargen port map (
		clk => clk,
		output_addr => stage1_addr,
		output_data => stage2_colorIndex
	);
	process (clk) begin
		if clk'event and clk='1' then
			if reset = '1' then
				stage2_odd <= '0';
				stage2_hsync <= '1';
				stage2_vsync <= '1';
				stage2_active <= '0';
			else
				stage2_odd <= stage1_odd;
				stage2_hsync <= stage1_hsync;
				stage2_vsync <= stage1_vsync;
				stage2_active <= stage1_active;
			end if;
		end if;
	end process;

	with stage2_colorIndex select stage2_colorCode <=
		"000000" when "0000",
		"000001" when "0001",
		"000100" when "0010",
		"000101" when "0011",
		"010000" when "0100",
		"010001" when "0101",
		"010100" when "0110",
		"010101" when "0111",
		"010101" when "1000",
		"000011" when "1001",
		"001100" when "1010",
		"001111" when "1011",
		"110000" when "1100",
		"110011" when "1101",
		"111100" when "1110",
		"111111" when others;
	
	stage2_color <=
		(stage2_colorCode(5) or (stage2_colorCode(4) and stage2_odd)) &
		(stage2_colorCode(3) or (stage2_colorCode(2) and stage2_odd)) &
		(stage2_colorCode(1) or (stage2_colorCode(0) and stage2_odd));
		
	stage2_activatedColor <=
		stage2_color when stage2_active = '1'
		else "000";
		
	process (clk) begin
		if clk'event and clk='1' then
			if reset = '1' then
				hsync <= '1';
				vsync <= '1';
				r <= '0';
				g <= '0';
				b <= '0';
--				r <= "000";
--				g <= "000";
--				b <= "000";
			else
				hsync <= stage2_hsync;
				vsync <= stage2_vsync;
				r <= stage2_activatedColor (2);
				g <= stage2_activatedColor (1);
				b <= stage2_activatedColor (0);
--				r <= stage2_activatedColor (8 downto 6);
--				g <= stage2_activatedColor (5 downto 3);
--				b <= stage2_activatedColor (2 downto 0);
			end if;
		end if;
	end process;

end Behavioral;

