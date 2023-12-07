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
import org.json.JSONException
import org.json.JSONObject
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        convertFromDropdown = findViewById(R.id.convertFromMenu)
        convertToDropdown = findViewById(R.id.convertToMenu)
        convertButton = findViewById(R.id.convertButton)
        conversionRate = findViewById(R.id.conversionRateText)
        resultTextView = findViewById(R.id.conversionResult)
        amountToConvert = findViewById(R.id.amountToConvertEditText)

        //This might be an issue 16 minutes
        arrayList = ArrayList(country.asList())

        convertFromDropdown.setOnClickListener {
            fromDialog = Dialog(this@MainActivity)
            fromDialog.setContentView(R.layout.from_spinner)
            fromDialog.window?.setLayout(650, 800)
            fromDialog.show()

            val edit: EditText = fromDialog.findViewById(R.id.edit_text)
            val list: ListView = fromDialog.findViewById(R.id.list_view)

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
            toDialog.setContentView(R.layout.to_spinner)
            toDialog.window?.setLayout(650, 800)
            toDialog.show()

            val edit: EditText = toDialog.findViewById(R.id.edit_text)
            val list: ListView = toDialog.findViewById(R.id.list_view)

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
        val apiKey = "fca_live_1lnNsBIpRPJ3UUnPVXkFexM95fBtQjC4Q8RhtIt3"
        val apiUrl = "https://api.freecurrencyapi.com/v1/latest?apikey=$apiKey&base=$convertFrom&symbols=$convertTo"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(Request.Method.GET, apiUrl,
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val dataObject = jsonObject.getJSONObject("data")

                    // Check if the currency code exists in the dataObject
                    if (dataObject.has(convertTo)) {
                        val convertVal = dataObject.getDouble(convertTo)
                        val result = convertVal * amountToConvert

                        runOnUiThread {
                            conversionRate.text = result.toString()
                        }
                    } else {
                        runOnUiThread {
                            conversionRate.text = "Error: Conversion rate not available for $convertTo."
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("JSONParsingError", "Error parsing JSON: $response")
                    runOnUiThread {
                        conversionRate.text =
                            "Error: An error occurred while parsing the conversion rate. See logs for details."
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
        /*
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(Request.Method.GET, apiUrl,
            { response ->
                try {
                    val jsonObject = JSONObject(response)

                    //val conversionRateValue = round(jsonObject.getDouble("${convertFrom}_$convertTo"), 2)
                    val key = "${convertFrom}_$convertTo"
                    //val conversionRateValue = round(jsonObject.getDouble(key), 2)
                    val convertVal = jsonObject.getDouble(key)

                    println("Conversion Rate Value: $conversionRateValue")
                    val result = conversionRateValue.toDouble() * amountToConvert
                    val res = convertVal * amountToConvert
                   //val result = "" + round((conversionRateValue * amountToConvert),2)
                    println("Result: $result")
                    runOnUiThread {
                        // Assuming conversionRate is a TextView, set the result to it
                        conversionRate.text = res.toString()
                    }
                    // Assuming conversionRate is a TextView, set the result to it
                        //conversionRate.text = result.toString()
                        //conversionRate.text = res.toString()

                    // If you need to return the result for some reason, you might want to use a callback or update the UI differently
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
            })

        queue.add(stringRequest)
        */
    }

    private fun round(value: Double, places: Int): Double {
        if (places < 0) throw IllegalArgumentException()
        val bd = BigDecimal.valueOf(value)
        return bd.setScale(places, RoundingMode.HALF_UP).toDouble()
    }
    private fun showPopup(title: String, message: String) {
        val popupDialog = Dialog(this@MainActivity)
        popupDialog.setContentView(R.layout.popup) // Replace with your custom layout or use a system layout

        val popupTitle: TextView = popupDialog.findViewById(R.id.popupTitle)
        val popupMessage: TextView = popupDialog.findViewById(R.id.popupMessage)
        val closeButton: Button = popupDialog.findViewById(R.id.closeButton)

        popupTitle.text = title
        popupMessage.text = message

        closeButton.setOnClickListener {
            popupDialog.dismiss()
        }

        popupDialog.show()
    }
}


