package com.example.securedatasharingfordtn.mainbody

data class Members(val data: List<Member>)

data class Member(
    val name: String,
    val id: Int
)
