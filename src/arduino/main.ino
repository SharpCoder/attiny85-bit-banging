#include "Comm1W.h"

void setup() {
  // put your setup code here, to run once:
  commInit();

  // For testing, set a random number.
  commSet(170);
}

void loop() {
  // put your main code here, to run repeatedly:
  commLoop();
}
