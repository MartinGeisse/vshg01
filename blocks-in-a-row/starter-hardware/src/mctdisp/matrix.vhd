----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    11:11:31 12/02/2007 
-- Design Name: 
-- Module Name:    matrix - Behavioral 
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
library UNISIM;
use UNISIM.VComponents.all;

entity matrix is
	port (
		clk : in std_logic;
	
		client_write : in std_logic;
		client_addr : in std_logic_vector (10 downto 0);
		client_wdata : in std_logic_vector (7 downto 0);
	
		output_addr : in std_logic_vector (10 downto 0);
		output_data : out std_logic_vector (7 downto 0)
	);
end matrix;

architecture Behavioral of matrix is
	signal oneSignal, zeroSignal : std_logic;
	signal xSignal : std_logic_vector (0 downto 0);
	signal xVector : std_logic_vector (7 downto 0);
begin
	zeroSignal <= '0';
	oneSignal <= '1';
	xSignal <= "X";
	xVector <= "XXXXXXXX";
	
	ramblock : RAMB16_S9_S9 generic map (
		WRITE_MODE_A => "WRITE_FIRST",
		WRITE_MODE_B => "WRITE_FIRST"
	) port map (
		DOB => output_data,
		ADDRA => client_addr,
		ADDRB => output_addr,
		CLKA => clk,
		CLKB => clk,
		DIA => client_wdata,
		DIB => xVector,
		ENA => oneSignal,
		ENB => oneSignal,
		SSRA => zeroSignal,
		SSRB => zeroSignal,
		WEA => client_write,
		WEB => zeroSignal,
		DIPA => xSignal,
		DIPB => xSignal
   );
end Behavioral;
