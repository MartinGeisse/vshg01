
# remove old stuff
rm -rf ngofiles
rm eco32.ngd
rm eco32.ncd
rm eco32.pcf
rm eco32par.ncd
rm eco32.bit

# create folders
mkdir ngofiles

# implement
ngdbuild -p xc3s1000-ft256-4 -dd ngofiles -nt on -uc ../src/original/eco32.ucf eco32 eco32.ngd
map -p xc3s1000-ft256-4 -o eco32.ncd eco32.ngd
par eco32.ncd eco32par.ncd eco32.pcf
bitgen -f eco32.ut eco32par.ncd eco32.bit eco32.pcf

