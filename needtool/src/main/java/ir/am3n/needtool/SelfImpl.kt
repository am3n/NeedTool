package ir.am3n.needtool

/* todo: need dependency
fun Context.openInBrowser(url: String) {
    try {
        val url = Uri.parse(url)
        val intent = CustomTabsIntent.Builder()
                .setToolbarColor(getResourceColor(R.attr.colorPrimary))
                .build()
        intent.launchUrl(this, url)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
*/

/* todo: need dependency
inline fun<reified W : ListenableWorker> Context.enqueueWorker(func: OneTimeWorkRequest.Builder.() -> Unit) {
    val builder = OneTimeWorkRequestBuilder<W>()
    builder.func()
    val workerRequest = builder.build()
    WorkManager.getInstance(this.applicationContext).enqueue(workerRequest)
}*/