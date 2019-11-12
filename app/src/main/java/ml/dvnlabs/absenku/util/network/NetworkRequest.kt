package ml.dvnlabs.absenku.util.network

import android.content.Context
import android.system.Os.listen
import com.android.volley.AuthFailureError
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest


class NetworkRequest(context: Context, listener: listener, url : String, REQUEST_CODE : Int, params : HashMap<String,String>?) {

    private val CODE_GET_REQUEST = 1024
    private val CODE_POST_REQUEST = 1025

    init {
        if(REQUEST_CODE == CODE_GET_REQUEST){
            getRequest(context, listener, url)
        }else if (REQUEST_CODE == CODE_POST_REQUEST && params != null){
            postRequest(context, listener, url, params)
        }
    }

    private fun getRequest(context: Context,listener: listener,url : String){
        listener.onFetchStart()
        val stringRequest = StringRequest(Request.Method.GET,url,Response.Listener<String>{
            listener.onFetchComplete(it)
        },Response.ErrorListener {
            if (it is NoConnectionError){
                listener.onFetchFailure("No Connection!")
            }else{
                listener.onFetchFailure(it.message!!)
            }
        })
        RequestQueue(context).addToRequestQueue(stringRequest.setShouldCache(false))
    }
    private fun postRequest(context: Context,listener: listener,url : String,params : HashMap<String,String>){
        listener.onFetchStart()

        val stringRequest = object : StringRequest(Method.POST,url,Response.Listener<String>{
            listener.onFetchComplete(it)
        },Response.ErrorListener {
            if (it is NoConnectionError){
                listener.onFetchFailure("No Connection!")
            }else if (it.networkResponse!=null && it.networkResponse.statusCode == 401){
                listener.onFetchFailure("BAD AUTH");
            }else{
                listener.onFetchFailure(it.message!!)
            }
        }){
            override fun getParams(): MutableMap<String, String> {
                return params
            }

            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        }
        RequestQueue(context).addToRequestQueue(stringRequest.setShouldCache(false))
    }
}