package com.example.cryptoapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.cryptoapp.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityMainBinding
    private lateinit var rvAdapter: RvAdapter
    private lateinit var data:ArrayList<Modal>
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
        data=ArrayList<Modal>()
//        data.add(Modal(name = "", symbol = "", price = ""))

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        apiData
        rvAdapter=RvAdapter(this,data)
        binding.Rv.layoutManager=LinearLayoutManager(this)
        binding.Rv.adapter=rvAdapter
        // for working of search button
        binding.search.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
                val filterdata=ArrayList<Modal>()
                for (item in data){
                    if (item.name.lowercase(Locale.getDefault()).contains(p0.toString().lowercase(
                            Locale.getDefault()))){
                        filterdata.add(item)
                    }
                }
                if (filterdata.isEmpty()){
                    Toast.makeText(this@MainActivity,"Not Found",Toast.LENGTH_LONG).show()
                }
                else{
                    rvAdapter.changeData(filterdata)
                }


            }

        })
    }


    val apiData:Unit
        get() {
            val url="https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest"
            VolleyLog.DEBUG = true
            val queue = Volley.newRequestQueue(this)

            val jsonObjectRequest:JsonObjectRequest=
                @SuppressLint("NotifyDataSetChanged")
                object :JsonObjectRequest(Method.GET,url,null,Response.Listener {
                    response->
                    //  if data found
                    binding.progressBar.isVisible=false
                      try {
                          val dataArray=response.getJSONArray("data")
                          for (i in 0 until dataArray.length()){
                            val dataObject=dataArray.getJSONObject(i)
                              val symbol=dataObject.getString("symbol")
                              val name=dataObject.getString("name")
                              val quote=dataObject.getJSONObject("quote")
                              val USD=quote.getJSONObject("USD")
                              val price= String.format("$"+"%.2f",USD.getDouble("price"))
                              data.add(Modal(name,symbol,price.toString()))
                          }
                          rvAdapter.notifyDataSetChanged()
                      }catch (e:Exception){
                          Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
                      }
                },Response.ErrorListener {error->
                    Toast.makeText(this@MainActivity,"Error 2",Toast.LENGTH_LONG).show()
                })
                {
                    override fun getHeaders(): Map<String, String> {

                        val headers=HashMap<String,String>();
                        headers["X-CMC_PRO_API_KEY"]="a21a8ac8-cda2-4b0f-8bbc-9153a56bf7b4"
                        return headers

                    }
                }
            queue.add(jsonObjectRequest)
        }
}
