#include "Arduino.h"
#include "CommConfig.h"
#include "Comm1W.h"

// Define all the variables we need to handle asynchronous state
boolean comm_sending_sync = false;
boolean comm_sending_sync_1 = false;
boolean comm_sending_sync_2 = false;

// Define all of the other variables we need to support the protocol
char comm_index = 0;
short comm_value = 0;
short comm_value_next = 0;
unsigned long comm_current_millis = 0;
unsigned long comm_target_millis = 0;

// Entry point to initialize the PIN we need.
void commInit() {
  pinMode(COMM_WIRE, OUTPUT);
}

// This method uses the yielding method of pause to allow the rest of the device
// time to process as much as it wants.
boolean comm_can_execute() {
  if (comm_target_millis > comm_current_millis) {
    return false;
  }
  return true;
}

// Update the target delay
void yield_delay(unsigned long ms)
{
  comm_target_millis = comm_current_millis + (ms * 1);
}

// Set the value to send.
void commSet(short number) {
  comm_value_next = number;
}

void commLoop() {
  comm_current_millis = millis();
  // return if we need to delay longer.
  if (comm_can_execute()) {
    if (!comm_sending_sync) {
      if ((comm_value >> comm_index) & 0x01 == 1) {
        // High value
        digitalWrite(COMM_WIRE, true);
      } else {
        // Low value
        digitalWrite(COMM_WIRE, false);
      }
      yield_delay(COMM_TIMING);
      comm_index++;

      if (comm_index >= 10) {
        comm_index = 0;
        digitalWrite(COMM_WIRE, true);
        comm_sending_sync_1 = true;
        comm_sending_sync_2 = false;
      }
    } else {
      if (comm_sending_sync_1) {

        // Now that we've completed one full transmission, update the value
        // to be whatever we want to send next.
        comm_value = comm_value_next;

        digitalWrite(COMM_WIRE, false);
        yield_delay(COMM_TIMING * 1);
        comm_sending_sync_1 = false;
        comm_sending_sync_2 = true;
       } else {
        digitalWrite(COMM_WIRE, true);
        yield_delay(COMM_TIMING * 10);
        comm_sending_sync = false;
        comm_sending_sync_1 = false;
        comm_sending_sync_2 = false;
      }
    }
  }
}
