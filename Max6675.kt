import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.SpiDevice
import java.io.IOException

class Max6675 constructor(deviceName: String) {

    private val device: SpiDevice = PeripheralManager.getInstance().openSpiDevice(deviceName)

    @Throws(IOException::class)
    fun configureSpiDevice() {
        this.device.apply {
            setMode(SpiDevice.MODE0)
            setFrequency(4300000)
            setBitsPerWord(8);
            setBitJustification(SpiDevice.BIT_JUSTIFICATION_MSB_FIRST)
        }
    }

    @Throws(IOException::class)
    private fun read16Bits(): Int {
        /** Reads 16 bits from the SPI device. **/

        val buffer = ByteArray(2)
        this.device.read(buffer, buffer.size)
        return (buffer[0].toInt() shl 8) or buffer[1].toInt()
    }

    @Throws(IOException::class)
    fun readTempInCelcius(): Double {
        /** Return the thermocouple temperature value in degrees celsius. */

        var dataFromDevice: Int = read16Bits()

        // Check for error reading value.
        val andResult = dataFromDevice and (0x4).toInt()
        if (andResult > 0) {
            return Double.NaN
        }

        // We only need the 12 MSBs.
        val data12MSBs = dataFromDevice shr 3

        // Scale by 0.25 degrees C per bit and return value.
        return data12MSBs.toDouble() * 0.25
    }
}

