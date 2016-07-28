rm -rf bin
mkdir bin
gcc -std=c99 -Wall -pedantic -o bin/createRecordIndex src/common/index.c src/createRecordIndex/main.c
gcc -std=c99 -Wall -pedantic -o bin/createRecordSorting src/common/index.c src/createRecordSorting/main.c
gcc -std=c99 -Wall -pedantic -o bin/reorderRecords src/common/index.c src/reorderRecords/main.c
