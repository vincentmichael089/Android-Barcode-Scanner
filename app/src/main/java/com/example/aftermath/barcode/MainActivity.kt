package com.example.aftermath.barcode

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest
import android.content.Context.WINDOW_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.webkit.URLUtil


class MainActivity : AppCompatActivity() {

    private lateinit var svBarcode:SurfaceView
    private lateinit var tvBarcode:TextView

    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource //link surface view with barcode detector
    private lateinit var overlay: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val handler = Handler()
//        handler.postDelayed({
//            // Do something after 5s = 5000ms
//        }, 2000)

        setContentView(R.layout.activity_main)

        val displayMetrics = DisplayMetrics()
        val wm = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        setSupportActionBar(toolbar)

        svBarcode = findViewById(R.id.sv_barcode)
        tvBarcode = findViewById(R.id.tv_barcode)

        tvBarcode.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)

            //TODO() //BAIKIN FUNGSI CHECKING URL, DI IF ELSE LANGSUNG TES BUILDER
            val valueholder = tvBarcode.text.toString()
            val bundle = Bundle()
            val uris = Uri.parse(valueholder)
            val intent = Intent(Intent.ACTION_VIEW, uris)
            bundle.putBoolean("new_window", true)
            intent.putExtras(bundle)

            builder.setMessage("Do you want to visit ${valueholder}?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", DialogInterface.OnClickListener {
                        dialog, id -> startActivity(intent);
                        dialog.cancel()

                    })
                    .setNegativeButton("No", DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()

                        ///

                    })
            builder.create().show()
        })

        barcodeDetector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode>{
            override fun release() {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {

                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                val barcodes = detections?.detectedItems

                if(barcodes!!.size()>0){
                    tvBarcode.post{
                        val valueholder = barcodes.valueAt(0).displayValue
                        tvBarcode.text = valueholder
                        val isValid = URLUtil.isValidUrl(tvBarcode.text.toString())

                        if(isValid){
                            tvBarcode.setTextColor(Color.parseColor("#9acd32"))
                            tvBarcode.isClickable = true
                        }else{
                            tvBarcode.setTextColor(Color.WHITE)
                            tvBarcode.isClickable = false
                        }
                    }
                }
            }

        })

        cameraSource =  CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(screenHeight,screenWidth).setRequestedFps(60f).setAutoFocusEnabled(true).build()

        svBarcode.holder.addCallback(object: SurfaceHolder.Callback2{
            override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                cameraSource.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                if(ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)
                    cameraSource.start(holder)
                else ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.CAMERA),89)
            }
        })

    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 89){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                cameraSource.start(svBarcode.holder)
            }else{
                Toast.makeText(this, "PERMISSION NEEDED!",Toast.LENGTH_SHORT)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        barcodeDetector.release()
        cameraSource.stop()
        cameraSource.release()
    }

//    fun isURL(url: String): Boolean {
//        try {
//            java.net.URL(url).openStream().close()
//            return true
//        } catch (ex: Exception) {
//        }
//        return false
//    }
}
