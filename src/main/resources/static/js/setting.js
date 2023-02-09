let settingApp = new Vue({
    el: '#setting',
    data: {
        selectedTab: "按键设置",
        tabs: [
            {name: "按键设置"},
            {name: "邮箱设置"},
            {name: "登录设置"},
            {name: "更新设置"},
        ],
        settingConfig: {
            keyPadPressWay: "dm",
            email: {
                emailFrom: "",
                emailPassword: "",
                emailTo: "",
                hostName: "",
                allowRemoteConnect: false,
                remoteSenderAddr: "",
                enableRemoteSender: false,
                useRemoteLocalConfigFirst: false,
            },
            loginConfig: new LoginConfig(),
            updateConfig: new UpdateConfig(),
        },
        updateInfoVo: new UpdateInfoVo(),
        versionInfo: new MainVersionInfoVo(),
    },
    created() {
        this.get();
    },
    methods: {
        testEmailSend: function () {
            showInfo("邮件发送中...", 20);
            let url = "email/sendTestEmail";
            axios.get(url).then((res) => {
                showInfo("邮件已发送", 100);
            });
        },
        changeTab: function (tab) {
            this.selectedTab = tab.name;
        },
        update: function () {
            let url = "setting/update";
            axios.post(url, this.settingConfig).then(() => {
                showInfo("成功！");
            });
        },
        get: function () {
            let url = "setting/get";
            axios.get(url).then((res) => {
                this.settingConfig = new SettingConfig(res.data.result);
            });
        },
        getUpdateInfo: function () {
            let url = "setting/getUpdateInfoVo";
            this.updateInfoVo = new UpdateInfoVo();
            this.versionInfo = new MainVersionInfoVo();
            axios.get(url).then((res) => {
                let result = res.data.result;
                this.updateInfoVo = new UpdateInfoVo(result);
            });
        },
        getReadableFileSizeString(size) {
            return getReadableFileSizeString(size);
        },
        updateFile(id, versionId, index) {
            let url = "setting/updateFile";
            let params = {
                "params": {
                    id: id, versionId: versionId, index: index,
                }
            };

            showInfo("开始下载，请稍后...");
            axios.get(url, params).then((res) => {
                let downloadFileInfoVo = new DownloadFileInfoVo(res.data.result);
                showInfo(downloadFileInfoVo.info());
            });
        },
        showUpdateFiles(id) {
            this.versionInfo = new MainVersionInfoVo();
            this.updateInfoVo.versionInfoList.forEach(v => {
                if (v.id === id) {
                    this.versionInfo = v;
                }
            });
        },
    }
});