**MAX6675-AndroidThings**
===================

What it is
-------------

MAX6675 is a Cold-Junction Compensated Thermocouple-to-Digital Converter.

Please see the official page for the sensor:  [MAX6675
Cold-Junction-Compensated K-Thermocouple-to-Digital Converter (0°C to +1024°C)](https://www.maximintegrated.com/en/products/sensors/MAX6675.html "")

This project provides a Kotlin module to integrate your MAX6675 sensor device via SPI protocol and read the temperature in °C from it. Inspired from [Adafruit MAX6675-library for Arduino](https://github.com/adafruit/MAX6675-library) and [MAX6675 by Troy Dack](https://github.com/tdack/MAX6675).

Since it's written in Kotlin, it is interoperable with Java as well. See: [Calling Kotlin from Java - Interoperability](https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html) for more details on how to use this module in Java code.


Usage
-------------------

```kotlin
// Find the SPI device name for your MAX6675 device.
// For more details on how to do that, see: https://developer.android.com/things/sdk/pio/spi#manage-connection
val SPI_DEVICE_NAME="SPI3.0"

// Initialize and configure the device.
val max6675 = Max6675(SPI_DEVICE_NAME)
max6675.configureSpiDevice()

// Read and print the temperature every second.
while(true) {
    val result = max6675.readTempInCelcius()
    Log.i(TAG, "Result: $result")
    Thread.sleep(1000)
}
```
