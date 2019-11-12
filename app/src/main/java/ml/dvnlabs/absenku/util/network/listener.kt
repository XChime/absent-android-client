package ml.dvnlabs.absenku.util.network

interface listener {
    fun onFetchComplete(data : String)
    fun onFetchFailure(msg : String)
    fun onFetchStart()
}