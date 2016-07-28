
library IEEE;
use IEEE.std_logic_1164.all;

entity busmap is
	port (
		clk : in std_logic;
		reset : in std_logic;
		keyboard_ps2_clk : in std_logic;
		keyboard_ps2_data : in std_logic;
		mctdisp_b : out std_logic_vector (2 downto 0);
		mctdisp_g : out std_logic_vector (2 downto 0);
		mctdisp_hsync : out std_logic;
		mctdisp_r : out std_logic_vector (2 downto 0);
		mctdisp_vsync : out std_logic;
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
		sound_lrck : out std_logic;
		sound_mclk : out std_logic;
		sound_sclk : out std_logic;
		sound_sdti : out std_logic
	);
end entity busmap;

architecture structural of busmap is

	signal \write\ : std_logic;
	signal \transferSize\ : std_logic_vector (1 downto 0);
	signal \irq\ : std_logic_vector (15 downto 0);

	component cpu is
		port (
			bus_addr : out std_logic_vector (31 downto 0);
			bus_data_in : in std_logic_vector (31 downto 0);
			bus_data_out : out std_logic_vector (31 downto 0);
			bus_en : out std_logic;
			bus_size : out std_logic_vector (1 downto 0);
			bus_wr : out std_logic;
			bus_wt : in std_logic;
			clk : in std_logic;
			irq : in std_logic_vector (15 downto 0);
			reset : in std_logic
		);
	end component;

	signal \master_addr\ : std_logic_vector (31 downto 0);
	signal \master_wdata\ : std_logic_vector (31 downto 0);
	signal \master_rdata\ : std_logic_vector (31 downto 0);
	signal \master_en\ : std_logic;
	signal \master_wait\ : std_logic;

	component kbd is
		port (
			addr2 : in std_logic;
			clk : in std_logic;
			data_in : in std_logic_vector (7 downto 0);
			data_out : out std_logic_vector (7 downto 0);
			en : in std_logic;
			irq : out std_logic;
			ps2_clk : in std_logic;
			ps2_data : in std_logic;
			reset : in std_logic;
			wr : in std_logic;
			wt : out std_logic
		);
	end component;

	signal \keyboard_addr\ : std_logic_vector (0 downto 0);
	signal \keyboard_wdata\ : std_logic_vector (7 downto 0);
	signal \keyboard_rdata\ : std_logic_vector (7 downto 0);
	signal \keyboard_en\ : std_logic;
	signal \keyboard_wait\ : std_logic;
	signal \keyboard_selected\ : boolean;
	signal \keyboard_irq_irq\ : std_logic;

	component mctdisp is
		port (
			b : out std_logic_vector (2 downto 0);
			bus_addr : in std_logic_vector (12 downto 2);
			bus_en : in std_logic;
			bus_wr : in std_logic;
			bus_wt : out std_logic;
			clk : in std_logic;
			g : out std_logic_vector (2 downto 0);
			hsync : out std_logic;
			r : out std_logic_vector (2 downto 0);
			reset : in std_logic;
			vsync : out std_logic;
			wdata : in std_logic_vector (7 downto 0)
		);
	end component;

	signal \mctdisp_addr\ : std_logic_vector (10 downto 0);
	signal \mctdisp_wdata\ : std_logic_vector (7 downto 0);
	signal \mctdisp_en\ : std_logic;
	signal \mctdisp_wait\ : std_logic;
	signal \mctdisp_selected\ : boolean;

	component ram is
		port (
			addr : in std_logic_vector (24 downto 0);
			clk : in std_logic;
			clk_ok : in std_logic;
			data_in : in std_logic_vector (31 downto 0);
			data_out : out std_logic_vector (31 downto 0);
			en : in std_logic;
			reset : in std_logic;
			sdram_a : out std_logic_vector (12 downto 0);
			sdram_ba : out std_logic_vector (1 downto 0);
			sdram_cas_n : out std_logic;
			sdram_cke : out std_logic;
			sdram_cs_n : out std_logic;
			sdram_dq : inout std_logic_vector (15 downto 0);
			sdram_ldqm : out std_logic;
			sdram_ras_n : out std_logic;
			sdram_udqm : out std_logic;
			sdram_we_n : out std_logic;
			size : in std_logic_vector (1 downto 0);
			wr : in std_logic;
			wt : out std_logic
		);
	end component;

	signal \ram_addr\ : std_logic_vector (24 downto 0);
	signal \ram_wdata\ : std_logic_vector (31 downto 0);
	signal \ram_rdata\ : std_logic_vector (31 downto 0);
	signal \ram_en\ : std_logic;
	signal \ram_wait\ : std_logic;
	signal \ram_selected\ : boolean;

	component rom is
		port (
			a : out std_logic_vector (19 downto 0);
			addr : in std_logic_vector (20 downto 0);
			byte_n : out std_logic;
			ce_n : out std_logic;
			clk : in std_logic;
			d : in std_logic_vector (15 downto 0);
			data_out : out std_logic_vector (31 downto 0);
			en : in std_logic;
			oe_n : out std_logic;
			reset : in std_logic;
			rst_n : out std_logic;
			size : in std_logic_vector (1 downto 0);
			we_n : out std_logic;
			wr : in std_logic;
			wt : out std_logic
		);
	end component;

	signal \rom_addr\ : std_logic_vector (20 downto 0);
	signal \rom_rdata\ : std_logic_vector (31 downto 0);
	signal \rom_en\ : std_logic;
	signal \rom_wait\ : std_logic;
	signal \rom_selected\ : boolean;

	component sound_module is
		port (
			bus_addr : in std_logic_vector (3 downto 2);
			bus_en : in std_logic;
			bus_wr : in std_logic;
			clk : in std_logic;
			lrck : out std_logic;
			mclk : out std_logic;
			reset : in std_logic;
			sclk : out std_logic;
			sdti : out std_logic;
			wdata : in std_logic_vector (31 downto 0)
		);
	end component;

	signal \sound_addr\ : std_logic_vector (1 downto 0);
	signal \sound_wdata\ : std_logic_vector (31 downto 0);
	signal \sound_en\ : std_logic;
	signal \sound_selected\ : boolean;

	component tmr is
		port (
			addr2 : in std_logic;
			clk : in std_logic;
			data_in : in std_logic_vector (31 downto 0);
			data_out : out std_logic_vector (31 downto 0);
			en : in std_logic;
			irq : out std_logic;
			reset : in std_logic;
			wr : in std_logic;
			wt : out std_logic
		);
	end component;

	signal \timer_addr\ : std_logic_vector (0 downto 0);
	signal \timer_wdata\ : std_logic_vector (31 downto 0);
	signal \timer_rdata\ : std_logic_vector (31 downto 0);
	signal \timer_en\ : std_logic;
	signal \timer_wait\ : std_logic;
	signal \timer_selected\ : boolean;
	signal \timer_irq_irq\ : std_logic;

begin

	instance_master : cpu port map (
		clk => clk,
		reset => reset,
		bus_addr => \master_addr\,
		bus_data_out => \master_wdata\,
		bus_data_in => \master_rdata\,
		bus_en => \master_en\,
		bus_wr => \write\,
		bus_wt => \master_wait\,
		bus_size => \transferSize\,
		irq => \irq\
	);

	instance_keyboard : kbd port map (
		clk => clk,
		reset => reset,
		addr2 => \keyboard_addr\ (0),
		data_in => \keyboard_wdata\,
		data_out => \keyboard_rdata\,
		en => \keyboard_en\,
		wr => \write\,
		wt => \keyboard_wait\,
		irq => \keyboard_irq_irq\,
		ps2_clk => keyboard_ps2_clk,
		ps2_data => keyboard_ps2_data
	);

	instance_mctdisp : mctdisp port map (
		clk => clk,
		reset => reset,
		bus_addr => \mctdisp_addr\,
		wdata => \mctdisp_wdata\,
		bus_en => \mctdisp_en\,
		bus_wr => \write\,
		bus_wt => \mctdisp_wait\,
		b => mctdisp_b,
		g => mctdisp_g,
		hsync => mctdisp_hsync,
		r => mctdisp_r,
		vsync => mctdisp_vsync
	);

	instance_ram : ram port map (
		clk => clk,
		reset => reset,
		addr => \ram_addr\,
		data_in => \ram_wdata\,
		data_out => \ram_rdata\,
		en => \ram_en\,
		wr => \write\,
		wt => \ram_wait\,
		size => \transferSize\,
		clk_ok => ram_clk_ok,
		sdram_a => ram_sdram_a,
		sdram_ba => ram_sdram_ba,
		sdram_cas_n => ram_sdram_cas_n,
		sdram_cke => ram_sdram_cke,
		sdram_cs_n => ram_sdram_cs_n,
		sdram_dq => ram_sdram_dq,
		sdram_ldqm => ram_sdram_ldqm,
		sdram_ras_n => ram_sdram_ras_n,
		sdram_udqm => ram_sdram_udqm,
		sdram_we_n => ram_sdram_we_n
	);

	instance_rom : rom port map (
		clk => clk,
		reset => reset,
		addr => \rom_addr\,
		data_out => \rom_rdata\,
		en => \rom_en\,
		wr => \write\,
		wt => \rom_wait\,
		size => \transferSize\,
		a => rom_a,
		byte_n => rom_byte_n,
		ce_n => rom_ce_n,
		d => rom_d,
		oe_n => rom_oe_n,
		rst_n => rom_rst_n,
		we_n => rom_we_n
	);

	instance_sound : sound_module port map (
		clk => clk,
		reset => reset,
		bus_addr => \sound_addr\,
		wdata => \sound_wdata\,
		bus_en => \sound_en\,
		bus_wr => \write\,
		lrck => sound_lrck,
		mclk => sound_mclk,
		sclk => sound_sclk,
		sdti => sound_sdti
	);

	instance_timer : tmr port map (
		clk => clk,
		reset => reset,
		addr2 => \timer_addr\ (0),
		data_in => \timer_wdata\,
		data_out => \timer_rdata\,
		en => \timer_en\,
		wr => \write\,
		wt => \timer_wait\,
		irq => \timer_irq_irq\
	);

	\master_rdata\ <= 
		"000000000000000000000000" & \keyboard_rdata\ (7 downto 0) when \keyboard_selected\ else
		"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" when \mctdisp_selected\ else
		\ram_rdata\ (31 downto 0) when \ram_selected\ else
		\rom_rdata\ (31 downto 0) when \rom_selected\ else
		"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" when \sound_selected\ else
		\timer_rdata\ (31 downto 0) when \timer_selected\ else
		"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

	\master_wait\ <= 
		\keyboard_wait\ when \keyboard_selected\ else
		\mctdisp_wait\ when \mctdisp_selected\ else
		\ram_wait\ when \ram_selected\ else
		\rom_wait\ when \rom_selected\ else
		'0' when \sound_selected\ else
		\timer_wait\ when \timer_selected\ else
		'1';

	\irq\ <= 
		"0" & \timer_irq_irq\ & "000000000" & \keyboard_irq_irq\ & "0000";

	\keyboard_addr\ <= \master_addr\ (2 downto 2);
	\keyboard_wdata\ (7 downto 0) <= \master_wdata\ (7 downto 0);
	\keyboard_en\ <= \master_en\ when \keyboard_selected\ else '0';
	\keyboard_selected\ <= (\master_addr\ (31 downto 20) = "001100000010");

	\mctdisp_addr\ <= \master_addr\ (12 downto 2);
	\mctdisp_wdata\ (7 downto 0) <= \master_wdata\ (7 downto 0);
	\mctdisp_en\ <= \master_en\ when \mctdisp_selected\ else '0';
	\mctdisp_selected\ <= (\master_addr\ (31 downto 20) = "001100000001");

	\ram_addr\ <= \master_addr\ (24 downto 0);
	\ram_wdata\ (31 downto 0) <= \master_wdata\ (31 downto 0);
	\ram_en\ <= \master_en\ when \ram_selected\ else '0';
	\ram_selected\ <= (\master_addr\ (31 downto 25) = "0000000");

	\rom_addr\ <= \master_addr\ (20 downto 0);
	\rom_en\ <= \master_en\ when \rom_selected\ else '0';
	\rom_selected\ <= (\master_addr\ (31 downto 21) = "00100000000");

	\sound_addr\ <= \master_addr\ (3 downto 2);
	\sound_wdata\ (31 downto 0) <= \master_wdata\ (31 downto 0);
	\sound_en\ <= \master_en\ when \sound_selected\ else '0';
	\sound_selected\ <= (\master_addr\ (31 downto 20) = "001100001000");

	\timer_addr\ <= \master_addr\ (2 downto 2);
	\timer_wdata\ (31 downto 0) <= \master_wdata\ (31 downto 0);
	\timer_en\ <= \master_en\ when \timer_selected\ else '0';
	\timer_selected\ <= (\master_addr\ (31 downto 20) = "001100000000");

end architecture structural; -- of busmap

