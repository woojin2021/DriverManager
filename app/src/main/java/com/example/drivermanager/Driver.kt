package com.example.drivermanager

data class Driver(
    var did       : String,
    var password  : String,
    var name      : String,
    var auth      : Int,
    var mobile    : String,
    var onwork    : Int,
    var permitted : Int,
    var reason    : String
) {
    constructor(did: String, password: String) : this(did, password, "", 0, "", 0, 0, "")
}
