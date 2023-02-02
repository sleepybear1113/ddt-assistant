let indexApp = new Vue({
    el: '#index',
    data: {
        appVersion: "",
        iframeIndex: 0,
        tabIndex: 0,
        hasLoginCookie: false,
    },
    created() {
        document.getElementById("ie").style.display = "none";
        document.getElementById("ie-a").innerText = "true";
        this.initApp();
        this.initLogoutButton();
    },
    methods: {
        initApp: function () {
            let url = "app/info"
            axios.get(url).then((res) => {
                let appInfoVo = new AppInfoVo(res.data.result);
                this.appVersion = appInfoVo.appVersion;
            });
        },
        initLogoutButton: function () {
            let cookie = document.cookie;
            if (cookie == null || cookie.length === 0) {
                this.hasLoginCookie = false;
                return;
            }
            if (cookie.indexOf("user=") >= 0) {
                this.hasLoginCookie = true;
            }
        },
    }
});