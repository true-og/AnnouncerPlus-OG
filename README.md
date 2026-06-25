# AnnouncerPlus-OG

Soft fork of [AnnouncerPlus](https://github.com/jpenilla/AnnouncerPlus) by jmp, maintained by the [TrueOG Network](https://true-og.net) for use on `true-og.net`.

## Differences from upstream

- **LuckPerms instead of Vault** - drops the Vault dependency and talks to LuckPerms 5.5 directly for permission/group lookups.
- **Bundled default configs** - `main.conf`, `first-join.conf`, `join-quit-configs/default.conf`, and `message-configs/demo.conf` are shaded into the JAR and copied out on first run, so a fresh install boots with TrueOG's live config rather than upstream stubs.
- **Join broadcasts visible to everyone** - the permission gate that hid join broadcasts from non-staff has been removed.
- **Join/quit formatting fixes** - corrected MiniMessage formatting in the bundled join/quit configs.
- **Conditional [Unions-OG](https://github.com/true-og/Unions-OG) tag** - PlaceholderAPI preprocessor hides the union tag when a player has no union, instead of rendering an empty bracket.
- **Smoother welcome title** - Fade animation like old TitleManager.

## Upstream links

- [Upstream repo](https://github.com/jpenilla/AnnouncerPlus)
- [Upstream wiki](https://github.com/jpenilla/AnnouncerPlus/wiki)
- [Spigot resource page](https://www.spigotmc.org/resources/announcer-plus.81005/)
