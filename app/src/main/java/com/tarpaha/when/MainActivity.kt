package com.tarpaha.`when`

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun requestButtonClick(view: View) {

        showResult("... loading ...", null)

        val queue = Volley.newRequestQueue(this)

        val editTextRequest = findViewById<EditText>(R.id.editTextRequest);
        val url = "https://risboo6909.org/when/get_unix?input=" + Uri.encode(editTextRequest.text.toString());

        val stringRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener<JSONObject> { json ->
                val result = json.optJSONObject("result")
                if(result == null) {
                    showResult("Wrong response from server, no result", json)
                } else {
                    val error = result.optJSONObject("Err")
                    if(error != null) {
                        showResult("Parser error, invalid input data", json)
                    } else {
                        val ok = result.optJSONArray("Ok");
                        if(ok == null) {
                            showResult("Wrong response from server, neither Ok or Err in result", json)
                        }
                        else {
                            if(ok.length() <= 0) {
                                showResult("Cannot parse", json)
                            } else {
                                showResult(getDateTimeFromUnixTimeStamp(ok[0].toString()), json)
                            }
                        }
                    }
                }
            },
            Response.ErrorListener {error ->
                val json = JSONObject()
                json.put("error", error.toString())
                showResult("Error", json)
            })

        queue.add(stringRequest);
    }

    private fun showResult(dateTime: String, json: JSONObject?) {
        findViewById<TextView>(R.id.textViewAnswer).text = dateTime
        findViewById<TextView>(R.id.textViewJSON).text = json?.toString(4) ?: ""
    }

    private fun getDateTimeFromUnixTimeStamp(timeStampSeconds: String): String {
        val date = Date(timeStampSeconds.toLong() * 1000)
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(date)
    }

}
