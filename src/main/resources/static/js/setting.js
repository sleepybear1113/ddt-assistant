let settingApp = new Vue({
    el: '#setting',
    data: {
        selectedTab: "按键设置",
        tabs: [
            {name: "按键设置"},
            {name: "邮箱设置"},
        ],
        settingConfig: {
            keyPadPressWay: "dm",
            email: {
                emailFrom: "",
                emailPassword: "",
                emailTo: "",
            }
        }
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
    }
});