# Third-party notices

The original mod and its 1.18.2 backport preserve the source headers and upstream licenses. The main mod's MIT notice and its assets' CC BY-NC-SA 4.0 notice accompany the JAR. Individual components retain their own terms.

| Component | License or notice supplied with the release |
| --- | --- |
| Chess and XQWLight engines | See `chess`, `xqwlight`, and the copyright headers in the corresponding Java sources. |
| GeckoLib-derived and McLib-derived code | See `geckolib` and `mclib`. |
| Reach Entity Attributes 2.2.0 | MIT, Erlend Åmdal. See `reach-entity-attributes.txt`. |
| Nashorn Core 15.4 | See `nashorn` and `nashorn-additional`, including the Classpath exception. |
| JavaZoom JLayer 1.0.1.4, MP3SPI 1.9.5.4 and Tritonus Share 0.3.7.4 | See `lgpl-2.0.txt` and the notices in each source archive. |
| Concentus 1.0.2 and Vorbis Java 0.8 | See `concentus`, `vorbis-java-core`, and the nested library notices. |
| SnakeYAML 2.4, Apache Commons Math 3.6.1 and Apache BCEL 6.6.1 | See `apache-2.0.txt` and the notices in the libraries. |
| JOML 1.10.5 | MIT, see the JOML JAR's `META-INF/LICENSE`. |

The accompanying third-party source archive contains the matching library source JARs. The backport source archive contains the mod, its integrated chess engines, assets and build scripts. Java library coordinates are pinned in `build.gradle`. The versions used only for compiling optional mod integrations are listed in `gradle/backport-deps.json`.
