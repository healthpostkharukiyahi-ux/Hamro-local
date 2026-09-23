package com.example.ui.util

object HamroStrings {
    fun get(key: String, lang: String = "ne"): String {
        val isNe = lang == "ne"
        return when (key) {
            "app_name" -> if (isNe) "हाम्रो लोकल" else "HAMRO LOCAL"
            "tagline" -> if (isNe) "किन्नुहोस् • बेच्नुहोस् • भाडामा दिनुहोस् • सेवा बुक गर्नुहोस्" else "Buy • Sell • Rent • Book Services"
            "nav_home" -> if (isNe) "गृहपृष्ठ" else "Home"
            "nav_explore" -> if (isNe) "खोज्नुहोस्" else "Explore"
            "nav_post" -> if (isNe) "थप्नुहोस्" else "Post"
            "nav_messages" -> if (isNe) "कुराकानी" else "Chat"
            "nav_profile" -> if (isNe) "प्रोफाइल" else "Profile"
            
            "cat_marketplace" -> if (isNe) "बजार" else "Marketplace"
            "cat_rent" -> if (isNe) "भाडा" else "Rent"
            "cat_services" -> if (isNe) "सेवा तथा कामदार" else "Services"
            "cat_vehicles" -> if (isNe) "सवारी साधन" else "Vehicles"
            "cat_agri" -> if (isNe) "कृषि उपज" else "Agriculture"
            "cat_other" -> if (isNe) "अन्य" else "Other"

            "featured_listings" -> if (isNe) "विशेष आकर्षण (Featured)" else "Featured Listings"
            "latest_products" -> if (isNe) "नयाँ सामानहरू (Latest Products)" else "Latest Products"
            "houses_for_rent" -> if (isNe) "कोठा तथा घर भाडा (Rent)" else "Houses/Rooms for Rent"
            "popular_services" -> if (isNe) "लोकप्रिय सेवाहरू (Services)" else "Popular Services"
            "nearby_listings" -> if (isNe) "तपाईंको नजिक (Nearby)" else "Nearby in Your Area"
            
            "call_now" -> if (isNe) "फोन गर्नुहोस्" else "Call Now"
            "chat_now" -> if (isNe) "च्याट गर्नुहोस्" else "Chat Seller"
            "buy_now" -> if (isNe) "अर्डर गर्नुहोस्" else "Buy / Order"
            "make_offer" -> if (isNe) "मूल्य प्रस्ताव (Offer)" else "Make Offer"
            "book_service" -> if (isNe) "सेवा बुक गर्नुहोस्" else "Book Service"
            "send_rental_req" -> if (isNe) "भाडा अनुरोध पठाउनुहोस्" else "Request Rental"
            
            "cod" -> if (isNe) "सामान पाएपछि नगद (Cash on Delivery)" else "Cash on Delivery (COD)"
            "esewa" -> if (isNe) "ईसेवा (eSewa)" else "eSewa Mobile Wallet"
            "khalti" -> if (isNe) "खल्ती (Khalti)" else "Khalti Digital Wallet"
            "fonepay" -> if (isNe) "फोनपे (Fonepay QR)" else "Fonepay Direct QR"
            "bank_transfer" -> if (isNe) "बैंक ट्रान्सफर (Bank Transfer)" else "Bank Transfer"

            "verified" -> if (isNe) "प्रमाणित" else "Verified"
            "negotiable" -> if (isNe) "मिल्ने (Negotiable)" else "Negotiable"
            "fixed_price" -> if (isNe) "निश्चित मूल्य" else "Fixed Price"
            
            else -> key
        }
    }

    fun formatNpr(amount: Long, lang: String = "ne"): String {
        return if (lang == "ne") {
            "रू ${"%,d".format(amount)}"
        } else {
            "NPR ${"%,d".format(amount)}"
        }
    }
}
