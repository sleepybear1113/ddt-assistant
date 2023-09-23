# ddt-assistant
一个用 Java 写的弹弹堂经典怀旧服的开源工具。

替代按键精灵操蛋的语法，转向 Java 怀抱。主要使用 Jacob 来调用 com 组件的大漠插件来实现对游戏窗口的后台操作。
# 简介
使用说明参见 Gitee 平台的文档：[ddt-assistant-manual](https://gitee.com/sleepybear1113/ddt-assistant-manual)

相关问题请加群询问。QQ 群：879596615。
# 本地构建运行
## 准备 Java 相关环境
建议使用 JetBrains IntelliJ IDEA Ultimate 打开本项目进行构建和运行。

本项目需要使用 Java 17 x86 作为构建运行的环境。由于官方没有高于 8 的 32 位的 Java，于是采用了 [Liberica JDK](https://bell-sw.com/pages/downloads/)。

运行前，需要在 java/bin 路径下（java.exe同路径下）放置 `jacob-1.18-x86.dll` 文件，以让 Java 可以通过 jacob 来调用 com。

`jacob-1.18-x86.dll` 文件可以在互联网处获取，但是可能文件名有差异。请使用 32 位的 dll 并且将文件名重命名为 `jacob-1.18-x86.dll` 即可（版本号基本无关）。本项目的 `其他文件` 下也有该文件，可以直接使用。

`jacob-1.18-x86.dll` 文件也可以从 [jacob-project](https://github.com/freemansoft/jacob-project/releases/tag/Root_B-1_20) 处下载，下载 `jacob-1.20.zip` 并解压，重命名 `jacob-1.20-x86.dll` 为 `jacob-1.18-x86.dll` 即可。
## 准备大漠插件环境
下载大漠插件 dll 文件 `dm.dll`。本程序使用的为最后一个免费的版本 3.1233。该 dll 只支持运行在 32 位的环境，发布于 2012 年。在 `dm.dll` 文件夹用管理员打开命令行，输入 `regsvr32 dm.dll` 完成大漠插件注册。
## 运行
打开工程，刷新 pom 依赖，运行即可。