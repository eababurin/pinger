package ru.eababurin.pinger

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val listOfOutput = mutableListOf<String>()
    val mutableOutput = MutableLiveData<List<String>>()
    val pingError = MutableLiveData<Int>()
    val isExecute = MutableLiveData(false)
    val isEmptyOutput = MutableLiveData(true)

    val requestHostname = MutableLiveData<String>()
    val requestCount = MutableLiveData<String>()
    val requestInterval = MutableLiveData<String>()
    val outputSeparator = application.getString(R.string.output_separator)

    fun ping(hostname: String, counts: String, interval: String) {
        Thread {
            if (listOfOutput.isNotEmpty())
                listOfOutput.add((outputSeparator))

            val inputCommand = mutableListOf<String>()

            if (counts == "∞") inputCommand.addAll(listOf("ping", "-i", interval, hostname))
            else inputCommand.addAll(listOf("ping", "-c", counts, "-i", interval, hostname))

            val process = ProcessBuilder().command(inputCommand).start()
            val stdOutput = process.inputStream.bufferedReader()
            val stdErrOutput = process.errorStream.bufferedReader()

            try {
                while (true) {
                    if (!isExecute.value!!) break

                    val stdLine = stdOutput.readLine()

                    if (stdLine != null) {
                        listOfOutput.add(stdLine)
                        mutableOutput.postValue(listOfOutput)
                        isEmptyOutput.postValue(false)
                        if (stdLine.contains("rtt")) {
                            isExecute.postValue(false)
                        }
                    } else {
                        val errLine = stdErrOutput.readLine()
                        if (errLine != null) {
                            listOfOutput.add(errLine)
                            mutableOutput.postValue(listOfOutput)
                            isEmptyOutput.postValue(false)
                            isExecute.postValue(false)
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                pingError.postValue(R.string.unknown_error)
            } finally {
                process.destroy()
            }
            isExecute.postValue(false)
        }.start()
    }
}