----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    15:44:00 07/15/2006 
-- Design Name: 
-- Module Name:    vga_timer - Behavioral 
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

entity vga_timer is
	generic (
		-- system clock to pixel clock divisor
		clocks_per_pixel : integer;
		-- visible screen size
		width, height : integer;
		-- front porch width
		hfp, vfp : integer;
		-- back porch width
		hbp, vbp : integer;
		-- synch pulse width
		hsp, vsp : integer;
		-- synch pulse polarity
		hpol, vpol : std_logic
	);
	port (
		-- control signals
		clk, reset : in std_logic;
		-- current pixel position
		x : out integer range 0 to width + hfp + hsp + hbp - 1;
		y : out integer range 0 to height + vfp + vsp + vbp - 1;
		z : out integer range 0 to clocks_per_pixel - 1;
		-- synch signals
		vs, hs : out std_logic;
		-- current pixel is active (not to be blanked)
		active : out std_logic
	);
end vga_timer;

architecture Behavioral of vga_timer is
	signal hblank, vblank : std_logic;
	signal curx : integer range 0 to width + hfp + hsp + hbp - 1;
	signal cury : integer range 0 to height + vfp + vsp + vbp - 1;
	signal curz : integer range 0 to clocks_per_pixel - 1;
begin
	active <= (not hblank) and (not vblank);
	x <= curx;
	y <= cury;
	z <= curz;
	process (clk) begin
		if clk'event and clk='1' then
			if reset='1' then
				curx <= 0;
				cury <= 0;
				curz <= 0;
				hs <= not hpol;
				vs <= not vpol;
				hblank <= '0';
				vblank <= '0';
			else
				if curz = clocks_per_pixel - 1 then

					curz <= 0;
					if curx = width - 1 then
						hblank <= '1'; end if;
					if curx = width + hfp - 1 then
						hs <= hpol; end if;
					if curx = width + hfp + hsp - 1 then
						hs <= not hpol; end if;

					if curx = width + hfp + hsp + hbp - 1 then

						hblank <= '0';
						curx <= 0;
						if cury = height - 1 then
							vblank <= '1'; end if;
						if cury = height + vfp - 1 then
							vs <= vpol; end if;
						if cury = height + vfp + vsp - 1 then
							vs <= not vpol; end if;
					
						if cury = height + vfp + vsp + vbp - 1 then
							vblank <= '0';
							cury <= 0;
						else cury <= cury + 1;
						end if;
					
					else curx <= curx + 1;
					end if;

				else curz <= curz + 1;
				end if;
			end if;
		end if;
	end process;

end Behavioral;
