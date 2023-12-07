package com.bignerdranch.android.currencyconverter

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bignerdranch.android.currencyconverter.R.id
import com.bignerdranch.android.currencyconverter.R.layout
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.math.BigDecimal
import java.math.RoundingMode


class MainActivity : AppCompatActivity() {

    private lateinit var convertFromDropdown: TextView
    private lateinit var convertToDropdown: TextView
    private lateinit var conversionRate: TextView
    private lateinit var resultTextView: TextView
    private lateinit var amountToConvert: EditText
    private lateinit var convertButton: Button
    private lateinit var fromDialog: Dialog
    private lateinit var toDialog: Dialog
    private var arrayList: ArrayList<String> = ArrayList()
    private lateinit var convertFrom: String
    private lateinit var convertTo: String
    private lateinit var conversionRateValue: String
    private var country: Array<String> = arrayOf("AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK", "EUR", "GBP", "HKD", "HRK", "HUF", "IDR", "ILS", "INR", "ISK", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD", "PHP", "PLN", "RON", "RUB", "SEK", "SGD", "THB", "TRY", "USD", "ZAR")

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        convertFromDropdown = findViewById(id.convertFromMenu)
        convertToDropdown = findViewById(id.convertToMenu)
        convertButton = findViewById(id.convertButton)
        conversionRate = findViewById(id.conversionRateText)
        resultTextView = findViewById(id.conversionResult)
        amountToConvert = findViewById(id.amountToConvertEditText)

        //This might be an issue 16 minutes
        arrayList = ArrayList(country.asList())

        convertFromDropdown.setOnClickListener {
            fromDialog = Dialog(this@MainActivity)
            fromDialog.setContentView(layout.from_spinner)
            fromDialog.window?.setLayout(650, 800)
            fromDialog.show()

            val edit: EditText = fromDialog.findViewById(id.edit_text)
            val list: ListView = fromDialog.findViewById(id.list_view)

            val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, arrayList)
            list.adapter = adapter

            edit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Not implemented
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    adapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable?) {
                    // Not implemented
                }
            })
            list.setOnItemClickListener { parent, view, position, id ->
                convertFromDropdown.text = adapter.getItem(position)
                fromDialog.dismiss()
                convertFrom = adapter.getItem(position).toString()
            }
        }

        convertToDropdown.setOnClickListener {
            toDialog = Dialog(this@MainActivity)
            toDialog.setContentView(layout.to_spinner)
            toDialog.window?.setLayout(650, 800)
            toDialog.show()

            val edit: EditText = toDialog.findViewById(id.edit_text)
            val list: ListView = toDialog.findViewById(id.list_view)

            val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, arrayList)
            list.adapter = adapter

            edit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    adapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
            list.setOnItemClickListener { parent, view, position, id ->
                convertToDropdown.text = adapter.getItem(position)
                toDialog.dismiss()
                convertTo = adapter.getItem(position).toString()
            }

        }
        convertButton.setOnClickListener {
            try {
                val amountToConvertValue = amountToConvert.text.toString().toDouble()
                getConversionRate(convertFrom, convertTo, amountToConvertValue)
            } catch (e: Exception) {
                showPopup("Error", "Invalid input. Please enter a valid number.")
                // Handle the exception if needed
            }
        }

    }
    private fun getConversionRate(convertFrom: String, convertTo: String, amountToConvert: Double) {
        val apiKey = "efc063d720e7ffc336b270c167f3bc06d18173bc"
        val apiUrl = "https://api.getgeoapi.com/v2/currency/convert\n" +
                "?api_key=$apiKey\n" +
                "&from=$convertFrom&to=$convertTo&format=xml"

        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(Request.Method.GET, apiUrl,
            { response ->
                try {
                    val factory = XmlPullParserFactory.newInstance()
                    factory.isNamespaceAware = true
                    val parser = factory.newPullParser()
                    parser.setInput(StringReader(response))

                    var eventType = parser.eventType
                    var rate: String? = null

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        when (eventType) {
                            XmlPullParser.START_TAG -> {
                                val tagName = parser.name
                                if (tagName == "rate") {
                                    rate = parser.nextText()
                                }
                            }
                            // Handle other XML events as needed
                        }
                        eventType = parser.next()
                    }

                    if (rate != null) {
                        val result = rate.toDouble() * amountToConvert

                        runOnUiThread {
                            conversionRate.text = result.toString()
                        }
                    } else {
                        runOnUiThread {
                            conversionRate.text = "Error: Rate not found in XML response."
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("XMLParsingError", "Error parsing XML: $response")
                    runOnUiThread {
                        conversionRate.text = "Error: An error occurred while parsing the conversion rate. See logs for details."
                    }
                }
            },
            { error ->
                error.printStackTrace()
                runOnUiThread {
                    conversionRate.text = "Error: An error occurred while fetching conversion rate."
                }
            })

        queue.add(stringRequest)

    }

    private fun round(value: Double, places: Int): Double {
        if (places < 0) throw IllegalArgumentException()
        val bd = BigDecimal.valueOf(value)
        return bd.setScale(places, RoundingMode.HALF_UP).toDouble()
    }
    private fun showPopup(title: String, message: String) {
        val popupDialog = Dialog(this@MainActivity)
        popupDialog.setContentView(layout.popup) // Replace with your custom layout or use a system layout

        val popupTitle: TextView = popupDialog.findViewById(id.popupTitle)
        val popupMessage: TextView = popupDialog.findViewById(id.popupMessage)
        val closeButton: Button = popupDialog.findViewById(id.closeButton)

        popupTitle.text = title
        popupMessage.text = message

        closeButton.setOnClickListener {
            popupDialog.dismiss()
        }

        popupDialog.show()
    }
}


