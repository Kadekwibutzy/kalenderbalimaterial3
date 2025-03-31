package com.itzkazuri.kalenderbali.utils

import java.util.*

/**
 * SakaCalendarHelper is a utility class for calculating important dates in the Balinese Saka calendar.
 * This includes Nyepi, Saraswati, Siwaratri, Pagerwesi, Full Moon (Purnama), and New Moon (Tilem).
 */
object SakaCalendarHelper {
    private const val FULL_MOON = "Purnama"
    private const val NEW_MOON = "Tilem"
    private const val NYEPI = "Hari Raya Nyepi"
    private const val SARASWATI = "Hari Raya Saraswati"
    private const val SIWARATRI = "Hari Suci Siwaratri"
    private const val PAGERWESI = "Hari Raya Pagerwesi"

    /**
     * Retrieves a list of important Balinese Hindu holidays for a given year.
     * The list includes Nyepi, Siwaratri, Saraswati, Pagerwesi, Full Moon, and New Moon.
     */
    fun getImportantDates(year: Int): List<Pair<Calendar, String>> {
        return buildList {
            addAll(getPurnamaTilem(year))
            add(calculateNyepi(year) to NYEPI)
            calculateSiwaratri(year)?.let { add(it to SIWARATRI) }
            addAll(getAllSaraswatiInYear(year))
            addAll(getAllPagerwesiInYear(year))
        }.sortedBy { it.first.timeInMillis }
    }

    /**
     * Calculates the Full Moon (Purnama) and New Moon (Tilem) dates for a given year.
     */
    private fun getPurnamaTilem(year: Int): List<Pair<Calendar, String>> {
        return buildList {
            val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }
            val endDate = Calendar.getInstance().apply { set(year, Calendar.DECEMBER, 31) }

            while (calendar <= endDate) {
                val saka = createSakaCalendar(calendar)
                val sasihName = getSasihName(saka.getSakaCalendar(SakaCalendar.NO_SASIH))
                when {
                    saka.getSakaCalendar(SakaCalendar.PENANGGAL) == 15 && !saka.getSakaCalendarStatus(SakaCalendar.IS_PANGELONG) ->
                        add(calendar.clone() as Calendar to "$FULL_MOON $sasihName")
                    saka.getSakaCalendar(SakaCalendar.PENANGGAL) == 15 && saka.getSakaCalendarStatus(SakaCalendar.IS_PANGELONG) ->
                        add(calendar.clone() as Calendar to "$NEW_MOON $sasihName")
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    /**
     * Determines the date of Nyepi (Balinese New Year) based on the Saka calendar.
     */
    fun calculateNyepi(year: Int): Calendar {
        val calendar = Calendar.getInstance().apply { set(year, Calendar.MARCH, 1) }
        while (true) {
            val saka = createSakaCalendar(calendar)
            if (saka.getSakaCalendar(SakaCalendar.NO_SASIH) == 10 && saka.getSakaCalendar(SakaCalendar.PENANGGAL) == 1) {
                return calendar
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    /**
     * Determines the date of Siwaratri, a sacred night dedicated to Lord Shiva.
     */
    fun calculateSiwaratri(year: Int): Calendar? {
        val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }
        while (true) {
            val saka = createSakaCalendar(calendar)
            if (saka.getSakaCalendar(SakaCalendar.NO_SASIH) == 7 && saka.getSakaCalendar(SakaCalendar.PENANGGAL) == 14) {
                return calendar.clone() as Calendar
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    /**
     * Retrieves all Saraswati days (celebrating the Goddess of Knowledge) in a given year.
     */
    fun getAllSaraswatiInYear(year: Int): List<Pair<Calendar, String>> {
        val result = mutableListOf<Pair<Calendar, String>>()
        val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }

        while (calendar.get(Calendar.YEAR) == year) {
            val saka = createSakaCalendar(calendar)
            if (saka.getWuku(SakaCalendar.NO_WUKU) == 30 &&
                saka.getPancawara(SakaCalendar.NO_PANCAWARA) == 1 &&
                calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
            ) {
                result.add(calendar.clone() as Calendar to SARASWATI)
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return result
    }

    /**
     * Retrieves all Pagerwesi days (a day for strengthening the mind and spirit) in a given year.
     */
    fun getAllPagerwesiInYear(year: Int): List<Pair<Calendar, String>> {
        val result = mutableListOf<Pair<Calendar, String>>()
        val calendar = Calendar.getInstance().apply { set(year, Calendar.JANUARY, 1) }

        while (calendar.get(Calendar.YEAR) == year) {
            val saka = createSakaCalendar(calendar)
            val wuku = saka.getWuku(SakaCalendar.NO_WUKU)
            val pancawara = saka.getPancawara(SakaCalendar.NO_PANCAWARA)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            if (wuku == 1 && pancawara == 5 && dayOfWeek == Calendar.WEDNESDAY) {
                result.add(calendar.clone() as Calendar to PAGERWESI)
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return result
    }

    /**
     * Converts Saka month numbers into their corresponding names.
     */
    private fun getSasihName(sasihNumber: Int): String {
        return listOf("Kasa", "Karo", "Katiga", "Kapat", "Kalima", "Kanem", "Kapitu", "Kawolu", "Kasanga", "Kadasa", "Destha", "Sadha")[sasihNumber - 1]
    }

    /**
     * Creates a SakaCalendar instance from a given Gregorian date.
     */
    fun createSakaCalendar(calendar: Calendar): SakaCalendar {
        return SakaCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }
}
