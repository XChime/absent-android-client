package ml.dvnlabs.absenku.util.network

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.Volley


class RequestQueue(context: Context) {
    private var requestQueue: com.android.volley.RequestQueue? = null
    private val mContext: Context = context


    private fun getRequestQueue(): com.android.volley.RequestQueue? {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(mContext.applicationContext)
        }
        return requestQueue
    }

    fun <T> addToRequestQueue(req: Request<T?>) {
        req.retryPolicy = DefaultRetryPolicy(
            5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        getRequestQueue()!!.add(req).tag = "NewReq"
    }

    fun clearCache() {
        requestQueue!!.cache.clear()
    }

    fun removeCache(key: String?) {
        requestQueue!!.cache.remove(key)
    }

    fun clearRequest() {
        requestQueue!!.cancelAll("NewReq")
    }
}