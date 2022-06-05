let indexApp = new Vue({
    el: '#index',
    data: {
        appVersion: "",
        iframeIndex: 0,
        tabIndex: 0,
    },
    created() {
        this.initApp();
    },
    methods: {
        initApp: function () {
            let url = "app/info"
            axios.get(url).then((res) => {
                let appInfoVo = new AppInfoVo(res.data.result);
                this.appVersion = appInfoVo.appVersion;
            });
        },
    }
});