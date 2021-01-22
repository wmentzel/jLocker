package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose

class JsonRoot(
    @Expose val encryptedBuildingsBase64: String,
    @Expose val settings: Settings,
    @Expose val tasks: List<Task>,
    @Expose val superUser: SuperUser,
    @Expose val restrictedUser: RestrictedUser
)