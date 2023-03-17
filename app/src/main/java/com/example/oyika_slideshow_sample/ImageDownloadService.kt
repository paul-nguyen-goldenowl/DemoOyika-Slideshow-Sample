
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.widget.Toast
import androidx.core.app.JobIntentService
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream

class ImageDownloadService : JobIntentService() {

    companion object {
        const val JOB_ID = 1000

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, ImageDownloadService::class.java, JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val url = intent.getStringExtra("url")
        val fileName = intent.getStringExtra("fileName")

        if (url != null && fileName != null) {
            try {
                val bitmap: Bitmap = Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .submit()
                    .get()

                val folder = File(Environment.getExternalStorageDirectory(), "MyApp")
                if (!folder.exists()) {
                    folder.mkdirs()
                }

                val file = File(folder, fileName)
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()

                showToast("Image downloaded")
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Error downloading image")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}