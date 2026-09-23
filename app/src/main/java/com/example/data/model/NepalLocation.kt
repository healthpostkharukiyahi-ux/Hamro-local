package com.example.data.model

data class Province(
    val id: Int,
    val nameEn: String,
    val nameNe: String,
    val districts: List<District>
)

data class District(
    val nameEn: String,
    val nameNe: String,
    val municipalities: List<String>
)

object NepalLocations {
    val provinces = listOf(
        Province(
            id = 1,
            nameEn = "Koshi Province",
            nameNe = "कोशी प्रदेश",
            districts = listOf(
                District("Morang", "मोरङ", listOf("Biratnagar Metropolitian", "Belbari", "Pathari Shanischare", "Sundarharaicha", "Urlabari", "Ratuwamai")),
                District("Sunsari", "सुनसरी", listOf("Dharan Sub-Metropolitan", "Itahari Sub-Metropolitan", "Inaruwa", "Duhabi", "Ramdhuni")),
                District("Jhapa", "झापा", listOf("Birtamod", "Damak", "Bhadrapur", "Mechinagar", "Kankai", "Shivasatakshi"))
            )
        ),
        Province(
            id = 2,
            nameEn = "Madhesh Province",
            nameNe = "मधेश प्रदेश",
            districts = listOf(
                District("Dhanusha", "धनुषा", listOf("Janakpur Sub-Metropolitan", "Dhanushadham", "Kshireshwarnath", "Mithila")),
                District("Parsa", "पर्सा", listOf("Birgunj Metropolitan", "Pokhariya", "Bahudaramai")),
                District("Siraha", "सिराहा", listOf("Lahan", "Siraha", "Golbazar", "Mirchaiya"))
            )
        ),
        Province(
            id = 3,
            nameEn = "Bagmati Province",
            nameNe = "बागमती प्रदेश",
            districts = listOf(
                District("Kathmandu", "काठमाडौं", listOf("Kathmandu Metropolitan", "Kirtipur", "Budhanilkantha", "Tokha", "Chandragiri", "Nagarjun", "Tarakeshwar", "Gokarneshwar")),
                District("Lalitpur", "ललितपुर", listOf("Lalitpur Metropolitan", "Mahalaxmi", "Godawari")),
                District("Bhaktapur", "भक्तपुर", listOf("Bhaktapur", "Madhyapur Thimi", "Suryabinayak", "Changunarayan")),
                District("Chitwan", "चितवन", listOf("Bharatpur Metropolitan", "Ratnanagar", "Khairahani", "Rapti", "Kalika", "Madi")),
                District("Makwanpur", "मकवानपुर", listOf("Hetauda Sub-Metropolitan", "Thaha"))
            )
        ),
        Province(
            id = 4,
            nameEn = "Gandaki Province",
            nameNe = "गण्डकी प्रदेश",
            districts = listOf(
                District("Kaski", "कास्की", listOf("Pokhara Metropolitan", "Annapurna", "Machhapuchhre", "Madi", "Rupa")),
                District("Tanahun", "तनहुँ", listOf("Byas", "Shuklagandaki", "Bhimad", "Bhanu")),
                District("Gorkha", "गोरखा", listOf("Gorkha", "Palungtar", "Barpak Sulikot"))
            )
        ),
        Province(
            id = 5,
            nameEn = "Lumbini Province",
            nameNe = "लुम्बिनी प्रदेश",
            districts = listOf(
                District("Rupandehi", "रुपन्देही", listOf("Butwal Sub-Metropolitan", "Siddharthanagar (Bhairahawa)", "Tilottama", "Devdaha", "Sainamaina")),
                District("Banke", "बाँके", listOf("Nepalgunj Sub-Metropolitan", "Kohalpur")),
                District("Dang", "दाङ", listOf("Ghorahi Sub-Metropolitan", "Tulsipur Sub-Metropolitan", "Lamahi"))
            )
        ),
        Province(
            id = 6,
            nameEn = "Karnali Province",
            nameNe = "कर्णाली प्रदेश",
            districts = listOf(
                District("Surkhet", "सुर्खेत", listOf("Birendranagar", "Gurbhakot", "Bheriganga", "Panchapuri")),
                District("Jumla", "जुम्ला", listOf("Chandannath", "Tatopani", "Sinja"))
            )
        ),
        Province(
            id = 7,
            nameEn = "Sudurpashchim Province",
            nameNe = "सुदूरपश्चिम प्रदेश",
            districts = listOf(
                District("Kailali", "कैलाली", listOf("Dhangadhi Sub-Metropolitan", "Tikapur", "Godawari", "Lamki Chuha")),
                District("Kanchanpur", "कञ्चनपुर", listOf("Bhimdatta (Mahendranagar)", "Bedkot", "Shuklaphanta", "Krishnapur"))
            )
        )
    )

    val popularLocations = listOf(
        "काठमाडौं (Kathmandu)",
        "ललितपुर (Lalitpur)",
        "भक्तपुर (Bhaktapur)",
        "पोखरा (Pokhara)",
        "विराटनगर (Biratnagar)",
        "भरतपुर (Bharatpur)",
        "बुटवल (Butwal)",
        "धरान (Dharan)",
        "नेपालगञ्ज (Nepalgunj)",
        "धनगढी (Dhangadhi)"
    )
}
