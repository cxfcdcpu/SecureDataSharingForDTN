package com.example.securedatasharingfordtn.mainbody

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securedatasharingfordtn.Preferences
import com.example.securedatasharingfordtn.databinding.ActivityMembersBinding
import kotlinx.android.synthetic.main.activity_members.*

class MembersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMembersBinding
    private lateinit var preferences: Preferences
    private lateinit var adapter : ManageMembersAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        preferences = Preferences(this)

        adapter = ManageMembersAdapter(preferences)
        setContentView(binding.root)

        recyclerView.layoutManager = LinearLayoutManager(this) // 2
        recyclerView.adapter = adapter // 3

        loadData()


    }

    private fun loadData(){
        val members = preferences.getMembers()

        Log.i("MembersActivity",members!!)

        if (members!=""){
            val memberList = members.split(";")
            val m = Members(memberList.withIndex().map { Member(it.value,it.index) })

            adapter.submitList(m.data)
        }

    }
}