----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    23:28:05 11/27/2007 
-- Design Name: 
-- Module Name:    beeper - Behavioral 
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

entity sound_serializer is
	port (
		clk, reset : in std_logic;
		
		ldata_in, rdata_in : in std_logic_vector (19 downto 0);
		accepting : out std_logic;
		
		mclk : out std_logic;
		sclk : out std_logic;
		lrck : out std_logic;
		sdti : out std_logic
	);
end sound_serializer;

architecture Behavioral of sound_serializer is
	signal counter : std_logic_vector (9 downto 0);
	signal shiftreg : std_logic_vector (63 downto 0);
	signal bit_finished, stereo_sample_finished : boolean;
begin

	bit_finished <= counter (3 downto 0) = "1111";
	stereo_sample_finished <= counter (9 downto 0) = "1111111111";

	-- Use non-negated values for the clocks, such that the
	-- lrck and sdti signals do not change when mclk, sclk do.
	mclk <= counter (1);
	sclk <= counter (3);
	lrck <= counter (9);
	sdti <= shiftreg (63);
	accepting <= '1' when stereo_sample_finished else '0';

	process (clk) begin
		if (clk'event and clk = '1') then
			
			if reset = '1' then
			
				counter <= conv_std_logic_vector (0, 10);
				shiftreg <= conv_std_logic_vector (0,64);
			
			else
			
				if stereo_sample_finished then
					shiftreg (63 downto 32) <= "XXXXXXXXXXXX" & rdata_in;
					shiftreg (31 downto 0) <= "XXXXXXXXXXXX" & ldata_in;
				elsif bit_finished then
					shiftreg (63 downto 1) <= shiftreg (62 downto 0);
					shiftreg (0) <= 'X';
				end if;
			
				counter <= counter + 1;
				
			end if;

		end if;
	end process;
	
end Behavioral;
