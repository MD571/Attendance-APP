package com.example.attendanceapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.attendanceapp.databinding.ActivityDownloadAttendanceBinding
import com.google.firebase.database.*
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


private lateinit var binding: ActivityDownloadAttendanceBinding
private lateinit var database: DatabaseReference
private lateinit var datePickerDialog:DatePickerDialog
private lateinit var dates:String
private lateinit var datep:String
private lateinit var userArrayList: ArrayList<attend>
@Suppress("DEPRECATION")
class download_attendance : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDownloadAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val branch= binding.branch.selectedItem.toString()
        when(branch){
            "CSE"->binding.classtv.adapter= ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,resources.getStringArray(R.array.CSE))
        }
        val sub=binding.classtv.selectedItem.toString()

        binding.datepick.setOnClickListener {
            initDatePicker()
            datePickerDialog.show()
            binding.datetxt.text=todaysDate
        }
        val workbook= HSSFWorkbook()
        val sheet=workbook.createSheet("attendance")
        var fos: FileOutputStream
        var i=1
        userArrayList= arrayListOf<attend>()
        val columns= arrayListOf<String>("Scholar Number","Full Name","Branch","Section","Subject","Time","Location")
        binding.download.setOnClickListener {
            val row=sheet.createRow(0)
            for (i in 0 until columns.size) {
                val cell: Cell = row.createCell(i)
                cell.setCellValue(columns[i])
            }
            row.createCell(0).setCellValue("Scholar Number")
            row.createCell(1).setCellValue("Full Name")
            row.createCell(2).setCellValue("Branch ")
            row.createCell(3).setCellValue("Section")
            row.createCell(4).setCellValue("Subject")
            row.createCell(5).setCellValue("Time")
            row.createCell(6).setCellValue("Location")
            //Log.d("downlw","attend/${branch}/${sub}/${dates}")
            database= FirebaseDatabase.getInstance().getReference().child("attend/${branch}/${sub}/${dates}")
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for (us in snapshot.children) {
                            val row: Row = sheet.createRow(i)
                            row.height=500
                            var attend= us.getValue(attend::class.java)!!
                            attend.ID?.let { it1 -> Log.d("downlw", it1) }

                            row.createCell(0).setCellValue(attend.ID.toString())
                            row.createCell(1).setCellValue(attend.fname.toString())
                            row.createCell(2).setCellValue(branch.toString())
                            row.createCell(3).setCellValue(attend.sec.toString())
                            row.createCell(4).setCellValue(binding.classtv.selectedItem as String)
                            row.createCell(5).setCellValue(attend.time.toString())
                            /*val bitmap=decodeFromFirebaseBase64(attend.image.toString())
                            val baos=ByteArrayOutputStream()
                            //binding.faceimg.setImageBitmap(bitmap)
                            bitmap.compress(Bitmap.CompressFormat.PNG,0,baos)
                            val byteImage=baos.toByteArray()
                            val intPI=workbook.addPicture(byteImage,Workbook.PICTURE_TYPE_JPEG)
                            val clientAnchor:ClientAnchor=workbook.creationHelper.createClientAnchor()
                            clientAnchor.setCol1(6)
                            clientAnchor.setRow1(i)

                            clientAnchor.setCol2(9)
                            clientAnchor.setRow2(i+1)
                            val pic=sheet.createDrawingPatriarch()
                            pic.createPicture(clientAnchor,intPI)*/
                            //pic.resize()
                            row.createCell(6).setCellValue(attend.locat.toString())
                            i++


                        }
                        sheet.setColumnWidth(0,(25*200))
                        sheet.setColumnWidth(1,(50*200))
                        sheet.setColumnWidth(2,(20*200))
                        sheet.setColumnWidth(3,(20*200))
                        sheet.setColumnWidth(4,(20*200))
                        sheet.setColumnWidth(5,(20*200))
                        sheet.setColumnWidth(6,(20*200))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

            try {
                print(workbook)
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
                    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val file= File(path, "/Attendance-${datep}.xlsx")
                    fos = FileOutputStream(file)
                    workbook.write(fos)
                    //fos.flush()
                    fos.close()
                    Toast.makeText(this, "Operation Completed", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {

                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val todaysDate: String
        private get() {
            val cal = Calendar.getInstance()
            val year = cal[Calendar.YEAR]
            var month = cal[Calendar.MONTH]
            month = month + 1
            val day = cal[Calendar.DAY_OF_MONTH]
            return makeDateString(day, month, year)
        }

    private fun initDatePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month = month + 1
                val date = makeDateString(day, month, year)
                binding.datetxt.text = date
            }
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]
        val style = AlertDialog.THEME_HOLO_LIGHT
        datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis())
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        dates=year.toString()+"/"+getMonthFormat(month)+"/"+day.toString()
        datep=year.toString()+"-"+getMonthFormat(month)+"-"+day.toString()
        return getMonthFormat(month) + " " + day + " " + year
    }

    private fun getMonthFormat(month: Int): String {
        if (month == 1) return "Jan"
        if (month == 2) return "Feb"
        if (month == 3) return "Mar"
        if (month == 4) return "Apr"
        if (month == 5) return "May"
        if (month == 6) return "Jun"
        if (month == 7) return "Jul"
        if (month == 8) return "Aug"
        if (month == 9) return "Sep"
        if (month == 10) return "Oct"
        if (month == 11) return "Nov"
        return if (month == 12) "Dec" else "Jan"

        //default should never happen
    }

    private fun decodeFromFirebaseBase64(image: String): Bitmap {
        val decodedByteArray = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
    }

}

private fun DatabaseReference.addChildEventListener(valueEventListener: ValueEventListener) {

}
