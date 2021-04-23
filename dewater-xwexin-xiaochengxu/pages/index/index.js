// index.js
// 获取应用实例
const app = getApp()

Page({
    data: {
        visable:false,
        link: '',//复制的抖音链接
        finalLink: '111',//无水印的下载链接地址
        userInfo: {},
        hasUserInfo: false,
        canIUse: wx.canIUse('button.open-type.getUserInfo'),
        canIUseGetUserProfile: false,
        canIUseOpenData: wx.canIUse('open-data.type.userAvatarUrl') && wx.canIUse('open-data.type.userNickName') // 如需尝试获取用户信息可改为false
    },
    // 事件处理函数
    bindViewTap() {
        wx.navigateTo({
            url: '../logs/logs'
        })
    },
    onLoad() {
        if (wx.getUserProfile) {
            this.setData({
                canIUseGetUserProfile: true
            })
        }
    },
    getUserProfile(e) {
        // 推荐使用wx.getUserProfile获取用户信息，开发者每次通过该接口获取用户个人信息均需用户确认，开发者妥善保管用户快速填写的头像昵称，避免重复弹窗
        wx.getUserProfile({
            desc: '展示用户信息', // 声明获取用户个人信息后的用途，后续会展示在弹窗中，请谨慎填写
            success: (res) => {
                this.setData({
                    userInfo: res.userInfo,
                    hasUserInfo: true
                })
            }
        })
    },
    getUserInfo(e) {
        this.setData({
            userInfo: e.detail.userInfo,
            hasUserInfo: true
        })
    },
    //复制链接
    pasteLink() {
        let then = this
        wx.getClipboardData({
            success(res) {
                then.setData({
                    link: res.data
                })
            }
        })

    },
    //绑定链接事件
    bindLink(e) {
        this.setData({
            link: e.detail.value
        })
    },
    //解析视频
    analysisVideo() {
        if (this.data.link == '') {
            wx.showToast({
                title: '请填写正确链接啊啊啊',
                icon: 'none',
                duration: 1500
            })
            return false
        }
        let then = this
        wx.showLoading({
            title: '解析ing',
          })
        wx.request({
            // url: 'http://localhost:9527/deWater',
            url: 'http://xiangcai.rongo.cc/deWater',
            method: 'POST',
            data: {
                link: this.data.link
            },
            head: {
                'content_type': 'application/json'//默认值
            },
            success: function (res) {
                //把返回成功的链接赋值给finalLink
                then.data.finalLink = res.data
                console.log(then.data.finalLink)
                //把下载按钮设为可见
                then.setData({
                    visable: true
                })
                wx.hideLoading()
                wx.showModal({
                    title: '解析成功',
                    content: '点击 下载视频 保存到手机 ',
                })
            },
            fail: function (res) {
                wx.hideLoading()
                wx.showToast({
                    title: '解析失败',
                    icon: 'fail',
                    duration: 2000
                })
            }
        })
    },

    //下载视频
    downVideo() {
        let then = this

        //可以通过wx.getSetting先查询一下用户是否授权了"scope.record"这个scope
        wx.getSetting({
            success(res) {
                if (!res.authSetting["scope.writePhotosAlbum"]) {
                    wx.authorize({
                        scope: "scope.writePhotosAlbum",
                        success() {
                            //用户已经同意小程序使用录音功能，后续调用接口不会弹窗询问
                            wx.showLoading({
                                title: '保存ing',
                              })
                            wx.downloadFile({
                                 url: then.data.finalLink,
                                // url: 'http://v95.douyinvod.com/ff2b87147d5dc80a8d1f3bf3340e3719/6081401e/video/n/tosedge-tos-aggdsz-ve-0000/2b8c0a0ea3db41f8b9c6d0cb5510e905/?a=1128&br=1348&bt=1348&btag=3&cd=0%7C0%7C0&ch=0&cr=0&cs=0&cv=1&dr=0&ds=3&er=&l=202104221621180102120660502C00BD11&lr=&mime_type=video_mp4&net=0&pl=0&qs=0&rc=M2g5bXdoazs1eTMzOWkzM0ApaGdlPDo8NmQ4N2VnZ2U5aWcpaGRqbGRoaGRmYXFmYjUtYjVwXy0tMi0vc3M0XjReYV5jMzMzNGIvYzA1OmNwb2wrbStqdDo%3D&vl=&vr=',
                                success(res) {
                                    let tempFilePath = res.tempFilePath
                                    wx.saveVideoToPhotosAlbum({
                                        filePath: tempFilePath,
                                        success(res) {
                                            wx.hideLoading()
                                            wx.showToast({
                                                title: '下载成功'
                                            })
                                        }
                                    })

                                },
                                fail: function (res) {
                                    wx.hideLoading()
                                    wx.showToast({
                                        title: '下载失败',
                                        icon: 'fail',
                                        duration: 2000
                                    })
                                }
                            })
                        },
                    });
                }else{
                    wx.showLoading({
                        title: '保存ing',
                      })
                    wx.downloadFile({
                        url: then.data.finalLink,
                       // url: 'http://v95.douyinvod.com/ff2b87147d5dc80a8d1f3bf3340e3719/6081401e/video/n/tosedge-tos-aggdsz-ve-0000/2b8c0a0ea3db41f8b9c6d0cb5510e905/?a=1128&br=1348&bt=1348&btag=3&cd=0%7C0%7C0&ch=0&cr=0&cs=0&cv=1&dr=0&ds=3&er=&l=202104221621180102120660502C00BD11&lr=&mime_type=video_mp4&net=0&pl=0&qs=0&rc=M2g5bXdoazs1eTMzOWkzM0ApaGdlPDo8NmQ4N2VnZ2U5aWcpaGRqbGRoaGRmYXFmYjUtYjVwXy0tMi0vc3M0XjReYV5jMzMzNGIvYzA1OmNwb2wrbStqdDo%3D&vl=&vr=',
                       success(res) {
                           let tempFilePath = res.tempFilePath
                           wx.saveVideoToPhotosAlbum({
                               filePath: tempFilePath,
                               success(res) {
                                wx.hideLoading()
                                   wx.showToast({
                                       title: '下载成功'
                                   })
                               }
                           })

                       },
                       fail: function (res) {
                        wx.hideLoading()
                           wx.showToast({
                               title: '下载失败',
                               icon: 'fail',
                               duration: 2000
                           })
                       }
                   })
                }
            },
        });
    },

    //复制无水印链接
    bindDeWaterLink() {
        wx.setClipboardData({
            data: this.data.finalLink,
            success(res) {
                wx.getClipboardData({
                    success(res) {

                    }
                })
            }
        })
    }
})
