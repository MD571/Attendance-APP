package com.example.attendanceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MyAdapter(private val userlist:ArrayList<user>):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.stu_approv,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem =userlist[position]
        holder.fname.text=currentitem.fname
        holder.sch.text= currentitem.snum
        holder.email.text=currentitem.email
        holder.branch.text=currentitem.branch
        holder.sec.text=currentitem.sec
    }

    override fun getItemCount(): Int {
        return userlist.size
    }
    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val fname: TextView=itemView.findViewById((R.id.tvfname))
        val sch: TextView=itemView.findViewById((R.id.tvsch))
        val email: TextView=itemView.findViewById((R.id.tvemail))
        val branch: TextView=itemView.findViewById((R.id.tvBranch))
        val sec: TextView=itemView.findViewById((R.id.tvsec))
        private lateinit var database: DatabaseReference
        private lateinit var sdatabase: DatabaseReference
    init {

        itemView.findViewById<Button>(R.id.acceptbtn).setOnClickListener {
            database = FirebaseDatabase.getInstance().getReference("request")
            sdatabase = FirebaseDatabase.getInstance().getReference("student")
            database.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                        val snum=sch.text.toString()
                        val pass=snapshot.child(snum).child("password").value.toString()
                        val image=snapshot.child(snum).child("Image").value
                        sdatabase.child(snum).child("fname").setValue(fname.text.toString())
                        sdatabase.child(snum).child("email").setValue(email.text.toString())
                        sdatabase.child(snum).child("snum").setValue(sch.text.toString())
                        sdatabase.child(snum).child("branch").setValue(branch.text.toString())
                        sdatabase.child(snum).child("sec").setValue(sec.text.toString())
                        sdatabase.child(snum).child("password").setValue(pass)
                        sdatabase.child(snum).child("Image").setValue(image)
                    database.child(snum).removeValue()
                    //Toast.makeText(this@MyViewHolder,"$pass.toString()",Toast.LENGTH_SHORT).show()


                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }
        itemView.findViewById<Button>(R.id.rejectbtn).setOnClickListener {
            FirebaseDatabase.getInstance().getReference("request").child(sch.text.toString()).removeValue()


        }

    }



    }
}