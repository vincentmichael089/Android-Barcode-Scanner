package com.example.aftermath.barcode

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var svBarcode:SurfaceView
    private lateinit var tvBarcode:TextView

    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource //link surface view with barcode detector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        svBarcode = findViewById(R.id.sv_barcode)
        tvBarcode = findViewById(R.id.tv_barcode)

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
                        tvBarcode.text = barcodes.valueAt(0).displayValue
                    }
                }
            }

        })

        cameraSource =  CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(1024,768).setRequestedFps(25f).setAutoFocusEnabled(true).build()

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
}
