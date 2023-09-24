let captchaApp = new Vue({
    el: '#captcha',
    data: {
        userConfig: {
            username: "",
            password: "",
            softId: "",
            typeId: "",
            captureInterval: "",
            timeout: "",
            captchaAppearDelay: "",
            keyPressAfterCaptchaShow: "",
            keyPressDelayAfterCaptchaDisappear: "",
            keyPressAfterCaptchaDisappear: "",
            pveFlopBonusAppearDelay: "",
            keyPressAfterPveFlopBonus: "",
            pveFlopBonusCapture: "",
            pveFlopBonusCaptureMosaic: "",
            pveFlopBonusDisappearDelay: "",
            keyPressAfterPveFlopBonusDisappear: "",
            logPrintInterval: "",
            detectNewWindowInterval: "",
            defaultChoiceAnswer: "",
            lowBalanceRemind: "",
            lowBalanceNum: "",
        },
        captchaConfig: {
            lowBalanceRemind: "",
            lowBalanceNum: "",
            captchaWay: [],
            tj: {},
            pc: {},
        },
        captchaWayTabIndex: 1,

        captureSampleChecked: "",
        totalCaptchaWayList: [],

        answerChoices: [
            {title: "A", option: "A"},
            {title: "B", option: "B"},
            {title: "C", option: "C"},
            {title: "D", option: "D"},
            {title: "", option: "无"},
        ],
        pcAuthorShow: false,
        showPcCami: false,
    },
    created() {
        this.initUserConfig();
        this.initCaptchaConfig();
    },
    methods: {
        initUserConfig: function () {
            let url = "config/get"
            axios.get(url).then((res) => {
                this.userConfig = new UserConfig(res.data.result);
            });
        },

        initCaptchaConfig: function () {
            let url = "captchaConfig/get"
            axios.get(url).then((res) => {
                this.captchaConfig = new CaptchaConfig(res.data.result);

                this.refreshTotalCaptchaWay();
            });
        },

        refreshTotalCaptchaWay() {
            this.totalCaptchaWayList = [new CaptchaWay().no()];
            this.captchaConfig.captchaInfoList.forEach(captchaInfo => {
                let captchaWay = new CaptchaWay();
                captchaWay.id = captchaInfo.id;
                captchaWay.captchaName = captchaInfo.captchaName;
                this.totalCaptchaWayList.push(captchaWay);
            });
            Vue.set(this.totalCaptchaWayList, 0, new CaptchaWay().no());
        },
        addCaptchaServer(tip, captchaType) {
            if (!confirm(tip)) {
                return;
            }

            let captchaInfoList = this.captchaConfig.captchaInfoList;
            let idList = [];
            for (let i = 0; i < captchaInfoList.length; i++) {
                const captchaInfo = captchaInfoList[i];
                idList.push(captchaInfo.id);
            }

            let newId;
            for (let i = 0; i < 200; i++) {
                newId = parseInt(Math.random() * 100000);
                if (!idList.includes(newId)) {
                    break;
                }
            }

            let name = prompt("给这个打码方式起个名字吧", "新建打码@" + newId);
            if (!name) {
                return;
            }

            let captchaWay = {};
            if (captchaType === 1) {
                captchaWay = new CaptchaTj();
            } else if (captchaType === 2) {
                captchaWay = new CaptchaPc();
            }
            captchaWay.id = newId;
            captchaWay.captchaName = name;
            captchaWay.captchaType = captchaType;

            captchaInfoList.push(captchaWay);
            this.refreshTotalCaptchaWay();
        },
        addTjCaptchaServer() {
            this.addCaptchaServer("是否新增一个图鉴打码服务器？", 1);
        },
        addPcCaptchaServer() {
            this.addCaptchaServer("是否新增一个平川类型的打码服务器？\n可以使用和平川打码相同格式的返回结果的打码服务器", 2);
        },
        deleteCaptcha(id) {
            if (!confirm("是否删除这个打码方式？")) {
                return;
            }

            let index = -1;
            let captchaInfoList = this.captchaConfig.captchaInfoList;
            for (let i = 0; i < captchaInfoList.length; i++) {
                const captchaInfo = captchaInfoList[i];
                if (captchaInfo.id === id) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                captchaInfoList.splice(index, 1);
            }
        },
        addValidTime(captchaInfo) {
            captchaInfo.validTimeList.push([[], [], [], []]);
        },
        deleteValidTime(captchaInfo, index) {
            captchaInfo.validTimeList.splice(index, 1);
        },
        changeCaptchaWayTabIndex: function (index) {
            this.captchaWayTabIndex = index;
        },
        updateCaptchaWaySetting: function () {
            let url = "captchaConfig/update"
            axios.post(url, this.captchaConfig.buildParamsAndReturn()).then(() => {
                showInfo("成功");
            });
        },
        resetCaptchaWaySetting: function () {
            if (!confirm("确定要重置？")) {
                return;
            }
            let url = "captchaConfig/reset";
            axios.get(url).then((res) => {
                this.captchaConfig = new CaptchaConfig(res.data.result);
                showInfo("成功");
            });
        },

        resetUserConfig: function () {
            if (!confirm("确定要重置？")) {
                return;
            }
            let url = "config/reset";
            axios.get(url).then((res) => {
                this.userConfig = new UserConfig(res.data.result);
                showInfo("成功");
            });
        },

        saveUserConfig: function () {
            let url = "config/update"
            axios.post(url, this.userConfig).then(() => {
                showInfo("成功");
            });
        },
        addServerAddrList(id) {
            let list = this.captchaConfig.captchaInfoList;
            if (!list) {
                return;
            }

            for (let i = 0; i < list.length; i++) {
                const item = list[i];
                if (item.id !== id) {
                    continue;
                }

                if (item.serverAddressList == null) {
                    item.serverAddressList = [];
                }
                item.serverAddressList.push("");
            }
        },
        deleteAddr(id, index) {
            let list = this.captchaConfig.captchaInfoList;
            if (!list) {
                return;
            }
            for (let i = 0; i < list.length; i++) {
                const item = list[i];
                if (item.id !== id) {
                    continue;
                }

                item.serverAddressList.splice(index, 1);
            }
        },
        bindAll: function () {
            let url = "captcha/addAllCaptcha"
            showInfo("正在检测窗口...")
            axios.get(url).then((res) => {
                let bindResultVo = new BindResultVo(res.data.result);
                if (bindResultVo == null) {
                    return;
                }

                showInfo(`新增线程数：${bindResultVo.newAddCount}，维持运行线程数：${bindResultVo.runningCount}`);
            });
        },
        unbindAll() {
            let url = "captcha/unbindAllCaptcha"
            axios.get(url).then((res) => {
                let bindResultVo = new BindResultVo(res.data.result);
                if (bindResultVo == null) {
                    return;
                }

                showInfo(`解绑线程数：${bindResultVo.newAddCount}`);
            });
        },
        testCaptcha(captchaInfoId) {
            let url = "captcha/test";
            showInfo("识别中，请稍后...");
            axios.get(url, {params: {captchaInfoId}}).then((res) => {
                let tjResponse = new TjResponse(res.data.result);
                if (tjResponse == null) {
                    showInfo("失败：接口返回内容为空");
                    return;
                }

                showInfo(tjResponse.info());
            });
        },
        showAccountInfo(captchaInfoId) {
            let url = "captcha/getAccountInfo";
            axios.get(url, {params: {captchaInfoId}}).then((res) => {
                let testRes = new TestRes(res.data.result);
                if (testRes == null) {
                    showInfo("失败：接口返回内容为空");
                    return;
                }

                showInfo(testRes.msg);
            });
        },
        changePcCamiShow: function () {
            this.showPcCami = !this.showPcCami;
        },

        captureCaptchaSampleRegion: function () {
            if (!this.captureSampleChecked) {
                showInfo("未勾选，不进行截图！")
                return;
            }
            let url = "captcha/captureCaptchaSampleRegion";
            axios.get(url).then((res) => {
                let ret = new StringRet(res.data.result);
                if (ret == null) {
                    showInfo("失败，程序没有返回任何内容");
                    return;
                }
                let success = ret.success;
                if (!success) {
                    showInfo("失败，" + ret.msg);
                    return;
                }

                showInfo(`截图成功，保存至程序运行目录的下述文件夹的图片文件\n请查看并且删除多余的或者不必要的以保证效率，重启程序生效。\n${ret.msg}`);
            });
        },
    }
});