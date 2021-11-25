package com.example.in2000_prosjektoppgave.util


/**
 * Object used to hold on string names for object types used by the Drifty server. The object
 * translates on screen names to server type names.
 */
object NameParser {

    // Different object names for people
    private val people: List<String> = listOf(
            "Person-in-water (PIW), unknown state (mean values)",
            ">PIW, sitting, PFD type I or II",
            ">PIW, vertical PFD type III conscious",
            ">PIW, survival suit (face up)",
            ">PIW, scuba suit (face up)",
            ">PIW, deceased (face down)",
            "Sea Kayak with person on aft deck",
            "Surf board with person"
    )

    // Different object names for misc small crafts
    private val small: List<String> = listOf(
            "Windsurfer with mast and sail in water",
            "Sunfish sailing dingy  -  Bare-masted, rudder missing"
    )

    // Different object names for sailboats
    private val sailboat: List<String> = listOf(
            "Sailboat Mono-hull (Average)",
            ">Sailboat Mono-hull (Dismasted, Average)",
            ">Sailboat Mono-hull (Bare-masted,  Average)"
    )

    // Different object names for small boats
    private val smallBoats: List<String> = listOf(
            "Skiff, V-hull",
            "Skiffs, swamped and capsized",
            "Fishing vessel, general (mean values)",
            "Sport fisher, center console (*2), open cockpit"
    )

    // Different object names for life rafts
    private val liftRafts: List<String> = listOf(
            "Life raft, deep ballast (DB) system, general, unknown capacity and loading (mean values)",
            ">4-14 person capacity, deep ballast system, canopy (average)",
            ">15-50 person capacity, deep ballast system, canopy, general (mean values)",
            ">Life-raft, no ballast system, no canopy, no drogue",
            ">Life-raft, no ballast system, no canopy, with drogue"
    )

    // All available objects
    private val allLevels: List<List<String>> = listOf(
            people,
            small,
            sailboat,
            smallBoats,
            liftRafts
    )


    /**
     * Function that returns the name used by Drifty for objects in simulations.
     *
     * @param id1 Id of top level chip.
     * @param id2 Id of sub level chip, can be null.
     */
    fun findNameAlias(id1: Int, id2: Int) = allLevels[id1][id2]

}