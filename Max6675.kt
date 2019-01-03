import android.util.Log
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.SpiDevice
import java.io.IOException

class Max6675 constructor(deviceName: String) {

    private val device: SpiDevice = PeripheralManager.getInstance().openSpiDevice(deviceName)
    private val TAG: String = "MAX6675"

    init {
        configureSpiDevice()
    }

    @Throws(IOException::class)
    fun configureSpiDevice() {
        device.apply {
            setMode(SpiDevice.MODE0)
            setFrequency(5000000)
            setBitsPerWord(8);
            setBitJustification(SpiDevice.BIT_JUSTIFICATION_MSB_FIRST)
        }
    }

    @Throws(IOException::class)
    fun closeSpiDevice(): Boolean {
        try {
            device.close()
        } catch (ex: IOException) {
            return false
        }
        return true
    }

    @Throws(IOException::class)
    private fun read16Bits(): Int {
        /** Reads 16 bits from the SPI device. **/

        val buffer = ByteArray(2)
        this.device.read(buffer, buffer.size)
        val byte1 = (buffer[0].toInt() shl 8)
        val byte2 = buffer[1].toInt()
        return byte1 or byte2
    }

    @Throws(IOException::class)
    fun readTempInCelcius(): Double {
        /** Return the thermocouple temperature value in degrees celsius. */

        var dataFromDevice: Int = read16Bits()

        // Check for error reading value.
        val errorCheckAnd = dataFromDevice and (0x4).toInt()
        if (errorCheckAnd > 0) {
            return Double.NaN
        }

        // We only need the 12 MSBs.
        var data12MSBs = dataFromDevice shr 3

        // We need to check if the signed bit is set.
        val negativeAnd = dataFromDevice and (0x80000000).toInt()
        if (negativeAnd > 0) {
            data12MSBs -= 4096
        }

        // Scale by 0.25 degrees C per bit and return value.
        return data12MSBs.toDouble() * 0.25
    }

    fun getMeanTempInCelcius(numReadings: Int = 10): Double {
        var totalTemp = 0.0
        var i = 1
        while(i++ <= numReadings) {
            totalTemp += readTempInCelcius()
            Thread.sleep(1000)
        }
        return totalTemp / numReadings
    }

    companion object {
        fun celciusToFahrenheit(tempInCelcius: Double): Double {
            return ( tempInCelcius * ( 9.0/5.0 ) ) + 32
        }
        fun fahrenheitToCelcius(tempInFahrenheit: Double): Double {
            return ( tempInFahrenheit - 32 ) * ( 5.0/9.0 )
        }
    }
}

