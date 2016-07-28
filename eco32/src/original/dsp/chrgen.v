module chrgen(clk, pixclk,
              chrcode, chrrow, chrcol,
              pixel,
              attcode_in, blank_in, hsync_in, vsync_in, blink_in,
              attcode_out, blank_out, hsync_out, vsync_out, blink_out);
    input clk;
    input pixclk;
    input [7:0] chrcode;
    input [3:0] chrrow;
    input [2:0] chrcol;
    output pixel;
    input [7:0] attcode_in;
    input blank_in;
    input hsync_in;
    input vsync_in;
    input blink_in;
    output reg [7:0] attcode_out;
    output reg blank_out;
    output reg hsync_out;
    output reg vsync_out;
    output reg blink_out;

  wire [13:0] addr;
  wire [0:0] pixel_lo;
  wire [0:0] pixel_hi;

  reg mux_ctrl;

  assign addr[13:7] = chrcode[6:0];
  assign addr[6:3] = chrrow[3:0];
  assign addr[2:0] = chrcol[2:0];

  assign pixel = (mux_ctrl == 0) ? pixel_lo[0] : pixel_hi[0];

   // RAMB16_S1: Virtex-II/II-Pro, Spartan-3 16kx1 Single-Port RAM
   // Xilinx HDL Language Template version 6.3.1i

   RAMB16_S1 character_rom_lo (
      .DO(pixel_lo),  // 1-bit Data Output
      .ADDR(addr),    // 14-bit Address Input
      .CLK(clk),      // Clock
      .DI(1'b0),      // 1-bit Data Input
      .EN(pixclk),    // RAM Enable Input
      .SSR(1'b0),     // Synchronous Set/Reset Input
      .WE(1'b0)       // Write Enable Input
   );

   // The following defparam declarations are only necessary if you wish to change the default behavior
   // of the RAM. If the instance name is changed, these defparams need to be updated accordingly.

   defparam character_rom_lo.INIT = 1'h0;  // Value of output RAM registers at startup
   defparam character_rom_lo.SRVAL = 1'h0; // Ouput value upon SSR assertion
   defparam character_rom_lo.WRITE_MODE = "WRITE_FIRST"; // WRITE_FIRST, READ_FIRST or NO_CHANGE

   // The following defparam INIT_xx declarations are only necessary if you wish to change the initial
   // contents of the RAM to anything other than all zero's.

   defparam character_rom_lo.INIT_00 = 256'h000000007E818199BD8181A5817E000000000000000000000000000000000000;
   defparam character_rom_lo.INIT_01 = 256'h00000000081C3E7F7F7F7F3600000000000000007EFFFFE7C3FFFFDBFF7E0000;
   defparam character_rom_lo.INIT_02 = 256'h000000003C1899E7E7E73C3C180000000000000000081C3E7F3E1C0800000000;
   defparam character_rom_lo.INIT_03 = 256'h000000000000183C3C18000000000000000000003C18187EFFFF7E3C18000000;
   defparam character_rom_lo.INIT_04 = 256'h00000000003C664242663C0000000000FFFFFFFFFFFFE7C3C3E7FFFFFFFFFFFF;
   defparam character_rom_lo.INIT_05 = 256'h000000001E333333331E4C5870780000FFFFFFFFFFC399BDBD99C3FFFFFFFFFF;
   defparam character_rom_lo.INIT_06 = 256'h00000000070F0E0C0C0C0CFCCCFC00000000000018187E183C666666663C0000;
   defparam character_rom_lo.INIT_07 = 256'h000000001818DB3CE73CDB18180000000000000367E7E6C6C6C6C6FEC6FE0000;
   defparam character_rom_lo.INIT_08 = 256'h00000000406070787C7F7C7870604000000000000103070F1F7F1F0F07030100;
   defparam character_rom_lo.INIT_09 = 256'h0000000066660066666666666666000000000000183C7E181818187E3C180000;
   defparam character_rom_lo.INIT_0A = 256'h0000003E63301C366363361C06633E0000000000D8D8D8D8D8DEDBDBDBFE0000;
   defparam character_rom_lo.INIT_0B = 256'h0000007E183C7E181818187E3C180000000000007F7F7F7F0000000000000000;
   defparam character_rom_lo.INIT_0C = 256'h00000000183C7E18181818181818000000000000181818181818187E3C180000;
   defparam character_rom_lo.INIT_0D = 256'h0000000000000C067F060C000000000000000000000018307F30180000000000;
   defparam character_rom_lo.INIT_0E = 256'h00000000000014367F361400000000000000000000007F030303030000000000;
   defparam character_rom_lo.INIT_0F = 256'h0000000000081C1C3E3E7F7F0000000000000000007F7F3E3E1C1C0800000000;
   defparam character_rom_lo.INIT_10 = 256'h000000001818001818183C3C3C18000000000000000000000000000000000000;
   defparam character_rom_lo.INIT_11 = 256'h0000000036367F3636367F363600000000000000000000000000002466666600;
   defparam character_rom_lo.INIT_12 = 256'h000000006163060C183063430000000000000018183E6361603E0343633E1818;
   defparam character_rom_lo.INIT_13 = 256'h0000000000000000000000060C0C0C00000000006E3333333B6E1C36361C0000;
   defparam character_rom_lo.INIT_14 = 256'h000000000C18303030303030180C00000000000030180C0C0C0C0C0C18300000;
   defparam character_rom_lo.INIT_15 = 256'h00000000000018187E18180000000000000000000000663CFF3C660000000000;
   defparam character_rom_lo.INIT_16 = 256'h00000000000000007F000000000000000000000C181818000000000000000000;
   defparam character_rom_lo.INIT_17 = 256'h000000000103060C183060400000000000000000181800000000000000000000;
   defparam character_rom_lo.INIT_18 = 256'h000000007E1818181818181E1C180000000000003E6363676B6B7363633E0000;
   defparam character_rom_lo.INIT_19 = 256'h000000003E636060603C6060633E0000000000007F6303060C183060633E0000;
   defparam character_rom_lo.INIT_1A = 256'h000000003E636060703F0303037F000000000000783030307F33363C38300000;
   defparam character_rom_lo.INIT_1B = 256'h000000000C0C0C0C18306060637F0000000000003E636363633F0303061C0000;
   defparam character_rom_lo.INIT_1C = 256'h000000001E306060607E6363633E0000000000003E636363633E6363633E0000;
   defparam character_rom_lo.INIT_1D = 256'h000000000C181800000018180000000000000000001818000000181800000000;
   defparam character_rom_lo.INIT_1E = 256'h0000000000007F00007F000000000000000000006030180C060C183060000000;
   defparam character_rom_lo.INIT_1F = 256'h000000001818001818183063633E000000000000060C18306030180C06000000;
   defparam character_rom_lo.INIT_20 = 256'h00000000636363637F6363361C080000000000003E033B7B7B7B63633E000000;
   defparam character_rom_lo.INIT_21 = 256'h000000003C66430303030343663C0000000000003F666666663E6666663F0000;
   defparam character_rom_lo.INIT_22 = 256'h000000007F664606161E1646667F0000000000001F36666666666666361F0000;
   defparam character_rom_lo.INIT_23 = 256'h000000005C6663637B030343663C0000000000000F060606161E1646667F0000;
   defparam character_rom_lo.INIT_24 = 256'h000000003C18181818181818183C00000000000063636363637F636363630000;
   defparam character_rom_lo.INIT_25 = 256'h00000000676666361E1E363666670000000000001E3333333030303030780000;
   defparam character_rom_lo.INIT_26 = 256'h0000000063636363636B7F7F77630000000000007F66460606060606060F0000;
   defparam character_rom_lo.INIT_27 = 256'h000000001C36636363636363361C00000000000063636363737B7F6F67630000;
   defparam character_rom_lo.INIT_28 = 256'h000070303E7B6B6363636363633E0000000000000F060606063E6666663F0000;
   defparam character_rom_lo.INIT_29 = 256'h000000003E636360301C0663633E00000000000067666666363E6666663F0000;
   defparam character_rom_lo.INIT_2A = 256'h000000003E6363636363636363630000000000003C1818181818185A7E7E0000;
   defparam character_rom_lo.INIT_2B = 256'h0000000036367F6B6B6363636363000000000000081C36636363636363630000;
   defparam character_rom_lo.INIT_2C = 256'h000000003C181818183C66666666000000000000636336361C1C363663630000;
   defparam character_rom_lo.INIT_2D = 256'h000000003C0C0C0C0C0C0C0C0C3C0000000000007F6343060C183061637F0000;
   defparam character_rom_lo.INIT_2E = 256'h000000003C30303030303030303C000000000000406070381C0E070301000000;
   defparam character_rom_lo.INIT_2F = 256'h0000FF0000000000000000000000000000000000000000000000000063361C08;
   defparam character_rom_lo.INIT_30 = 256'h000000006E3333333E301E000000000000000000000000000000000000180C0C;
   defparam character_rom_lo.INIT_31 = 256'h000000003E63030303633E0000000000000000003B66666666361E0606070000;
   defparam character_rom_lo.INIT_32 = 256'h000000003E6303037F633E0000000000000000006E33333333363C3030380000;
   defparam character_rom_lo.INIT_33 = 256'h001E33303E33333333336E0000000000000000000F060606060F0626361C0000;
   defparam character_rom_lo.INIT_34 = 256'h000000003C18181818181C00181800000000000067666666666E360606070000;
   defparam character_rom_lo.INIT_35 = 256'h000000006766361E1E36660606070000003C6666606060606060700060600000;
   defparam character_rom_lo.INIT_36 = 256'h000000006B6B6B6B6B7F370000000000000000003C18181818181818181C0000;
   defparam character_rom_lo.INIT_37 = 256'h000000003E63636363633E0000000000000000006666666666663B0000000000;
   defparam character_rom_lo.INIT_38 = 256'h007830303E33333333336E0000000000000F06063E66666666663B0000000000;
   defparam character_rom_lo.INIT_39 = 256'h000000003E63301C06633E0000000000000000000F060606466E3B0000000000;
   defparam character_rom_lo.INIT_3A = 256'h000000006E333333333333000000000000000000386C0C0C0C0C3F0C0C080000;
   defparam character_rom_lo.INIT_3B = 256'h00000000367F6B6B636363000000000000000000183C66666666660000000000;
   defparam character_rom_lo.INIT_3C = 256'h001F30607E63636363636300000000000000000063361C1C1C36630000000000;
   defparam character_rom_lo.INIT_3D = 256'h0000000070181818180E181818700000000000007F63060C18337F0000000000;
   defparam character_rom_lo.INIT_3E = 256'h000000000E18181818701818180E000000000000181818181800181818180000;
   defparam character_rom_lo.INIT_3F = 256'h00000000007F636363361C08000000000000000000000000000000003B6E0000;

   // RAMB16_S1: Virtex-II/II-Pro, Spartan-3 16kx1 Single-Port RAM
   // Xilinx HDL Language Template version 6.3.1i

   RAMB16_S1 character_rom_hi (
      .DO(pixel_hi),  // 1-bit Data Output
      .ADDR(addr),    // 14-bit Address Input
      .CLK(clk),      // Clock
      .DI(1'b0),      // 1-bit Data Input
      .EN(pixclk),    // RAM Enable Input
      .SSR(1'b0),     // Synchronous Set/Reset Input
      .WE(1'b0)       // Write Enable Input
   );

   // The following defparam declarations are only necessary if you wish to change the default behavior
   // of the RAM. If the instance name is changed, these defparams need to be updated accordingly.

   defparam character_rom_hi.INIT = 1'h0;  // Value of output RAM registers at startup
   defparam character_rom_hi.SRVAL = 1'h0; // Ouput value upon SSR assertion
   defparam character_rom_hi.WRITE_MODE = "WRITE_FIRST"; // WRITE_FIRST, READ_FIRST or NO_CHANGE

   // The following defparam INIT_xx declarations are only necessary if you wish to change the initial
   // contents of the RAM to anything other than all zero's.

   defparam character_rom_hi.INIT_00 = 256'h000000006E333333333333003333000000003E60303C664303030343663C0000;
   defparam character_rom_hi.INIT_01 = 256'h000000006E3333333E301E00361C0800000000003E6303037F633E000C183000;
   defparam character_rom_hi.INIT_02 = 256'h000000006E3333333E301E00180C0600000000006E3333333E301E0033330000;
   defparam character_rom_hi.INIT_03 = 256'h0000003C60303C660606663C00000000000000006E3333333E301E001C361C00;
   defparam character_rom_hi.INIT_04 = 256'h000000003E6303037F633E0063630000000000003E6303037F633E00361C0800;
   defparam character_rom_hi.INIT_05 = 256'h000000003C18181818181C0066660000000000003E6303037F633E00180C0600;
   defparam character_rom_hi.INIT_06 = 256'h000000003C18181818181C00180C0600000000003C18181818181C00663C1800;
   defparam character_rom_hi.INIT_07 = 256'h000000006363637F6363361C001C361C000000006363637F6363361C08636300;
   defparam character_rom_hi.INIT_08 = 256'h00000000761B1B7E6C6E330000000000000000007F6606063E06667F00060C18;
   defparam character_rom_hi.INIT_09 = 256'h000000003E63636363633E00361C08000000000073333333337F3333367C0000;
   defparam character_rom_hi.INIT_0A = 256'h000000003E63636363633E00180C0600000000003E63636363633E0063630000;
   defparam character_rom_hi.INIT_0B = 256'h000000006E33333333333300180C0600000000006E33333333333300331E0C00;
   defparam character_rom_hi.INIT_0C = 256'h000000001C3663636363361C00636300001E30607E6363636363630063630000;
   defparam character_rom_hi.INIT_0D = 256'h0000000018183C66060606663C181800000000003E6363636363636300636300;
   defparam character_rom_hi.INIT_0E = 256'h000000001818187E187E183C66660000000000003F67060606060F0626361C00;
   defparam character_rom_hi.INIT_0F = 256'h00000E1B18181818187E181818D8700000000000633333337B33231F33331F00;
   defparam character_rom_hi.INIT_10 = 256'h000000003C18181818181C000C183000000000006E3333333E301E00060C1800;
   defparam character_rom_hi.INIT_11 = 256'h000000006E33333333333300060C1800000000003E63636363633E00060C1800;
   defparam character_rom_hi.INIT_12 = 256'h00000000636363737B7F6F6763003B6E000000006666666666663B003B6E0000;
   defparam character_rom_hi.INIT_13 = 256'h0000000000000000003E001C36361C000000000000000000007E007C36363C00;
   defparam character_rom_hi.INIT_14 = 256'h0000000000030303037F000000000000000000003E636303060C0C000C0C0000;
   defparam character_rom_hi.INIT_15 = 256'h0000F83060C973060C183363430303000000000000606060607F000000000000;
   defparam character_rom_hi.INIT_16 = 256'h00000000183C3C3C18181800181800000000F060FC5973660C18336343030300;
   defparam character_rom_hi.INIT_17 = 256'h0000000000003366CC66330000000000000000000000CC663366CC0000000000;
   defparam character_rom_hi.INIT_18 = 256'h55AA55AA55AA55AA55AA55AA55AA55AA22882288228822882288228822882288;
   defparam character_rom_hi.INIT_19 = 256'h18181818181818181818181818181818EEBBEEBBEEBBEEBBEEBBEEBBEEBBEEBB;
   defparam character_rom_hi.INIT_1A = 256'h18181818181818181F181F181818181818181818181818181F18181818181818;
   defparam character_rom_hi.INIT_1B = 256'h6C6C6C6C6C6C6C6C7F000000000000006C6C6C6C6C6C6C6C6F6C6C6C6C6C6C6C;
   defparam character_rom_hi.INIT_1C = 256'h6C6C6C6C6C6C6C6C6F606F6C6C6C6C6C18181818181818181F181F0000000000;
   defparam character_rom_hi.INIT_1D = 256'h6C6C6C6C6C6C6C6C6F607F00000000006C6C6C6C6C6C6C6C6C6C6C6C6C6C6C6C;
   defparam character_rom_hi.INIT_1E = 256'h00000000000000007F6C6C6C6C6C6C6C00000000000000007F606F6C6C6C6C6C;
   defparam character_rom_hi.INIT_1F = 256'h18181818181818181F0000000000000000000000000000001F181F1818181818;
   defparam character_rom_hi.INIT_20 = 256'h0000000000000000FF181818181818180000000000000000F818181818181818;
   defparam character_rom_hi.INIT_21 = 256'h1818181818181818F8181818181818181818181818181818FF00000000000000;
   defparam character_rom_hi.INIT_22 = 256'h1818181818181818FF181818181818180000000000000000FF00000000000000;
   defparam character_rom_hi.INIT_23 = 256'h6C6C6C6C6C6C6C6CEC6C6C6C6C6C6C6C1818181818181818F818F81818181818;
   defparam character_rom_hi.INIT_24 = 256'h6C6C6C6C6C6C6C6CEC0CFC00000000000000000000000000FC0CEC6C6C6C6C6C;
   defparam character_rom_hi.INIT_25 = 256'h6C6C6C6C6C6C6C6CEF00FF00000000000000000000000000FF00EF6C6C6C6C6C;
   defparam character_rom_hi.INIT_26 = 256'h0000000000000000FF00FF00000000006C6C6C6C6C6C6C6CEC0CEC6C6C6C6C6C;
   defparam character_rom_hi.INIT_27 = 256'h0000000000000000FF00FF18181818186C6C6C6C6C6C6C6CEF00EF6C6C6C6C6C;
   defparam character_rom_hi.INIT_28 = 256'h1818181818181818FF00FF00000000000000000000000000FF6C6C6C6C6C6C6C;
   defparam character_rom_hi.INIT_29 = 256'h0000000000000000FC6C6C6C6C6C6C6C6C6C6C6C6C6C6C6CFF00000000000000;
   defparam character_rom_hi.INIT_2A = 256'h1818181818181818F818F800000000000000000000000000F818F81818181818;
   defparam character_rom_hi.INIT_2B = 256'h6C6C6C6C6C6C6C6CFF6C6C6C6C6C6C6C6C6C6C6C6C6C6C6CFC00000000000000;
   defparam character_rom_hi.INIT_2C = 256'h00000000000000001F181818181818181818181818181818FF18FF1818181818;
   defparam character_rom_hi.INIT_2D = 256'hFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF1818181818181818F800000000000000;
   defparam character_rom_hi.INIT_2E = 256'h0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0FFFFFFFFFFFFFFFFFFF00000000000000;
   defparam character_rom_hi.INIT_2F = 256'h000000000000000000FFFFFFFFFFFFFFF0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0;
   defparam character_rom_hi.INIT_30 = 256'h00000303033F63633F633F0000000000000000006E3B1B1B1B3B6E0000000000;
   defparam character_rom_hi.INIT_31 = 256'h000000003636363636367F0100000000000000000303030303030363637F0000;
   defparam character_rom_hi.INIT_32 = 256'h000000000E1B1B1B1B1B7E0000000000000000007F63060C180C06637F000000;
   defparam character_rom_hi.INIT_33 = 256'h000000001818181818183B6E000000000000000306063E666666666600000000;
   defparam character_rom_hi.INIT_34 = 256'h000000001C3663637F6363361C000000000000007E183C6666663C187E000000;
   defparam character_rom_hi.INIT_35 = 256'h000000003C666666667C30180C780000000000007736363636636363361C0000;
   defparam character_rom_hi.INIT_36 = 256'h0000000003067ECFDBF37E60C00000000000000000007EDBDBDB7E0000000000;
   defparam character_rom_hi.INIT_37 = 256'h0000000063636363636363633E00000000000000380C0606063E06060C380000;
   defparam character_rom_hi.INIT_38 = 256'h00000000FF000018187E18180000000000000000007F00007F00007F00000000;
   defparam character_rom_hi.INIT_39 = 256'h000000007E0030180C060C1830000000000000007E000C18306030180C000000;
   defparam character_rom_hi.INIT_3A = 256'h000000000E1B1B1B18181818181818181818181818181818181818D8D8700000;
   defparam character_rom_hi.INIT_3B = 256'h0000000000003B6E003B6E000000000000000000001818007E00181800000000;
   defparam character_rom_hi.INIT_3C = 256'h0000000000000018180000000000000000000000000000000000001C36361C00;
   defparam character_rom_hi.INIT_3D = 256'h00000000383C3636373030303030F00000000000000000180000000000000000;
   defparam character_rom_hi.INIT_3E = 256'h0000000000000000001F13060C190E0000000000000000000036363636361B00;
   defparam character_rom_hi.INIT_3F = 256'h0000000000000000000000000000000000000000003E3E3E3E3E3E3E00000000;

  always @(posedge clk) begin
    if (pixclk == 1) begin
      attcode_out[7:0] <= attcode_in[7:0];
      blank_out <= blank_in;
      hsync_out <= hsync_in;
      vsync_out <= vsync_in;
      blink_out <= blink_in;
      mux_ctrl <= chrcode[7];
    end
  end

endmodule
