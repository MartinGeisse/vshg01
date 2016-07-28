cd "$5/run"
"$5/devtools/mkdisk" disk.img 40M
"$5/devtools/mkpart" disk.img disk.part
"$5/devtools/mkfs" disk.img 0 root.fsys
"$5/devtools/fsck" disk.img 0
"$5/devtools/mkfs" disk.img 2 usr.fsys
"$5/devtools/fsck" disk.img 2
