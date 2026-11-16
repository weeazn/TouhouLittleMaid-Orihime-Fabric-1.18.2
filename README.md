# Touhou Little Maid: Orihime for Fabric 1.18.2

## English

This unofficial backport brings **Touhou Little Maid: Orihime 0.8.3** from Minecraft 1.20.1 to **Minecraft 1.18.2 on Fabric**. Tame a maid, equip her with armor and weapons, and give her jobs such as fighting, farming or shearing sheep. Maids have their own inventory and support custom skins and voice packs. The backport adapts combat targeting, crossbows, beds and multi-block crop harvesting to 1.18.2.

The original mod is by **tartaric_acid and contributors**. The Fabric port on which this project is based is by **Sh1roCu and contributors**. [Original Touhou Little Maid](https://github.com/TartaricAcid/TouhouLittleMaid) · [Orihime source used for this backport](https://github.com/Sh1roCu/TouhouLittleMaid-Orihime/tree/a1c28d57178c980da8e531ff0e09197ffe3cd6d2)

### Installation

Use Minecraft Java Edition **1.18.2**, **Java 17**, and Fabric Loader (**0.16.13** was tested). Download `touhoulittlemaid-fabric-0.8.3-backport.9+mc1.18.2.jar` from Releases. Put the JAR and the four dependencies below in `mods/` on both client and server. When upgrading, replace the previous maid JAR. [Download the release](https://github.com/weeazn/TouhouLittleMaid-Orihime-Fabric-1.18.2/releases/latest)

| Required mod | Tested version |
| --- | --- |
| Fabric API | [0.77.0+1.18.2](https://modrinth.com/mod/fabric-api/versions?g=1.18.2) |
| Forge Config API Port for Fabric | [3.2.4](https://modrinth.com/mod/forge-config-api-port/version/XGKEYlsw) |
| Cardinal Components API | [4.2.0](https://modrinth.com/mod/cardinal-components-api/version/4.2.0) |
| Porting Lib | [1.2.1451-beta+1.18.2](https://modrinth.com/mod/porting_lib/version/WtG6ZFxI) |

For in-game settings, install Mod Menu and Cloth Config on the client. Patchouli provides the optional in-game guide. The common settings are in `config/touhou_little_maid-common.toml`, and each world's server settings are in its `serverconfig/touhou_little_maid-server.toml`. Reach Entity Attributes is bundled. [Mod Menu 3.2.3](https://modrinth.com/mod/modmenu/version/3.2.3) · [Cloth Config 6.5.102](https://modrinth.com/mod/cloth-config/version/BLMp2TRt) · [Patchouli for Fabric 1.18.2](https://modrinth.com/mod/patchouli/versions?g=1.18.2)

### Tested modpack

Testing used a local **Infinity-Reborn v3.4** instance. Its 352 other mod JARs were matched by SHA-256 against the test copy. In backport.9 we looted a tamed maid's grave, saved her film in a shrine, quit the game, and reopened the world. Both the shrine and the six-pillar altar restored the same maid with her original UUID, owner, name, skin and favorability. We also checked a film created in backport.8 and the tooltip shown when a skin pack is missing. [Modpack repository](https://github.com/moyuguguji/infinity-reborn) · [Modpack download page](https://bbsmc.net/modpack/if2) · [Test results](docs/verification.json)

Earlier tests covered modded weapons and enchantments, following, beds, full-inventory storage and multi-block crops from Farmer's Delight. Results cover the tested configurations. If you encounter a bug, include the mod and loader versions, steps to reproduce it and the relevant crash trace. [Report an issue](https://github.com/weeazn/TouhouLittleMaid-Orihime-Fabric-1.18.2/issues)

### Build and license

With JDK 17 and Python 3.9 or later, run the following from the repository root. The first build downloads pinned development dependencies. On Windows, use `.\gradlew.bat build` for the second command. The playable JAR is written to `build/libs/`.

```sh
python tools/prepare_dev.py
./gradlew build
```

The main mod code and this backport's changes to it are released under the **MIT License**. The mod's artwork, models, sounds and other covered assets retain **CC BY-NC-SA 4.0**. Integrated engines and bundled libraries retain their own notices. Matching third-party sources are attached to the release. [MIT license](LICENSE-MIT) · [Asset license](LICENSE-CC) · [Third-party notices](src/main/resources/licenses/THIRD-PARTY.md)

## 中文

这是 **车万女仆 Orihime 0.8.3** 的非官方回移版，将 Minecraft 1.20.1 的 Fabric 移植带到 **Minecraft 1.18.2 Fabric**。你可以驯服女仆，给她穿上盔甲、装备武器，让她战斗、种田或剪羊毛。女仆拥有独立物品栏，可以更换皮肤和语音包。本项目针对 1.18.2 适配了索敌、弩、专属床和多格作物收获等行为。

原作由 **tartaric_acid 和贡献者**开发，作为回移基础的 Orihime Fabric 移植由 **Sh1roCu 和贡献者**开发。[车万女仆原作](https://github.com/TartaricAcid/TouhouLittleMaid) · [本次回移采用的 Orihime 源码](https://github.com/Sh1roCu/TouhouLittleMaid-Orihime/tree/a1c28d57178c980da8e531ff0e09197ffe3cd6d2)

### 安装

使用 Minecraft Java 版 **1.18.2**、**Java 17** 和 Fabric Loader，实测加载器版本为 **0.16.13**。从发布页下载 `touhoulittlemaid-fabric-0.8.3-backport.9+mc1.18.2.jar`，将它和下列四个前置放入 `mods/`。联机时客户端和服务端都要安装。升级时请替换原来的女仆 JAR。[下载发布版](https://github.com/weeazn/TouhouLittleMaid-Orihime-Fabric-1.18.2/releases/latest)

| 必需前置 | 实测版本 |
| --- | --- |
| Fabric API | [0.77.0+1.18.2](https://modrinth.com/mod/fabric-api/versions?g=1.18.2) |
| Forge Config API Port（Fabric 版） | [3.2.4](https://modrinth.com/mod/forge-config-api-port/version/XGKEYlsw) |
| Cardinal Components API | [4.2.0](https://modrinth.com/mod/cardinal-components-api/version/4.2.0) |
| Porting Lib | [1.2.1451-beta+1.18.2](https://modrinth.com/mod/porting_lib/version/WtG6ZFxI) |

客户端可选装 Mod Menu 和 Cloth Config，在模组菜单中调整配置。游戏内手册需要 Patchouli。通用配置在 `config/touhou_little_maid-common.toml`，单个世界的服务端配置在该存档的 `serverconfig/touhou_little_maid-server.toml`。攻击距离库 Reach Entity Attributes 已随女仆 JAR 提供。[Mod Menu 3.2.3](https://modrinth.com/mod/modmenu/version/3.2.3) · [Cloth Config 6.5.102](https://modrinth.com/mod/cloth-config/version/BLMp2TRt) · [Patchouli 1.18.2 Fabric](https://modrinth.com/mod/patchouli/versions?g=1.18.2)

### 测试整合包

测试使用 **《无限：重生》Infinity-Reborn v3.4** 的本地实例。测试副本中的其他 352 个模组 JAR 均与该实例逐一核对过 SHA-256。backport.9 的实测流程包括从墓碑取回胶片、将胶片存入神龛、退出并重开游戏，再通过神龛和六柱祭坛分别复活女仆。复活后的 UUID、主人、名字、皮肤和好感度均与原记录相同。另验证了旧版 backport.8 胶片的升级读取，以及缺少皮肤包时的文字提示。[整合包作者仓库](https://github.com/moyuguguji/infinity-reborn) · [整合包下载页](https://bbsmc.net/modpack/if2) · [实测记录](docs/verification.json)

此前的整合包测试还覆盖了第三方武器和附魔、跟随、专属床、满包转存及农夫乐事的多格作物，结果对应实际测试过的配置。反馈问题时，请附女仆模组和加载器版本、复现步骤，以及相关崩溃堆栈。[提交问题](https://github.com/weeazn/TouhouLittleMaid-Orihime-Fabric-1.18.2/issues)

### 源码构建与许可

安装 JDK 17 和 Python 3.9 或更高版本，在仓库根目录执行下列命令。首次构建需要联网下载已锁定版本的开发依赖。Windows 下第二条命令使用 `.\gradlew.bat build`，可直接游玩的 JAR 生成在 `build/libs/`。

```sh
python tools/prepare_dev.py
./gradlew build
```

主体代码和本项目对它的回移改动采用 **MIT 许可证**。模组的美术、模型、声音等受许可覆盖的资源沿用 **CC BY-NC-SA 4.0（署名－非商业性使用－相同方式共享）**。内置引擎和依赖库保留各自许可，发行版附件提供对应的第三方源码。[MIT 代码许可](LICENSE-MIT) · [资源许可](LICENSE-CC) · [第三方组件声明](src/main/resources/licenses/THIRD-PARTY.md)
