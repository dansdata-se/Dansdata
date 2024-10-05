package se.dansdata.backstage.utilities

import se.dansdata.backstage.list.LinkedList

@Suppress("UtilityClassWithPublicConstructor")
class JoinUtils {
    companion object {
        fun join(source: LinkedList): String {
            val result = StringBuilder()
            for (i in 0 until source.size()) {
                if (result.isNotEmpty()) {
                    result.append(" ")
                }
                result.append(source.get(i))
            }

            return result.toString()
        }
    }
}
