package com.example.attendanceapp

import android.os.Build.ID
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.activityViewModels
import com.example.attendanceapp.databinding.ActivityStudentBinding
import com.example.attendanceapp.databinding.FragmentFirstBinding
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var _binding:FragmentFirstBinding?=null
private val binding get()= _binding!!
/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstFragment : Fragment(R.layout.fragment_first) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }
private lateinit var branch:String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding= FragmentFirstBinding.inflate(inflater,container,false)
        val ID:String?
        val bundle=arguments
        if (bundle != null) {
            ID=bundle.getString("ID").toString()
            //val lat=bundle.getString("lat")
            //val lon=bundle.getString("lon")
            //Toast.makeText(requireContext(),lat.toString()+lon.toString(), Toast.LENGTH_LONG).show()
            database= FirebaseDatabase.getInstance().getReference("student")
            database.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChild(ID)){
                        binding.details1.text=getString(R.string.hello,(snapshot.child(ID).child("fname").value).toString())
                        binding.details2.text="Sch : "+ID
                        branch=(snapshot.child(ID).child("branch").value).toString()
                        binding.details3.text="Branch : "+branch
                        binding.details4.text="Section : "+(snapshot.child(ID).child("sec").value).toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

            /*
            FirebaseDatabase.getInstance().getReference("attend").child(branch).addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for (userSnapshot in snapshot.children){
                            if(userSnapshot.value=="Data Structures") {
                                for (v in userSnapshot.children) {
                                    for (t in v.children) {

                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })*/

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirstFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                FirstFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}