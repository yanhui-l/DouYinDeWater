# DouYinDeWater
香菜自制抖音去水印工具demo：前端微信小程序，后端java
已上传至个人公众号-- <多放香菜> 欢迎食用

![](https://github.com/yanhui-l/img-folder/blob/master/dewater/xaingcai.jpg)

![](https://github.com/yanhui-l/img-folder/blob/master/dewater/dewater.jpg)


# 用法
提供两个接口

```shell script
http://host:port/dewater

json传参 ：{
    "link":"抖音复制的链接地址"
}
```
此接口，返回小程序可下载的链接地址，配合小程序 wx.downloadFile()保存视频到手机相册

```shell script
http://host:port/deWaterAndDown

json传参 ：{
    "link":"抖音复制的链接地址"
}
```
此接口，直接下载视频到电脑本机，返回本地保存地址（保存路径可自行设置）

# 原理分析

csdn[博客原文](https://blog.csdn.net/weixin_42182713/article/details/116062074)

