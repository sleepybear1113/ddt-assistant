let settingApp = new Vue({
    el: '#setting',
    data: {
        settingConfig: {
            keyPadPressWay: "dm",
            apiPaoJiaoHost: ""
        }
    },
    created() {
        this.get();
    },
    methods: {
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