#include <SPI.h>
#include <Ethernet.h>
#include "library/SimpleTimer/SimpleTimer.h"
#include "library/PinChangeInterrupt/src/PinChangeInterrupt.h"
#include "library/TinkerKit-master/TinkerKit.h"

#define CO2_PIN 5 // CO2 sensor should be attached to pin 5
#define MOTION_PIN 6 // Motion sensor should be attached to pin 6

volatile uint16_t cur_co2_concentration;
volatile uint16_t prev_co2_concentration;
volatile bool co2_changed;
volatile uint32_t start_millis;
volatile bool motion_detected;
volatile bool motion_detected_cloud;
volatile uint8_t cloud_interval_counter;
volatile bool cloud_update;
volatile uint32_t test;
SimpleTimer timer;
EthernetClient client;
TKLightSensor ldr(I1);  //create the "ldr" object on port I1

#define BUF_SIZE 256
#define PORT 80
byte mac[] = { 0x90, 0xA2, 0xDA, 0x0D, 0x75, 0xFD }; // This is the real mac of one of the arduino ethernet boards
IPAddress ip(192, 168, 1, 100); //fallback if DHCP doesnt work
const char server[] = "tomato-1230.herokuapp.com";

/*
 * Interrupt handler which is triggered by rising edge of CO2 sensor's PWM signal.
 * Function is used for co2 concentration calculation in co-operation with on_falling_edge.
 */
void on_rising_edge(void)
{
  attachPinChangeInterrupt(digitalPinToPinChangeInterrupt(CO2_PIN), on_falling_edge, FALLING);
  test = start_millis;
  start_millis = millis();
  //Serial.print("full length: "); // should be 1004ms
  //Serial.println(start_millis - test);
}

/*
 * Interrupt handler which is triggered by falling edge of CO2 sensor's PWM signal.
 * Function is used for co2 concentration calculation in co-operation with on_rising_edge.
 */
void on_falling_edge(void)
{
  attachPinChangeInterrupt(digitalPinToPinChangeInterrupt(CO2_PIN), on_rising_edge, RISING);
  prev_co2_concentration = cur_co2_concentration;
  uint32_t cur_millis = millis();

  if (cur_millis < start_millis) //millis() will go around at some point
    return; // We don't really need to take care of this as we only send data once in five minutes, so one sample doesn't really matter, eventhought it would be easy to handle
  else
    cur_co2_concentration = cur_millis - start_millis - 2; // 2.0ms (@ 0ppm), 1002ms (@ 5000ppm)

  if (cur_co2_concentration != prev_co2_concentration)
    co2_changed = true;
}

/*
 * Interrupt handler which is triggered by motion sensor when motion is detected.
 */
void motion_interrupt_handler(void)
{
  motion_detected = true;
  motion_detected_cloud = true;
}

/*
 * "Interrupt" handler which is triggered by timer (Simpletimer) once in a minute.
 */
void cloud_timer_handler(void)
{
  cloud_update = true;
  cloud_interval_counter++;
}

/*
 * Function to send sensor data to cloud.
 */
void send_sensor_data_to_cloud(const char *method, const char *path, const char *data)
{
  char buf[BUF_SIZE];
  client.stop(); //just to make sure that there is no previous connection

  // connect to server, construct and send the sensor data
  if (client.connect(server, PORT)) {
    snprintf(buf, BUF_SIZE, "%s %s HTTP/1.1", method, path);
    client.println(buf);
    snprintf(buf, BUF_SIZE, "Host: %s", server);
    client.println(buf);
    client.println(F("Connection: close\r\nContent-Type: application/x-www-form-urlencoded; charset=UTF-8"));
    snprintf(buf, BUF_SIZE, "Content-Length: %u\r\n", strlen(data));
    client.println(buf);
    client.print(data);

    while (client.connected()) {
      delay(1000); // this is to make sure that we don't close connection before we have received the response
      while (client.available())
        Serial.write(client.read()); //print response to serial

      client.stop();
    }
  } else {
    Serial.println("Failed to create connection to server");
  }
}

void setup()
{
  pinMode(CO2_PIN, INPUT); //INPUT no internal pullup
  pinMode(MOTION_PIN, INPUT); //no internal pullup
  Serial.begin(9600);
  Serial.println("Setting up everything");
  attachPinChangeInterrupt(digitalPinToPinChangeInterrupt(MOTION_PIN), motion_interrupt_handler, RISING);
  attachPinChangeInterrupt(digitalPinToPinChangeInterrupt(CO2_PIN), on_rising_edge, RISING);
  timer.setInterval(60000, cloud_timer_handler);

  if (Ethernet.begin(mac) == 0) { //if DHCP fails use static ip
    Serial.println(F("Failed to configure Ethernet using DHCP"));
    Ethernet.begin(mac, ip);
  }
  // print the Ethernet board/shield's IP address:
  Serial.print("IP address: ");
  Serial.println(Ethernet.localIP());
}

void loop() {

  int brightnessVal = ldr.read();  

  timer.run();
  if (co2_changed) {
    co2_changed = false;
    Serial.print("CO2 concentration: ");
    Serial.print(cur_co2_concentration); //2.0ms (@ 0ppm), 1002ms (@ 5000ppm)
    Serial.println("ppm");
  }

  if (motion_detected) {
    motion_detected = false;
    Serial.println("motion detected");
  }

  if (cloud_update) {
    cloud_update = false;
    Ethernet.maintain(); //renew DHCP lease

    char params[BUF_SIZE];
    snprintf(params, BUF_SIZE, "available=%s&name=%s&size=%u&organization=%s&location=%s", motion_detected_cloud ? "false" : "true", "Tomato", 10, "Aalto", "CS Building");
    send_sensor_data_to_cloud("PUT", "/api/v1/rooms/1/", params);
    motion_detected_cloud = false;
    Serial.println("sensor data sent to cloud"); //send motion sensor data to cloud once in minute

    if (cloud_interval_counter % 5 == 0) { // send other sensor data once in 5 minutes
      Serial.println("Send other sensor data to cloud");
    }
  }
}
