# KMHomes

A complete Homes Bukkit plugin using KotlinBukkitAPI.

Speedcode writing this plugin: [https://youtu.be/rqSz6XhC6Tw](https://youtu.be/rqSz6XhC6Tw)

## Commands

| Command | Permission |
| --- | --- |
| home | kmhomes.cmd.home |
| sethome | kmhomes.cmd.sethome |
| homes | kmhomes.cmd.homes |
| delhome | kmhomes.cmd.delhome |
| visit | kmhomes.cmd.visit |
| makepublic | kmhomes.cmd.makepublic |

## Limit homes by permission

You can limit the amount of homes that a player can have using permissions.

First you should create the limit profiles in the `config.yml` and then, set this permission
to your players group.

`kmhomes.limit.profile`

By default the plugin have 2 profiles in the `config.yml`:

```ymal
limit:
  default: 5
  vip: 25
```

