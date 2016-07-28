
static unsigned int autoSeeder;
static unsigned int currentNumber;

void randomAutoSeederTick (void)
{
	autoSeeder++;
}

void autoSeedRandom (void)
{
	currentNumber = autoSeeder;
}

void seedRandom (unsigned int seed)
{
	currentNumber = seed;
}

unsigned int getRandom (void)
{
	currentNumber = currentNumber * 1664525 + 1013904223;
	return currentNumber;
}
