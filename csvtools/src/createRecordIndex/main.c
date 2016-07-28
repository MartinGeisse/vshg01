/**
 * createRecordIndex: Given a CSV file, this program finds line breaks and
 * creates an index file that contains the position of each line break, starting
 * with 0 for the first entry and ending with an entry for the end-of-file.
 */
#include "../common/index.h"

/**
 * The actual main method.
 */
int actualMain(int argc, char *argv[]) {
	return 0;
}

/**
 * Main function. Just wraps actualMain() since the compiler does not properly
 * check for missing return statements in main() according to c99 standard.
 */
int main(int argc, char *argv[]) {
	return actualMain(argc, argv);
}
