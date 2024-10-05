package se.dansdata.backstage.utilities

import se.dansdata.backstage.list.LinkedList

class StringUtils {
  companion object {
    fun join(source: LinkedList): String {
      return JoinUtils.join(source)
    }

    fun split(source: String): LinkedList {
      return SplitUtils.split(source)
    }
  }
}