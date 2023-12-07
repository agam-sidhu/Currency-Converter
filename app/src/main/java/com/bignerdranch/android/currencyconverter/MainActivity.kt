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
    private lateinit var exchangeRateView: TextView
    private lateinit var exchangeRate: TextView
    private lateinit var amountToConvert: EditText
    private lateinit var convertButton: Button
    private lateinit var fromDialog: Dialog
    private lateinit var toDialog: Dialog
    private var arrayList: ArrayList<String> = ArrayList()
    private lateinit var convertFrom: String
    private lateinit var convertTo: String
    private lateinit var conversionRateValue: String
    private var country: Array<String> = arrayOf("STN", "XAG", "XAU", "USDC", "USDT", "PLN", "UGX", "GGP", "MWK", "NAD", "ALL", "BHD", "JEP", "BWP", "MRU", "BMD", "MNT", "FKP", "PYG", "AUD", "KYD", "RWF", "WST", "SHP", "SOS", "SSP", "BIF", "SEK", "CUC", "BTN", "MOP", "XDR", "IMP", "INR", "BYN", "BOB", "SRD", "GEL", "ZWL", "EUR", "BBD", "RSD", "SDG", "VND", "VES", "ZMW", "KGS", "HUF", "BND", "BAM", "CVE", "BGN", "NOK", "BRL", "JPY", "HRK", "HKD", "ISK", "IDR", "KRW", "KHR", "XAF", "CHF", "MXN", "PHP", "RON", "RUB", "SGD", "AED", "KWD", "CAD", "PKR", "CLP", "CNY", "COP", "AOA", "KMF", "CRC", "CUP", "GNF", "NZD", "EGP", "DJF", "ANG", "DOP", "JOD", "AZN", "SVC", "NGN", "ERN", "SZL", "DKK", "ETB", "FJD", "XPF", "GMD", "AFN", "GHS", "GIP", "GTQ", "HNL", "GYD", "HTG", "XCD", "GBP", "AMD", "IRR", "JMD", "IQD", "KZT", "KES", "ILS", "LYD", "LSL", "LBP", "LRD", "AWG", "MKD", "LAK", "MGA", "ZAR", "MDL", "MVR", "MUR", "MMK", "MAD", "XOF", "MZN", "MYR", "OMR", "NIO", "NPR", "PAB", "PGK", "PEN", "ARS", "SAR", "QAR", "SCR", "SLL", "LKR", "SBD", "VUV", "USD", "DZD", "BDT", "BSD", "BZD", "CDF", "UAH", "YER", "TMT", "UZS", "UYU", "CZK", "SYP", "TJS", "TWD", "TZS", "TOP", "TTD", "THB", "TRY", "TND")
    private val currencyFullNameMap = mapOf(
        "STN" to "São Tomé and Príncipe dobra",
        "XAG" to "Silver (troy ounce)",
        "XAU" to "Gold (troy ounce)",
        "USDC" to "USD Coin",
        "USDT" to "Tether",
        "PLN" to "Polish złoty",
        "UGX" to "Ugandan shilling",
        "GGP" to "Guernsey pound",
        "MWK" to "Malawian kwacha",
        "NAD" to "Namibian dollar",
        "ALL" to "Albanian lek",
        "BHD" to "Bahraini dinar",
        "JEP" to "Jersey pound",
        "BWP" to "Botswana pula",
        "MRU" to "Mauritanian ouguiya",
        "BMD" to "Bermudian dollar",
        "MNT" to "Mongolian tögrög",
        "FKP" to "Falkland Islands pound",
        "PYG" to "Paraguayan guaraní",
        "AUD" to "Australian dollar",
        "KYD" to "Cayman Islands dollar",
        "RWF" to "Rwandan franc",
        "WST" to "Samoan tālā",
        "SHP" to "Saint Helena pound",
        "SOS" to "Somali shilling",
        "SSP" to "South Sudanese pound",
        "BIF" to "Burundian franc",
        "SEK" to "Swedish krona",
        "CUC" to "Cuban convertible peso",
        "BTN" to "Bhutanese ngultrum",
        "MOP" to "Macanese pataca",
        "XDR" to "Special drawing rights",
        "IMP" to "Manx pound",
        "INR" to "Indian rupee",
        "BYN" to "Belarusian ruble",
        "BOB" to "Bolivian boliviano",
        "SRD" to "Surinamese dollar",
        "GEL" to "Georgian lari",
        "ZWL" to "Zimbabwean dollar",
        "EUR" to "Euro",
        "BBD" to "Barbadian dollar",
        "RSD" to "Serbian dinar",
        "SDG" to "Sudanese pound",
        "VND" to "Vietnamese đồng",
        "VES" to "Venezuelan bolívar",
        "ZMW" to "Zambian kwacha",
        "KGS" to "Kyrgyzstani som",
        "HUF" to "Hungarian forint",
        "BND" to "Brunei dollar",
        "BAM" to "Bosnia and Herzegovina convertible mark",
        "CVE" to "Cape Verdean escudo",
        "BGN" to "Bulgarian lev",
        "NOK" to "Norwegian krone",
        "BRL" to "Brazilian real",
        "JPY" to "Japanese yen",
        "HRK" to "Croatian kuna",
        "HKD" to "Hong Kong dollar",
        "ISK" to "Icelandic króna",
        "IDR" to "Indonesian rupiah",
        "KRW" to "South Korean won",
        "KHR" to "Cambodian riel",
        "XAF" to "Central African CFA franc",
        "CHF" to "Swiss franc",
        "MXN" to "Mexican peso",
        "PHP" to "Philippine peso",
        "RON" to "Romanian leu",
        "RUB" to "Russian ruble",
        "SGD" to "Singapore dollar",
        "AED" to "United Arab Emirates dirham",
        "KWD" to "Kuwaiti dinar",
        "CAD" to "Canadian dollar",
        "PKR" to "Pakistani rupee",
        "CLP" to "Chilean peso",
        "CNY" to "Renminbi",
        "COP" to "Colombian peso",
        "AOA" to "Angolan kwanza",
        "KMF" to "Comorian franc",
        "CRC" to "Costa Rican colón",
        "CUP" to "Cuban peso",
        "GNF" to "Guinean franc",
        "NZD" to "New Zealand dollar",
        "EGP" to "Egyptian pound",
        "DJF" to "Djiboutian franc",
        "ANG" to "Netherlands Antillean guilder",
        "DOP" to "Dominican peso",
        "JOD" to "Jordanian dinar",
        "AZN" to "Azerbaijani manat",
        "SVC" to "Salvadoran colón",
        "NGN" to "Nigerian naira",
        "ERN" to "Eritrean nakfa",
        "SZL" to "Swazi lilangeni",
        "DKK" to "Danish krone",
        "ETB" to "Ethiopian birr",
        "FJD" to "Fijian dollar",
        "XPF" to "CFP franc",
        "GMD" to "Gambian dalasi",
        "AFN" to "Afghan afghani",
        "GHS" to "Ghanaian cedi",
        "GIP" to "Gibraltar pound",
        "GTQ" to "Guatemalan quetzal",
        "HNL" to "Honduran lempira",
        "GYD" to "Guyanese dollar",
        "HTG" to "Haitian gourde",
        "XCD" to "Eastern Caribbean dollar",
        "GBP" to "Pound sterling",
        "AMD" to "Armenian dram",
        "IRR" to "Iranian rial",
        "JMD" to "Jamaican dollar",
        "IQD" to "Iraqi dinar",
        "KZT" to "Kazakhstani tenge",
        "KES" to "Kenyan shilling",
        "ILS" to "Israeli new shekel",
        "LYD" to "Libyan dinar",
        "LSL" to "Lesotho loti",
        "LBP" to "Lebanese pound",
        "LRD" to "Liberian dollar",
        "AWG" to "Aruban florin",
        "MKD" to "Macedonian denar",
        "LAK" to "Lao kip",
        "MGA" to "Malagasy ariary",
        "ZAR" to "South African rand",
        "MDL" to "Moldovan leu",
        "MVR" to "Maldivian rufiyaa",
        "MUR" to "Mauritian rupee",
        "MMK" to "Burmese kyat",
        "MAD" to "Moroccan dirham",
        "XOF" to "West African CFA franc",
        "MZN" to "Mozambican metical",
        "MYR" to "Malaysian ringgit",
        "OMR" to "Omani rial",
        "NIO" to "Nicaraguan córdoba",
        "NPR" to "Nepalese rupee",
        "PAB" to "Panamanian balboa",
        "PGK" to "Papua New Guinean kina",
        "PEN" to "Peruvian sol",
        "ARS" to "Argentine peso",
        "SAR" to "Saudi riyal",
        "QAR" to "Qatari riyal",
        "SCR" to "Seychellois rupee",
        "SLL" to "Sierra Leonean leone",
        "LKR" to "Sri Lankan rupee",
        "SBD" to "Solomon Islands dollar",
        "VUV" to "Vanuatu vatu",
        "USD" to "United States dollar",
        "DZD" to "Algerian dinar",
        "BDT" to "Bangladeshi taka",
        "BSD" to "Bahamian dollar",
        "BZD" to "Belize dollar",
        "CDF" to "Congolese franc",
        "UAH" to "Ukrainian hryvnia",
        "YER" to "Yemeni rial",
        "TMT" to "Turkmenistan manat",
        "UZS" to "Uzbekistani soʻm",
        "UYU" to "Uruguayan peso",
        "CZK" to "Czech koruna",
        "SYP" to "Syrian pound",
        "TJS" to "Tajikistani somoni",
        "TWD" to "New Taiwan dollar",
        "TZS" to "Tanzanian shilling",
        "TOP" to "Tongan paʻanga",
        "TTD" to "Trinidad and Tobago dollar",
        "THB" to "Thai baht",
        "TRY" to "Turkish lira",
        "TND" to "Tunisian dinar"
    )
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
        exchangeRate = findViewById(id.exchangeRate)
        exchangeRateView = findViewById(id.exchangeRateText)

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
                val selectedCurrencyCode = adapter.getItem(position).toString()
                convertFromDropdown.text = selectedCurrencyCode
                convertFrom = selectedCurrencyCode
                // Update the dropdown text to display the currency name
                val currencyName = currencyFullNameMap[selectedCurrencyCode]
                convertFromDropdown.text = currencyName ?: selectedCurrencyCode
                fromDialog.dismiss()
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
                val selectedCurrencyCode = adapter.getItem(position).toString()
                convertToDropdown.text = selectedCurrencyCode
                convertTo = selectedCurrencyCode
                // Update the dropdown text to display the currency name
                val currencyName = currencyFullNameMap[selectedCurrencyCode]
                convertToDropdown.text = currencyName ?: selectedCurrencyCode
                toDialog.dismiss()
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
                            conversionRate.text = result.toString() + " $convertTo"
                            exchangeRateView.text = "1 $convertFrom = $rate $convertTo"

                        }
                    } else {
                        runOnUiThread {
                            conversionRate.text = "Error: Rate not found in XML response."
                            exchangeRate.text = "Exchange Rate: N/A"

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
        popupMessage.text = "Invalid Input (Missing Necessary Currency Input"

        closeButton.setOnClickListener {
            popupDialog.dismiss()
        }

        popupDialog.show()
    }
}


