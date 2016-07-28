
int ipow(int x, int y) {
	int result = 1;
	
	// special case
	if (y < 0) {
		return 0;
	}
	
	// normal cases
	while (y > 0) {
		result *= x;
		y--;
	}
	return result;
	
}
