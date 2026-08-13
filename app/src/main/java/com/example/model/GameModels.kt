package com.example.model

enum class StoreDepartmentType(
    val id: String,
    val displayName: String,
    val shortName: String,
    val iconName: String,
    val primaryColor: Long,
    val defaultCapacity: Int,
    val baseServiceSeconds: Float
) {
    HOME_THEATER(
        id = "dept_ht",
        displayName = "Home Theater & TVs",
        shortName = "Home Theater",
        iconName = "tv",
        primaryColor = 0xFF3D5AFE,
        defaultCapacity = 4,
        baseServiceSeconds = 6.0f
    ),
    COMPUTERS(
        id = "dept_comp",
        displayName = "Computers & Laptops",
        shortName = "Computers",
        iconName = "laptop",
        primaryColor = 0xFF00B0FF,
        defaultCapacity = 4,
        baseServiceSeconds = 6.5f
    ),
    DIGITAL_IMAGING(
        id = "dept_photo",
        displayName = "Cameras & Drones",
        shortName = "Cameras",
        iconName = "photo_camera",
        primaryColor = 0xFFFF9100,
        defaultCapacity = 3,
        baseServiceSeconds = 5.5f
    ),
    PORTABLE_AUDIO(
        id = "dept_audio",
        displayName = "Audio & Headphones",
        shortName = "Audio",
        iconName = "headphones",
        primaryColor = 0xFFE040FB,
        defaultCapacity = 3,
        baseServiceSeconds = 5.0f
    ),
    SMART_HOME(
        id = "dept_smart",
        displayName = "Smart Home & Mobile",
        shortName = "Smart Home",
        iconName = "smartphone",
        primaryColor = 0xFF00E676,
        defaultCapacity = 4,
        baseServiceSeconds = 5.5f
    ),
    APPLIANCES(
        id = "dept_app",
        displayName = "Major Appliances",
        shortName = "Appliances",
        iconName = "kitchen",
        primaryColor = 0xFFFF5252,
        defaultCapacity = 3,
        baseServiceSeconds = 8.0f
    ),
    STORE_PICKUP(
        id = "dept_pickup",
        displayName = "Order Pickup / Returns",
        shortName = "Order Pickup",
        iconName = "inventory",
        primaryColor = 0xFFFFD600,
        defaultCapacity = 5,
        baseServiceSeconds = 3.5f
    ),
    GEEK_TECH_SUPPORT(
        id = "dept_tech",
        displayName = "Tech Support & Repairs",
        shortName = "Tech Support",
        iconName = "build",
        primaryColor = 0xFFFF6D00,
        defaultCapacity = 3,
        baseServiceSeconds = 7.5f
    ),
    RESTROOMS(
        id = "dept_rest",
        displayName = "Restrooms / Just Looking",
        shortName = "Restroom",
        iconName = "wc",
        primaryColor = 0xFF78909C,
        defaultCapacity = 8,
        baseServiceSeconds = 2.0f
    )
}

enum class CustomerArchetype(
    val archetypeId: String,
    val title: String,
    val defaultPatience: Float,
    val avgSpending: Int,
    val isShoplifter: Boolean = false,
    val isVIP: Boolean = false,
    val requiresManager: Boolean = false,
    val preferredDepartment: StoreDepartmentType,
    val initialQuotes: List<String>,
    val revealedIntents: List<String>,
    val avatarEmoji: String,
    val shirtColorHex: Long
) {
    SPEC_SHEET_DAD(
        archetypeId = "spec_dad",
        title = "The Spec-Sheet Dad",
        defaultPatience = 75f,
        avgSpending = 1800,
        preferredDepartment = StoreDepartmentType.HOME_THEATER,
        initialQuotes = listOf(
            "Which HDMI 2.1 cable has the thickest braided nylon shielding?",
            "I need 120Hz refresh rate minimum for the big Sunday game.",
            "Is the OLED panel RGB or WRGB subpixel layout?"
        ),
        revealedIntents = listOf(
            "Needs an 85-inch OLED TV and monster audio setup for the football playoff.",
            "Wants a 7.1.4 Dolby Atmos surround sound soundbar with dual subwoofers."
        ),
        avatarEmoji = "👨‍🦳",
        shirtColorHex = 0xFF2E7D32
    ),
    CONFUSED_SENIOR(
        archetypeId = "confused_senior",
        title = "The Confused Senior",
        defaultPatience = 90f,
        avgSpending = 350,
        preferredDepartment = StoreDepartmentType.GEEK_TECH_SUPPORT,
        initialQuotes = listOf(
            "Excuse me young man, where is the Cloud physically located in the store?",
            "My grandson told me to buy 32 Gigabytes of RAM in a bag.",
            "My iPad screen is making a funny clicking noise."
        ),
        revealedIntents = listOf(
            "Needs virus cleanup and email password recovery on their 2011 tablet.",
            "Wants to buy an easy-to-use tablet for looking at bird photos."
        ),
        avatarEmoji = "👵",
        shirtColorHex = 0xFFBA68C8
    ),
    PC_GAMER_ENTHUSIAST(
        archetypeId = "pc_gamer",
        title = "The Hardcore PC Gamer",
        defaultPatience = 60f,
        avgSpending = 2400,
        preferredDepartment = StoreDepartmentType.COMPUTERS,
        initialQuotes = listOf(
            "Did the morning truck deliver any RTX 50-series GPUs in the cage?",
            "I need an ultrawide OLED gaming monitor with 0.03ms response time.",
            "Do you carry thermal paste with silver diamond microparticles?"
        ),
        revealedIntents = listOf(
            "Building a $3,000 liquid-cooled gaming battle station with RGB lighting.",
            "Looking for high-end mechanical gaming keyboard and 240Hz monitor."
        ),
        avatarEmoji = "🧑‍💻",
        shirtColorHex = 0xFF303F9F
    ),
    IMPATIENT_KAREN(
        archetypeId = "impatient_karen",
        title = "The 'Speak to Manager' Shopper",
        defaultPatience = 40f,
        avgSpending = 800,
        requiresManager = true,
        preferredDepartment = StoreDepartmentType.STORE_PICKUP,
        initialQuotes = listOf(
            "I've been waiting at the front door for 45 seconds already!",
            "Your website said 1-hour in-store pickup, it's been 61 minutes!",
            "I know your regional vice president personally, call the GM right now."
        ),
        revealedIntents = listOf(
            "Demanding a price match against a clearance seller from 2018.",
            "Picked up the wrong online color and wants an immediate manager coupon."
        ),
        avatarEmoji = "👱‍♀️",
        shirtColorHex = 0xFFD81B60
    ),
    SHADY_SHOPLIFTER(
        archetypeId = "shady_shoplifter",
        title = "Suspicious Trench-Coat Browser",
        defaultPatience = 50f,
        avgSpending = 0,
        isShoplifter = true,
        preferredDepartment = StoreDepartmentType.PORTABLE_AUDIO,
        initialQuotes = listOf(
            "Just checking out the AirPods display... don't mind the aluminum lined foil bag.",
            "Are those security cameras in aisle 4 actually hooked up or just dummy lights?",
            "Where do you keep the unlocked $1,200 smartphones without alarm tethers?"
        ),
        revealedIntents = listOf(
            "Attempting to slip three wireless noise-canceling headphones into deep pockets!",
            "Trying to grab an open-box camera lens when the sales rep turns away!"
        ),
        avatarEmoji = "🕵️‍♂️",
        shirtColorHex = 0xFF212121
    ),
    QUICK_PICKUP(
        archetypeId = "quick_pickup",
        title = "In-A-Rush Order Pickup",
        defaultPatience = 55f,
        avgSpending = 220,
        preferredDepartment = StoreDepartmentType.STORE_PICKUP,
        initialQuotes = listOf(
            "Order #98421! My double-parked car has the hazards flashing outside!",
            "Got the notification saying ready for pickup! Just need the barcode scanned.",
            "Quick in and out, I'm late for an anniversary dinner!"
        ),
        revealedIntents = listOf(
            "Picking up a pre-paid smart speaker and wireless charging pad.",
            "Grabbing reserved anniversary wireless earbuds."
        ),
        avatarEmoji = "🏃‍♂️",
        shirtColorHex = 0xFF00897B
    ),
    PRO_PHOTOGRAPHER(
        archetypeId = "pro_photographer",
        title = "The Gearhead Photographer",
        defaultPatience = 70f,
        avgSpending = 3200,
        preferredDepartment = StoreDepartmentType.DIGITAL_IMAGING,
        initialQuotes = listOf(
            "Do you have the 70-200mm f/2.8 GM Mark II lens in stock?",
            "I have a destination wedding shoot tomorrow and my secondary body died.",
            "Are there high-speed V90 UHS-II SD cards in the glass showcase?"
        ),
        revealedIntents = listOf(
            "Purchasing a flagship full-frame mirrorless camera with dual fast lenses.",
            "Buying a 4K aerial camera drone with creator fly-more combo pack."
        ),
        avatarEmoji = "📷",
        shirtColorHex = 0xFFE65100
    ),
    SMART_HOME_DIYER(
        archetypeId = "smart_home_diy",
        title = "The Smart Home Automator",
        defaultPatience = 75f,
        avgSpending = 650,
        preferredDepartment = StoreDepartmentType.SMART_HOME,
        initialQuotes = listOf(
            "Do your smart smart bulbs support Matter and Thread protocol?",
            "I want my porch floodlight to sync with my robot vacuum when door opens.",
            "Which video doorbell works without a monthly cloud subscription?"
        ),
        revealedIntents = listOf(
            "Upgrading entire household to smart mesh Wi-Fi 7 and automated locks.",
            "Buying 6 smart security cameras with local storage hub."
        ),
        avatarEmoji = "🧑‍🔧",
        shirtColorHex = 0xFF00ACC1
    ),
    APPLIANCE_HOMEOWNER(
        archetypeId = "appliance_owner",
        title = "The Kitchen Remodeler",
        defaultPatience = 80f,
        avgSpending = 3800,
        isVIP = true,
        preferredDepartment = StoreDepartmentType.APPLIANCES,
        initialQuotes = listOf(
            "Our refrigerator compressor died this morning with $400 of groceries inside!",
            "We need a French-door fridge that makes craft spherical ice balls.",
            "Looking for an induction cooktop and whisper-quiet dishwasher suite."
        ),
        revealedIntents = listOf(
            "Full 4-piece stainless steel smart appliance package with home delivery.",
            "Ultra-capacity washer & dryer set with steam sanitation cycle."
        ),
        avatarEmoji = "👫",
        shirtColorHex = 0xFFC2185B
    ),
    AUDIOPHILE_PURIST(
        archetypeId = "audiophile_purist",
        title = "The Golden-Ear Audiophile",
        defaultPatience = 65f,
        avgSpending = 1200,
        preferredDepartment = StoreDepartmentType.PORTABLE_AUDIO,
        initialQuotes = listOf(
            "Can I audition these planar magnetic headphones with uncompressed FLAC audio?",
            "Do your high-end Bluetooth DACs support LDAC and aptX Lossless codecs?",
            "I can hear the subtle jitter in compressed standard streaming files."
        ),
        revealedIntents = listOf(
            "Buying flagship audiophile open-back headphones and dedicated desktop amplifier.",
            "Picking up multi-room hi-fi streaming speakers."
        ),
        avatarEmoji = "🎧",
        shirtColorHex = 0xFF6A1B9A
    ),
    LOST_WANDERER(
        archetypeId = "lost_wanderer",
        title = "The Restroom Searcher",
        defaultPatience = 85f,
        avgSpending = 0,
        preferredDepartment = StoreDepartmentType.RESTROOMS,
        initialQuotes = listOf(
            "Hey buddy, is there a public bathroom around here?",
            "Just came in for the air conditioning while waiting for my bus.",
            "Do you guys sell cold soda bottles or snacks?"
        ),
        revealedIntents = listOf(
            "Just needs directions to the back corner restrooms.",
            "Grabbed a $2 candy bar and a bottle of iced tea."
        ),
        avatarEmoji = "🚶‍♂️",
        shirtColorHex = 0xFF757575
    ),
    TECH_INFLUENCER(
        archetypeId = "tech_influencer",
        title = "The Unboxing Streamer",
        defaultPatience = 50f,
        avgSpending = 2900,
        isVIP = true,
        preferredDepartment = StoreDepartmentType.DIGITAL_IMAGING,
        initialQuotes = listOf(
            "I'm live streaming to 200,000 followers, which gimbal stabilizer is top-rated?",
            "Need the newest podcast condenser mic setup and wireless lapels ASAP!",
            "Can you hold the camera box towards my selfie stick while I film the store?"
        ),
        revealedIntents = listOf(
            "Full content creator studio kit: ring lights, pro wireless mics, 4K camera.",
            "Flagship folding phone and ultra-fast external SSD drives."
        ),
        avatarEmoji = "🤳",
        shirtColorHex = 0xFFFF1744
    )
}

data class CustomerInstance(
    val id: String,
    val archetype: CustomerArchetype,
    val currentDialogue: String,
    val revealedIntent: String,
    val targetDepartment: StoreDepartmentType,
    var currentPatience: Float,
    val maxPatience: Float,
    var isGreeted: Boolean = false,
    var isIntentRevealed: Boolean = false,
    var isRadioAssisted: Boolean = false,
    var isCouponApplied: Boolean = false,
    var isManagerAssisted: Boolean = false,
    var isSecurityAlerted: Boolean = false,
    var status: CustomerStatus = CustomerStatus.IN_QUEUE,
    var progressSeconds: Float = 0f,
    val totalServiceRequiredSeconds: Float = 5f,
    val potentialSpending: Int = 100,
    val patienceDecayRate: Float = 1.0f
)

enum class CustomerStatus {
    IN_QUEUE,
    AT_HOST_DESK,
    WALKING_TO_DEPT,
    BEING_SERVED,
    CHECKOUT_PURCHASED,
    LEFT_ANGRY,
    SECURITY_CAUGHT,
    DIRECTED_WRONG
}

data class DepartmentState(
    val departmentType: StoreDepartmentType,
    var staffCount: Int = 1,
    var maxCapacity: Int = 4,
    val currentCustomers: MutableList<CustomerInstance> = mutableListOf(),
    var isRadioRequested: Boolean = false,
    var staffServiceMultiplier: Float = 1.0f
) {
    val occupancy: Int get() = currentCustomers.size
    val isFull: Boolean get() = occupancy >= maxCapacity
}

data class RadioMessage(
    val id: Long = System.currentTimeMillis(),
    val senderName: String,
    val department: StoreDepartmentType,
    val text: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val isUrgent: Boolean = false
)
