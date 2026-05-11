/*
 * This file is part of AnnouncerPlus, licensed under the MIT License.
 *
 * Copyright (c) 2020-2024 Jason Penilla
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package xyz.jpenilla.announcerplus.compatibility

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import java.util.regex.Matcher
import java.util.regex.Pattern

class PlaceholderAPIMiniMessagePreprocessor(private val miniMessage: MiniMessage) {
  fun process(player: Player, input: String): String =
    this.process(
      PlaceholderAPI.getPlaceholderPattern(),
      stripSimpleClansNoneBrackets(input) { PlaceholderAPI.setPlaceholders(player, it) }
    ) { PlaceholderAPI.setPlaceholders(player, it) }

  fun process(one: Player, two: Player, input: String): String =
    this.process(
      PlaceholderAPI.getPlaceholderPattern(),
      stripSimpleClansNoneBrackets(input) { PlaceholderAPI.setPlaceholders(one, it) }
    ) { PlaceholderAPI.setPlaceholders(one, PlaceholderAPI.setRelationalPlaceholders(one, two, it)) }

  private fun stripSimpleClansNoneBrackets(input: String, resolver: (String) -> String): String {
    if (!input.contains(SIMPLECLANS_UNION_COLOR_TAG)) return input
    val resolved = LEGACY_COLOR_CODE_PATTERN.matcher(resolver(SIMPLECLANS_UNION_COLOR_TAG)).replaceAll("").trim()
    if (!resolved.equals("None", ignoreCase = true)) return input
    return SIMPLECLANS_NONE_BRACKET_PATTERN.matcher(input).replaceAll("")
  }

  private fun process(
    pattern: Pattern,
    input: String,
    placeholderResolver: (String) -> String,
  ): String {
    val matcher = pattern.matcher(input)
    val buffer = StringBuffer()
    while (matcher.find()) {
      val match = matcher.group()
      val replaced = placeholderResolver(match)
      if (match == replaced || !LEGACY_CODE_PATTERN.matcher(replaced).find()) {
        matcher.appendReplacement(buffer, Matcher.quoteReplacement(replaced))
      } else {
        val normalized = AMPERSAND_CODE_PATTERN.matcher(replaced)
          .replaceAll(LegacyComponentSerializer.SECTION_CHAR.toString() + "$1")
        matcher.appendReplacement(buffer, Matcher.quoteReplacement(miniMessage.serialize(LegacyComponentSerializer.legacySection().deserialize(normalized))))
      }
    }
    matcher.appendTail(buffer)
    return buffer.toString()
  }

  companion object {
    private const val SIMPLECLANS_UNION_COLOR_TAG = "%simpleclans_union_color_tag%"
    private val SIMPLECLANS_NONE_BRACKET_PATTERN: Pattern =
      Pattern.compile("(?:<[^>]+>)*\\[(?:<[^>]+>)*\\Q$SIMPLECLANS_UNION_COLOR_TAG\\E(?:<[^>]+>)*](?:<[^>]+>)*\\s?")
    private val LEGACY_CODE_PATTERN: Pattern =
      Pattern.compile("[&§][0-9a-fA-Fk-oK-OrRxX]")
    private val AMPERSAND_CODE_PATTERN: Pattern =
      Pattern.compile("&([0-9a-fA-Fk-oK-OrRxX])")
    private val LEGACY_COLOR_CODE_PATTERN: Pattern =
      Pattern.compile("[&§][0-9a-fA-F]")
  }
}
