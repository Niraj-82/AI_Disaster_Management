package com.example.aidm

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
// --- Single, Unified FakeRepo Class ---

class FakeRepo {

    // Mutex for thread-safe operations
    private val mutex = Mutex()

    // --- Shelter Data ---

    private val shelters = listOf(
        Shelter("1", "North Community Center", 19.0761, 72.8778, 100, 85, "123 Maple St"),
        Shelter("2", "Southside High School Gym", 19.0762, 72.8779, 250, 120, "456 Oak Ave"),
        Shelter("3", "West End Church Hall", 19.0763, 72.8780, 50, 15, "789 Pine Ln"),
        Shelter("4", "Downtown Convention Center", 19.0764, 72.8781, 1000, 950, "101 Main Blvd")
    )

    /**
     * Returns the complete list of all shelters.
     */
    fun getShelterList(): List<Shelter> {
        return shelters
    }

    /**
     * Finds and returns a single shelter by its ID.
     */
    fun getShelter(id: String): Shelter? {
        return shelters.find { it.id == id }
    }


    // --- Incident Data ---

    private val _incidents = MutableStateFlow<List<IncidentData>>(emptyList())

    /**
     * Publicly exposes the flow of incidents as a read-only StateFlow.
     */
    val incidents: StateFlow<List<IncidentData>> = _incidents.asStateFlow()

    /**
     * Simulates submitting a new incident.
     */
    suspend fun submitIncident(type: String, description: String, lat: Double, lng: Double): Boolean {
        delay(500) // Simulate network delay
        val newIncident = IncidentData(
            id = java.util.UUID.randomUUID().toString(), // Generate a unique ID
            type = type,
            description = description,
            location = "Unknown Location", // Placeholder
            latitude = lat,
            longitude = lng,
            timestamp = System.currentTimeMillis(), // Current time
            status = "Reported", // Default status
            reportedBy = "FakeRepoUser", // Placeholder or null if appropriate
            imageUrl = null // Default to no image
        )
        mutex.withLock {
            _incidents.value = _incidents.value + newIncident
        }
        return true
    }

    /**
     * Provides a direct flow to the list of incidents.
     */
    fun getIncidentsFlow(): StateFlow<List<IncidentData>> {
        return incidents
    }


    // --- Announcement Data ---

    /**
     * Simulates fetching announcements, which are derived from the reported incidents.
     */
    suspend fun getAnnouncements(): List<String> {
        delay(200) // Simulate delay
        return _incidents.value.map { incident ->
            "New Incident Reported: ${incident.type} - ${incident.description.take(50)}${if (incident.description.length > 50) "..." else ""}"
        }.reversed()
    }


    // --- First Aid Data ---

    /**
     * Simulates fetching first aid topics and their steps.
     */
    suspend fun getFirstAidTopics(): Map<String, List<String>> {
        delay(800) // Simulate delay
        return mapOf(
            "Cuts and Scrapes" to listOf(
                "1. Stop the bleeding: Apply gentle pressure with a clean cloth or bandage.",
                "2. Clean the wound: Rinse the wound with clear water. Keep soap out of the wound.",
                "3. Apply an antibiotic: Apply a thin layer of an antibiotic ointment (like Neosporin or Polysporin) to help keep the surface moist and prevent infection.",
                "4. Cover the wound: Apply a bandage, rolled gauze, or gauze held in place with paper tape.",
                "5. Change the dressing: Do this at least daily or whenever the bandage becomes wet or dirty."
            ),
            "Burns (Minor, First-Degree)" to listOf(
                "1. Cool the burn: Immediately hold the burned area under cool (not cold) running water or apply a cool, wet compress until the pain eases.",
                "2. Remove rings or other tight items from the burned area. Try to do this quickly and gently, before the area swells.",
                "3. Don't break blisters: Fluid-filled blisters protect against infection. If a blister breaks, clean the area with water and apply an antibiotic ointment.",
                "4. Apply lotion: Once a burn is completely cooled, apply a lotion, such as one that contains aloe vera or a moisturizer.",
                "5. Bandage the burn: Cover the burn with a sterile gauze bandage (not fluffy cotton). Wrap it loosely to avoid putting pressure on burned skin.",
                "6. If needed, take an over-the-counter pain reliever, such as ibuprofen (Advil, Motrin IB, others), naproxen sodium (Aleve), or acetaminophen (Tylenol, others)."
            ),
            "Sprains (e.g., Ankle)" to listOf(
                "Follow the R.I.C.E. approach:",
                "1. Rest: Avoid activities that cause pain, swelling or discomfort.",
                "2. Ice: Use an ice pack or ice slush bath immediately for 15 to 20 minutes and repeat every two to three hours while you're awake for the first few days.",
                "3. Compression: To help stop swelling, compress the ankle with an elastic bandage until the swelling stops. Don't hinder circulation by wrapping too tightly.",
                "4. Elevation: To reduce swelling, elevate your ankle above the level of your heart, especially at night."
            ),
            "Choking (Adult - Conscious)" to listOf(
                "1. Ask: 'Are you choking? Can you speak or cough?' Do not interfere if they can speak, cough, or breathe.",
                "2. If the person cannot speak, cough, or breathe: Lean the person forward slightly and give 5 back blows between their shoulder blades with the heel of your hand.",
                "3. Perform 5 abdominal thrusts (Heimlich maneuver): Stand behind the person. Wrap your arms around their waist. Make a fist with one hand. Place the thumb side of your fist just above the person's navel and well below the breastbone. Grasp your fist with your other hand. Quickly thrust inward and upward as if trying to lift the person.",
                "4. Repeat: Continue cycles of 5 back blows and 5 abdominal thrusts until the object is dislodged or the person becomes unconscious.",
                "5. If person becomes unconscious: Call for emergency help immediately and start CPR if you're trained."
            ),
            "Nosebleeds" to listOf(
                "1. Sit upright and lean forward: This will reduce blood pressure in the veins of your nose and discourage swallowing blood.",
                "2. Pinch your nose: Pinch all parts of the nose (nostrils) closed with your thumb and index finger. Breathe through your mouth.",
                "3. Continue to pinch for 10 to 15 minutes: This time frame allows for blood clotting.",
                "4. If bleeding continues after 10 to 15 minutes of pinching, repeat pinching for another 10 to 15 minutes.",
                "5. To prevent re-bleeding, don't pick or blow your nose and don't bend down for several hours."
            )
        )
    }
}

private val appRepository = FakeRepo()

fun getRepository(): FakeRepo {
    return appRepository
}