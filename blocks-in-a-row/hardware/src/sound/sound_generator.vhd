----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    01:55:19 11/28/2007 
-- Design Name: 
-- Module Name:    sound_generator - Behavioral 
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

entity sound_generator is
	port (
		clk, reset : in std_logic;
		ldata_out, rdata_out : out std_logic_vector (19 downto 0);
		next_sample : in std_logic;
		
		shape : in std_logic_vector (1 downto 0);
		step_delay : in std_logic_vector (31 downto 0);
		slope : in std_logic_vector (19 downto 0)
	);
end sound_generator;

-- implementation note: Unsigned counters are used internally
-- for internal_value and delay_counter. The output value

-- is 20-bit signed (2's complement).
architecture Behavioral of sound_generator is
	signal delay_counter : std_logic_vector (31 downto 0);
	signal internal_value : std_logic_vector (20 downto 0);
	signal noise_register : std_logic_vector (19 downto 0);

	signal output_value : std_logic_vector (19 downto 0);
	signal rectangle_output_value : std_logic_vector (19 downto 0);
	signal sawtooth_output_value : std_logic_vector (19 downto 0);
	signal triangle_output_value : std_logic_vector (19 downto 0);
	signal noise_output_value : std_logic_vector (19 downto 0);
	
	signal triangle_helper : std_logic_vector (18 downto 0);
	signal triangle_down : std_logic;
	signal noise_feedback_bit : std_logic;
begin

	ldata_out <= output_value;
	rdata_out <= output_value;
	
	process (clk) begin
		if (clk'event and clk = '1') then
			if reset = '1' then
				delay_counter <= conv_std_logic_vector (0, 32);
				internal_value <= conv_std_logic_vector (0, 21);
				noise_register <= conv_std_logic_vector (1, 20);
			else
		
				if delay_counter = 0 then
					delay_counter <= step_delay;
					internal_value <= internal_value + ("0" & slope);
					noise_register (19 downto 0) <=
						noise_feedback_bit & noise_register (19 downto 1);
				else
					delay_counter <= delay_counter - 1;
				end if;
				
			end if;
		end if;
	end process;
	
	with shape select output_value <=
		rectangle_output_value when "00",
		sawtooth_output_value when "01",
		triangle_output_value when "10",
		noise_output_value when others;
	
	rectangle_output_value <=
		"01111111111111111111" when internal_value (19) = '1' else
		"10000000000000000000";
		
	sawtooth_output_value <=
		internal_value (19 downto 0);
		
	triangle_output_value <=
		internal_value (20) & triangle_helper;
	
	triangle_helper <=
		(not internal_value (18 downto 0))
			when (triangle_down = '1')
		else (internal_value (18 downto 0));
		
	triangle_down <=
		internal_value (20) xor internal_value (19);
	
	noise_output_value <=
		noise_register;
	
	noise_feedback_bit <=
		noise_register (0) xor noise_register (1) xor
		noise_register (4) xor noise_register (18);

end Behavioral;
