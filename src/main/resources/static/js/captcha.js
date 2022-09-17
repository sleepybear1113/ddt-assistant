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
            pveFlopBonusDisappearDelay: "",
            keyPressAfterPveFlopBonusDisappear: "",
            logPrintInterval: "",
            detectNewWindowInterval: "",
            extraPorts: "",
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

        answerChoices: [
            {title: "A", option: "A"},
            {title: "B", option: "B"},
            {title: "C", option: "C"},
            {title: "D", option: "D"},
            {title: "", option: "无"},
        ],
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
            });
        },

        changeCaptchaWayTabIndex: function (index) {
            this.captchaWayTabIndex = index;
        },
        updateCaptchaWaySetting: function () {
            let url = "captchaConfig/update"
            axios.post(url, this.captchaConfig).then(() => {
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
        testCaptcha: function (captchaWay) {
            let url = "captcha/test";
            showInfo("识别中，请稍后...");
            axios.get(url, {params: {captchaWay}}).then((res) => {
                let tjResponse = new TjResponse(res.data.result);
                if (tjResponse == null) {
                    showInfo("失败：接口返回内容为空");
                    return;
                }

                showInfo(tjResponse.info());
            });
        },
        showTjAccountInfo: function (way) {
            let url = "captcha/getTjAccountInfo";
            axios.get(url, {params: {way}}).then((res) => {
                let testRes = new TestRes(res.data.result);
                if (testRes == null) {
                    showInfo("失败：接口返回内容为空");
                    return;
                }

                showInfo(testRes.msg);
            });
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