----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    02:44:30 11/28/2007 
-- Design Name: 
-- Module Name:    sound_module - Behavioral 
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

entity sound_module is
	port (
		clk, reset : in std_logic;
		
		bus_en, bus_wr : in std_logic;
		bus_addr : in std_logic_vector (3 downto 2);
		wdata : in std_logic_vector (31 downto 0);
		
		mclk : out std_logic;
		sclk : out std_logic;
		lrck : out std_logic;
		sdti : out std_logic
	);
end sound_module;

architecture Behavioral of sound_module is
	signal ldata, rdata : std_logic_vector (19 downto 0);
	signal next_sample : std_logic;

	signal shape_register : std_logic_vector (1 downto 0);
	signal step_delay_register : std_logic_vector (31 downto 0);
	signal slope_register : std_logic_vector (19 downto 0);
	
	component sound_serializer is
		port (
			clk, reset : in std_logic;
			ldata_in, rdata_in : in std_logic_vector (19 downto 0);
			accepting : out std_logic;
			mclk : out std_logic;
			sclk : out std_logic;
			lrck : out std_logic;
			sdti : out std_logic
		);
	end component;

	component sound_generator is
		port (
			clk, reset : in std_logic;
			ldata_out, rdata_out : out std_logic_vector (19 downto 0);
			next_sample : in std_logic;
			shape : in std_logic_vector (1 downto 0);
			step_delay : in std_logic_vector (31 downto 0);
			slope : in std_logic_vector (19 downto 0)
		);
	end component;
	
begin

	serializer : sound_serializer port map (
		clk => clk,
		reset => reset,
		ldata_in => ldata,
		rdata_in => rdata,
		accepting => next_sample,
		mclk => mclk,
		sclk => sclk,
		lrck => lrck,
		sdti => sdti
	);
	
	generator : sound_generator port map (
		clk => clk,
		reset => reset,
		ldata_out => ldata,
		rdata_out => rdata,
		next_sample => next_sample,
		shape => shape_register,
		step_delay => step_delay_register,
		slope => slope_register
	);
	
	process (clk) begin
		if (clk'event and clk = '1') then
			if reset = '1' then
				shape_register <= conv_std_logic_vector (0, 2);
				step_delay_register <= conv_std_logic_vector (0, 32);
				slope_register <= conv_std_logic_vector (0, 20);
			else
				if (bus_en = '1') and (bus_wr = '1') then
					if bus_addr (3 downto 2) = "00" then
						shape_register <= wdata (1 downto 0);
					elsif bus_addr (3 downto 2) = "01" then
						step_delay_register <= wdata (31 downto 0);
					elsif bus_addr (3 downto 2) = "10" then
						slope_register <= wdata (19 downto 0);
					end if;
				end if;
			end if;
		end if;
	end process;
	
end Behavioral;
