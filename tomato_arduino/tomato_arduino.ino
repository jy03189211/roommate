#include <SPI.h>
#include <Ethernet.h>
#include "library/SimpleTimer/SimpleTimer.h"
#include "library/PinChangeInterrupt/src/PinChangeInterrupt.h"
//#include "library/TinkerKit-master/TinkerKit.h"
#include "library/DHT_sensor_library/DHT.h"

#define CO2_PIN 5 // CO2 sensor should be attached to pin 5
#define MOTION_PIN 6 // Motion sensor should be attached to pin 6
#define DHT_PIN 7 // Temperature & Humidity sensor should be attached to pin 7
#define SOUND_DPIN 8
#define SOUND_APIN A0

#define DHT_TYPE DHT22

#define API_VERSION "1"
#define ROOM_ID "1"
#define SENSOR_ID_MOTION "1"
#define SENSOR_ID_CO2 "2"
#define SENSOR_ID_TEMPERATURE "3"
#define SENSOR_ID_HUMIDITY "4"

volatile double cur_co2_concentration;
volatile double prev_co2_concentration;
volatile bool co2_changed;
volatile uint32_t co2_sum;
volatile uint16_t co2_count;

volatile uint32_t start_micro;
volatile bool motion_detected;
volatile bool motion_detected_cloud;

volatile uint8_t cloud_interval_counter;
volatile bool cloud_update;

uint16_t prev_temperature;
uint16_t cur_temperature;
int32_t temperature_sum;
int16_t temperature_count;

uint8_t cur_humidity;
uint8_t prev_humidity;
uint32_t humidity_sum;
uint16_t humidity_count;

volatile bool sound_detected;
uint16_t sound_cur_value;
uint16_t sound_prev_value;

SimpleTimer timer;
EthernetClient client;
//TKLightSensor ldr(I1);  //create the "ldr" object on port I1
DHT dht(DHT_PIN, DHT_TYPE);

#define BUF_SIZE 256
#define PORT 80
byte mac[] = { 0x90, 0xA2, 0xDA, 0x0D, 0x75, 0xFD }; // This is the real mac of one of the arduino ethernet boards
IPAddress ip(192, 168, 1, 108); //fallback if DHCP doesnt work
const char protocol[] = "http://";
const char server[] = "tomato-1230.herokuapp.com";
const char api_url[] = "/api/v" API_VERSION "/";
const char sensor_url[] = "sensors/";
const char motion_url[] = SENSOR_ID_MOTION "/";
const char co2_url[] = SENSOR_ID_CO2 "/";
const char temperature_url[] = SENSOR_ID_TEMPERATURE "/";
const char humidity_url[] = SENSOR_ID_HUMIDITY "/";
const char post[] = "POST";
const char room_url[] = "rooms/" ROOM_ID "/";
const char motion[] = "motion/";
const char co2[] = "co2/";
const char temperature[] = "temperature/";
const char humidity[] = "humidity/";

/*
   Interrupt handler which is triggered by rising edge of CO2 sensor's PWM signal.
   Function is used for co2 concentration calculation in co-operation with on_falling_edge.
*/
void on_rising_edge(void)
{
  attachPinChangeInterrupt(digitalPinToPinChangeInterrupt(CO2_PIN), on_falling_edge, FALLING);
  start_micro = micros();
}

/*
   Interrupt handler which is triggered by falling edge of CO2 sensor's PWM signal.
   Function is used for co2 concentration calculation in co-operation with on_rising_edge.
*/
void on_falling_edge(void)
{
  prev_co2_concentration = cur_co2_concentration;
  
  
  attachPinChangeInterrupt(digitalPinToPinChangeInterrupt(CO2_PIN), on_rising_edge, RISING);
  uint32_t cur_micro = micros();
  
  if (cur_micro < start_micro) //micro() will go around at some point
    return; // We don't really need to take care of this as we only send data once in five minutes, so one sample doesn't really matter, eventhought it would be easy to handle
  else
    cur_co2_concentration = cur_micro - start_micro; // 2.0ms (@ 0ppm), 1002ms (@ 5000ppm)
    Serial.print("CO2 concentration: ");
    Serial.print(cur_co2_concentration/400); //2.0ms (@ 0ppm), 1002ms (@ 5000ppm)
    Serial.println("ppm");
    
  co2_sum += cur_co2_concentration;
  co2_count++;
  if (cur_co2_concentration != prev_co2_concentration)
    co2_changed = true;
}

/*
   Interrupt handler which is triggered by motion sensor when motion is detected.
*/
void motion_interrupt_handler(void)
{
  motion_detected = true;
  motion_detected_cloud = true;
}

/*
   Interrupt handler which is triggered by microphone sensor when threshold has been exceeded.
*/
void sound_interrupt_handler(void)
{
  sound_detected = true;
}

/*
   "Interrupt" handler which is triggered by timer (Simpletimer) once in a minute.
*/
void cloud_timer_handler(void)
{
  cloud_update = true;
  cloud_interval_counter++;
}

/*
   "Interrupt" handler which is triggered by timer (Simpletimer) once in 2 seconds to fetch temperature & humidity.
   Our temperature & humidity sensor is so slow that we get new result only once in 2 seconds
*/
void temperature_humidity_timer_handler(void)
{
  cur_temperature = dht.readTemperature();
  cur_humidity = dht.readHumidity();

  temperature_sum += cur_temperature;
  temperature_count++;
  humidity_sum += cur_humidity;
  humidity_count++;
}

/*
   Function to send sensor data to cloud.
*/
/*
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
*/
void setup()
{
  pinMode(CO2_PIN, INPUT); // no internal pullup
  pinMode(MOTION_PIN, INPUT); // no internal pullup
  pinMode(SOUND_DPIN, INPUT); // no internal pullup
  dht.begin();
  Serial.begin(115200);
  Serial.println("Setting up everything");
  attachPinChangeInterrupt(digitalPinToPinChangeInterrupt(MOTION_PIN), motion_interrupt_handler, RISING);
  attachPinChangeInterrupt(digitalPinToPinChangeInterrupt(SOUND_DPIN), sound_interrupt_handler, RISING);
  attachPinChangeInterrupt(digitalPinToPinChangeInterrupt(CO2_PIN), on_rising_edge, RISING);
  timer.setInterval(60000, cloud_timer_handler);
  timer.setInterval(2000, temperature_humidity_timer_handler);

  if (Ethernet.begin(mac) == 0) { //if DHCP fails use static ip
    Serial.println(F("Failed to configure Ethernet using DHCP"));
    Ethernet.begin(mac, ip);
  }
  Serial.print("IP address: ");
  Serial.println(Ethernet.localIP());
  delay(2000); // 2s delay to get real data from sensors at first run already
}

void loop() {
//  int brightnessVal = ldr.read(); //this is actually not used at all
  
  timer.run();

  sound_prev_value = sound_cur_value;
  sound_cur_value = analogRead(SOUND_APIN);
  
  if (sound_cur_value != sound_prev_value) {
    //Serial.print("Sound level: ");
    //Serial.println(sound_cur_value);
  }

  if (cur_temperature != prev_temperature) {
    prev_temperature = cur_temperature;
    Serial.print("temperature: ");
    Serial.print(cur_temperature);
    Serial.println("*C");
  }

  if (cur_humidity != prev_humidity) {
    prev_humidity = cur_humidity;
    Serial.print("humidity: ");
    Serial.print(cur_humidity);
    Serial.println("%");
  }

  if (co2_changed) {
    co2_changed = false;
    
  }

  if (motion_detected) {
    motion_detected = false;
    Serial.println("motion detected");
  }

  if (sound_detected) {
    sound_detected = false;
    Serial.println("sound detected");
  }

  if (cloud_update) {
    cloud_update = false;

    char params[BUF_SIZE];
    char sensor[32];

    snprintf(params, BUF_SIZE, "detected=%s&sensor=%s%s%s%s%s", motion_detected_cloud ? "true" : "false", protocol, server, api_url, sensor_url, motion_url);
    snprintf(sensor, 32, "%s%s%s", api_url, room_url, motion);
    //send_sensor_data_to_cloud(post, sensor, params);

    motion_detected_cloud = false;
    //Serial.println("sensor data sent to cloud"); //send motion sensor data to cloud once in minute

    if (cloud_interval_counter % 5 == 0) { // send other sensor data once in 5 minutes
      //there is bug somewhere as for some reason we need six %s eventhought I have only five string parameters
      snprintf(params, BUF_SIZE, "concentration=%u&sensor=%s%s%s%s%s%s", co2_sum / co2_count, protocol, server, api_url, sensor_url, co2_url);
      Serial.println(params);
      snprintf(sensor, 32, "%s%s%s", api_url, room_url, co2);
      //send_sensor_data_to_cloud(post, sensor, params);
      co2_sum = co2_count = 0;

      snprintf(params, BUF_SIZE, "temperature=%i&sensor=%s%s%s%s%s%s", temperature_sum / temperature_count, protocol, server, api_url, sensor_url, temperature_url);
      snprintf(sensor, 32, "%s%s%s", api_url, room_url, temperature);
      //send_sensor_data_to_cloud(post, sensor, params);
      temperature_sum = temperature_count = 0;

      snprintf(params, BUF_SIZE, "humidity=%u&sensor=%s%s%s%s%s%s", humidity_sum / humidity_count, protocol, server, api_url, sensor_url, humidity_url);
      snprintf(sensor, 32, "%s%s%s", api_url, room_url, humidity);
      //send_sensor_data_to_cloud(post, sensor, params);
      humidity_sum = humidity_count = 0;

      Serial.println("Other sensor data sent to cloud");
      Ethernet.maintain(); //renew DHCP lease
    }
  }
}

