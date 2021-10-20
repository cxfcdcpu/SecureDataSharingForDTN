package com.example.securedatasharingfordtn.mainbody

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.securedatasharingfordtn.databinding.ItemRevoBinding
import java.util.prefs.Preferences



class ManageMembersAdapter(private val preferences: com.example.securedatasharingfordtn.Preferences) : ListAdapter<Member, MemberViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRevoBinding.inflate(inflater,parent,false)

        return MemberViewHolder(binding,preferences)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }
}


private val DIFF_CALLBACK: DiffUtil.ItemCallback<Member> = object : DiffUtil.ItemCallback<Member>() {
    override fun areItemsTheSame(oldData: Member,
                                 newData: Member): Boolean {
        return oldData.id == newData.id
    }

    override fun areContentsTheSame(oldData: Member,
                                    newData: Member): Boolean {
        return oldData == newData
    }
}

class MemberViewHolder(private val binding: ItemRevoBinding, private val preferences: com.example.securedatasharingfordtn.Preferences): RecyclerView.ViewHolder(binding.root){

    fun bindTo(member: Member){
        binding.MemberName.text = member.name
        val curSet = preferences.getRevokedMembers()
        if (curSet.contains(member.id.toString())){
            binding.switch2.isChecked = true
        }
        binding.switch2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val newSet = preferences.getRevokedMembers()
                newSet.add(member.id.toString())
                preferences.setRevokedMembers(newSet)
            } else {
                val newSet = preferences.getRevokedMembers()
                newSet.remove(member.id.toString())
                preferences.setRevokedMembers(newSet)
            }
        }


    }
}