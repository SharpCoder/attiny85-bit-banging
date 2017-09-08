
#ifndef COMM_1_WIRE
#define COMM_1_WIRE

// NOTE: The following MUST be defined in the main program
// for this library to work.
void commInit();
void commSet(short value);
void commLoop();

#endif
