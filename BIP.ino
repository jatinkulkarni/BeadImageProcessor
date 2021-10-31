#include "Stepper.h"

// These steppers must have a step of .9 degrees!

const int stepsPerRevolution = 400;

Stepper colorPicker(stepsPerRevolution, 22, 23, 2, 3);
Stepper dropper(stepsPerRevolution, 24, 25, 4, 5);

byte numRows = 0;
char curLoc = 'A';
byte numOfBuckets = 20;

byte stepsForDrop = 20;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  colorPicker.setSpeed(30);
  dropper.setSpeed(30);

  // NOTE: This assumes bead bucket is aligned on the first bucket ! ('A')
}

void loop() {
  if(Serial.available() > 0) {
    // Assure all the data has made it to the buffer
    delay(5);
    
    String beadCol = Serial.readString();
    
    for(int i = 0; i < beadCol.length(); i++) {
      // Align the correct bucket
      byte change = curLoc - beadCol.charAt(i);
      colorPicker.step(change * stepsPerRevolution / numOfBuckets);
      curLoc = beadCol.charAt(i);

      delay(50);
      
      // Drop a bead
      dropper.step(stepsForDrop);

      delay(50);
    }
  }

  // Saves resources
  delay(50);
}
