
#include <Ethernet.h>
#include <SPI.h>
#include "RestClient.h"

RestClient client = RestClient("tomato-1230.herokuapp.com");

//Setup
void setup() {
  Serial.begin(9600);
  // Connect via DHCP
  Serial.println("connect to network");
  client.dhcp();
/*
  // Can still fall back to manual config:
  byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
  //the IP address for the shield:
  byte ip[] = { 192, 168, 2, 11 };
  Ethernet.begin(mac,ip);
*/
  Serial.println("Setup!");
}

String response;
void loop(){
  response = "";
  char[] body="{/"room/":/"123/",/"motion_detected/":1,/"time/":/"2016-03-10T01:44:32.553210Z/"}"
  int statusCode = client.post("/api/v1/motion/",body , &response);
  Serial.print("Status code from server: ");
  Serial.println(statusCode);
  delay(1000);
}
